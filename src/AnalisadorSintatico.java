import java.util.Set;
import java.util.EnumSet;

/**
 * Analisador Sintático Preditivo Recursivo Descendente para a gramática GYH.
 */
public class AnalisadorSintatico {

    private AnalisadorLexico alex;
    private Token currentToken;

    // Conjunto de tokens que podem iniciar um comando, usado para laços
    private final Set<TipoToken> firstOfComando = EnumSet.of(
        TipoToken.Var, 
        TipoToken.PCLer, 
        TipoToken.PCImprimir,
        TipoToken.PCSe, 
        TipoToken.PCEnqto, 
        TipoToken.PCIni
    );

    // Conjunto de tokens que são operadores relacionais
    private final Set<TipoToken> operadoresRelacionais = EnumSet.of(
        TipoToken.OpRelMenor, 
        TipoToken.OpRelMenorIgual, 
        TipoToken.OpRelMaior,
        TipoToken.OpRelMaiorIgual, 
        TipoToken.OpRelIgual, 
        TipoToken.OpRelDif
    );

    public AnalisadorSintatico(AnalisadorLexico alex) throws Exception {
        this.alex = alex;
        this.currentToken = alex.proxToken(); // Inicializa o primeiro token
    }

    /**
     * Consome o token atual se for do tipo esperado.
     * Lança uma exceção se o token atual for diferente do esperado.
     * @param expectedType O TipoToken esperado.
     * @throws Exception Erro de sintaxe.
     */
    private void eat(TipoToken expectedType) throws Exception {
        if (currentToken.getPadrao() == expectedType) {
            // System.out.println("Consumed: " + currentToken); // Para depuração
            currentToken = alex.proxToken();
        } else {
            throw new Exception(
                String.format("Erro de Sintaxe na linha %d, coluna %d: Esperado token '%s', mas encontrou '%s' (Lexema: '%s')",
                    currentToken.getLinha(), currentToken.getColuna(),
                    expectedType, currentToken.getPadrao(), currentToken.getLexema())
            );
        }
    }

    /**
     * Inicia a análise sintática.
     */
    public void parse() {
        try {
            programa(); // Símbolo inicial da gramática
            
            if (currentToken.getPadrao() != TipoToken.EOF) {
                // Se não chegou ao EOF, há "lixo" no final do arquivo
                throw new Exception(
                    String.format("Erro de Sintaxe na linha %d, coluna %d: Código inesperado após o final do programa, iniciando com '%s'",
                        currentToken.getLinha(), currentToken.getColuna(), currentToken.getLexema())
                );
            }
            System.out.println("Análise Sintática concluída com sucesso!");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // --- Métodos para cada regra da gramática ---

    // Programa → '[' 'DECLARAR'']' ListaDeclaracoes '[' 'PROGRAMA'']' ListaComandos;
    private void programa() throws Exception {
        eat(TipoToken.DelimAbre);
        eat(TipoToken.PCDec);
        eat(TipoToken.DelimFecha);
        listaDeclaracoes();
        eat(TipoToken.DelimAbre);
        eat(TipoToken.PCProg);
        eat(TipoToken.DelimFecha);
        listaComandos();
    }

    // ListaDeclaracoes → Declaracao ListaDeclaracoes | Declaracao;
    // Implementado como: (Declaracao)*
    private void listaDeclaracoes() throws Exception {
        while (currentToken.getPadrao() == TipoToken.Var) {
            declaracao();
        }
    }

    // Declaracao → VARIAVEL ':' TipoVar;
    private void declaracao() throws Exception {
        eat(TipoToken.Var);
        eat(TipoToken.DelimDoisPontos);
        tipoVar();
    }

    // TipoVar → 'INTEGER' | 'REAL';
    private void tipoVar() throws Exception {
        if (currentToken.getPadrao() == TipoToken.PCInt) {
            eat(TipoToken.PCInt);
        } else if (currentToken.getPadrao() == TipoToken.PCReal) {
            eat(TipoToken.PCReal);
        } else {
            throw new Exception(String.format("Erro de Sintaxe na linha %d: Esperado 'INTEGER' ou 'REAL', mas encontrou '%s'",
                currentToken.getLinha(), currentToken.getPadrao()));
        }
    }

    // ListaComandos → Comando ListaComandos | Comando;
    // Implementado como: (Comando)*
    private void listaComandos() throws Exception {
        // Usa o conjunto 'firstOfComando' para decidir se há mais comandos
        while (firstOfComando.contains(currentToken.getPadrao())) {
            comando();
        }
    }

    // Comando → ComandoAtribuicao | ComandoEntrada | ComandoSaida | ComandoCondicao | ComandoRepeticao | SubAlgoritmo;
    private void comando() throws Exception {
        switch (currentToken.getPadrao()) {
            case Var:
                comandoAtribuicao();
                break;
            case PCLer:
                comandoEntrada();
                break;
            case PCImprimir:
                comandoSaida();
                break;
            case PCSe:
                comandoCondicao();
                break;
            case PCEnqto:
                comandoRepeticao();
                break;
            case PCIni:
                subAlgoritmo();
                break;
            default:
                throw new Exception(String.format("Erro de Sintaxe na linha %d: Comando inválido. '%s' não inicia um comando.",
                    currentToken.getLinha(), currentToken.getLexema()));
        }
    }

    // ComandoAtribuicao → VARIAVEL ':=' ExpressaoAritmetica;
    private void comandoAtribuicao() throws Exception {
        eat(TipoToken.Var);
        eat(TipoToken.Atrib);
        expressaoAritmetica();
    }

    // ComandoEntrada → 'LER' VARIAVEL;
    private void comandoEntrada() throws Exception {
        eat(TipoToken.PCLer);
        eat(TipoToken.Var);
    }

    // ComandoSaida → 'IMPRIMIR' VARIAVEL | 'IMPRIMIR' CADEIA;
    private void comandoSaida() throws Exception {
        eat(TipoToken.PCImprimir);
        if (currentToken.getPadrao() == TipoToken.Var) {
            eat(TipoToken.Var);
        } else if (currentToken.getPadrao() == TipoToken.Cadeia) {
            eat(TipoToken.Cadeia);
        } else {
            throw new Exception(String.format("Erro de Sintaxe na linha %d: Argumento inválido para 'IMPRIMIR'. Esperado VARIAVEL ou CADEIA, encontrou '%s'",
                currentToken.getLinha(), currentToken.getPadrao()));
        }
    }

    // ComandoCondicao → 'SE' ExpressaoRelacional 'ENTAO' Comando | 'SE' ExpressaoRelacional 'ENTAO' Comando 'SENAO' Comando;
    private void comandoCondicao() throws Exception {
        eat(TipoToken.PCSe);
        expressaoRelacional();
        eat(TipoToken.PCEntao);
        comando();
        // Parte opcional 'SENAO'
        if (currentToken.getPadrao() == TipoToken.PCSenao) {
            eat(TipoToken.PCSenao);
            comando();
        }
    }

    // ComandoRepeticao → 'ENQTO' ExpressaoRelacional Comando;
    private void comandoRepeticao() throws Exception {
        eat(TipoToken.PCEnqto);
        expressaoRelacional();
        comando();
    }

    // SubAlgoritmo → 'INICIO' ListaComandos 'FINAL';
    private void subAlgoritmo() throws Exception {
        eat(TipoToken.PCIni);
        listaComandos();
        eat(TipoToken.PCFim);
    }

    // --- Regras de Expressão (com eliminação de recursão à esquerda) ---

    // ExpressaoAritmetica → TermoAritmetico ExpressaoAritmetica'
    private void expressaoAritmetica() throws Exception {
        termoAritmetico();
        expressaoAritmeticaPrime();
    }

    // ExpressaoAritmetica' → '+' TermoAritmetico ExpressaoAritmetica' | '-' TermoAritmetico ExpressaoAritmetica' | ε
    private void expressaoAritmeticaPrime() throws Exception {
        while (currentToken.getPadrao() == TipoToken.OpAritSoma || currentToken.getPadrao() == TipoToken.OpAritSub) {
            if (currentToken.getPadrao() == TipoToken.OpAritSoma) {
                eat(TipoToken.OpAritSoma);
                termoAritmetico();
            } else if (currentToken.getPadrao() == TipoToken.OpAritSub) {
                eat(TipoToken.OpAritSub);
                termoAritmetico();
            }
        }
        // Se não for '+' ou '-', é a produção vazia (ε), então não fazemos nada.
    }

    // TermoAritmetico → FatorAritmetico TermoAritmetico'
    private void termoAritmetico() throws Exception {
        fatorAritmetico();
        termoAritmeticoPrime();
    }

    // TermoAritmetico' → '*' FatorAritmetico TermoAritmetico' | '/' FatorAritmetico TermoAritmetico' | ε
    private void termoAritmeticoPrime() throws Exception {
        while (currentToken.getPadrao() == TipoToken.OpAritMult || currentToken.getPadrao() == TipoToken.OpAritDiv) {
            if (currentToken.getPadrao() == TipoToken.OpAritMult) {
                eat(TipoToken.OpAritMult);
                fatorAritmetico();
            } else if (currentToken.getPadrao() == TipoToken.OpAritDiv) {
                eat(TipoToken.OpAritDiv);
                fatorAritmetico();
            }
        }
        // Produção vazia (ε)
    }

    // FatorAritmetico → NUMINT | NUMREAL | VARIAVEL | '(' ExpressaoAritmetica ')'
    private void fatorAritmetico() throws Exception {
        switch (currentToken.getPadrao()) {
            case NumInt:
                eat(TipoToken.NumInt);
                break;
            case NumReal:
                eat(TipoToken.NumReal);
                break;
            case Var:
                eat(TipoToken.Var);
                break;
            case AbrePar:
                eat(TipoToken.AbrePar);
                expressaoAritmetica();
                eat(TipoToken.FechaPar);
                break;
            default:
                throw new Exception(String.format("Erro de Sintaxe na linha %d: Fator aritmético inválido. Esperado Número, Variável ou '(', encontrou '%s'",
                    currentToken.getLinha(), currentToken.getPadrao()));
        }
    }

    // ExpressaoRelacional → TermoRelacional ExpressaoRelacional'
    private void expressaoRelacional() throws Exception {
        termoRelacional();
        expressaoRelacionalPrime();
    }

    // ExpressaoRelacional' → OperadorBooleano TermoRelacional ExpressaoRelacional' | ε
    private void expressaoRelacionalPrime() throws Exception {
        while (currentToken.getPadrao() == TipoToken.OpBoolE || currentToken.getPadrao() == TipoToken.OpBoolOu) {
            operadorBooleano();
            termoRelacional();
        }
        // Produção vazia (ε)
    }

    // TermoRelacional → ExpressaoAritmetica OP_REL ExpressaoAritmetica | '(' ExpressaoRelacional ')';
    private void termoRelacional() throws Exception {
        if (currentToken.getPadrao() == TipoToken.AbrePar) {
            eat(TipoToken.AbrePar);
            expressaoRelacional();
            eat(TipoToken.FechaPar);
        } else {
            // Se não começa com '(', então deve ser uma ExpressaoAritmetica
            expressaoAritmetica();
            operadorRelacional(); // consome o operador relacional
            expressaoAritmetica();
        }
    }

    // OperadorBooleano → 'E' | 'OU';
    private void operadorBooleano() throws Exception {
        if (currentToken.getPadrao() == TipoToken.OpBoolE) {
            eat(TipoToken.OpBoolE);
        } else if (currentToken.getPadrao() == TipoToken.OpBoolOu) {
            eat(TipoToken.OpBoolOu);
        } else {
            throw new Exception(String.format("Erro de Sintaxe na linha %d: Operador booleano esperado ('E' ou 'OU'), encontrou '%s'",
                currentToken.getLinha(), currentToken.getPadrao()));
        }
    }

    // Método auxiliar para consumir qualquer um dos 6 operadores relacionais
    private void operadorRelacional() throws Exception {
        if (operadoresRelacionais.contains(currentToken.getPadrao())) {
            eat(currentToken.getPadrao()); // Consome o operador que encontrou
        } else {
            throw new Exception(String.format("Erro de Sintaxe na linha %d: Operador relacional esperado (ex: '==', '>', '<='), encontrou '%s'",
                currentToken.getLinha(), currentToken.getPadrao()));
        }
    }
}



