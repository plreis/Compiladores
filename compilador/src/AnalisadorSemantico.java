import java.util.*;

public class AnalisadorSemantico extends GyhLangBaseListener {
    
    // Tabela de símbolos: nome -> tipo
    private Map<String, String> tabelaSimbolos = new HashMap<>();
    
    // Lista de erros semânticos
    private List<String> erros = new ArrayList<>();
    
    @Override
    public void enterDeclaracao(GyhLangParser.DeclaracaoContext ctx) {
        String nomeVar = ctx.Var().getText();
        String tipo = ctx.tipoVar().getText();
        
        // Verifica declaração duplicada
        if (tabelaSimbolos.containsKey(nomeVar)) {
            erros.add("Erro Semântico: Variável '" + nomeVar + "' já foi declarada anteriormente.");
        } else {
            tabelaSimbolos.put(nomeVar, tipo);
        }
    }
    
    @Override
    public void enterComandoAtribuicao(GyhLangParser.ComandoAtribuicaoContext ctx) {
        String nomeVar = ctx.Var().getText();
        
        // Verifica se variável foi declarada
        if (!tabelaSimbolos.containsKey(nomeVar)) {
            erros.add("Erro Semântico: Variável '" + nomeVar + "' não foi declarada.");
            return;
        }
        
        String tipoVar = tabelaSimbolos.get(nomeVar);
        String tipoExpressao = obterTipoExpressao(ctx.expressaoAritmetica());
        
        // Verifica compatibilidade de tipos
        if (!tiposCompativeis(tipoVar, tipoExpressao)) {
            erros.add("Erro Semântico: Tentativa de atribuir " + tipoExpressao + 
                     " à variável '" + nomeVar + "' do tipo " + tipoVar + ".");
        }
    }
    
    @Override
    public void enterComandoEntrada(GyhLangParser.ComandoEntradaContext ctx) {
        String nomeVar = ctx.Var().getText();
        
        if (!tabelaSimbolos.containsKey(nomeVar)) {
            erros.add("Erro Semântico: Variável '" + nomeVar + "' não foi declarada.");
        }
    }
    
    @Override
    public void enterComandoSaida(GyhLangParser.ComandoSaidaContext ctx) {
        if (ctx.Var() != null) {
            String nomeVar = ctx.Var().getText();
            if (!tabelaSimbolos.containsKey(nomeVar)) {
                erros.add("Erro Semântico: Variável '" + nomeVar + "' não foi declarada.");
            }
        }
    }
    
    @Override
    public void enterComandoCondicao(GyhLangParser.ComandoCondicaoContext ctx) {
        // Valida a expressão relacional
        validarExpressaoRelacional(ctx.expressaoRelacional());
    }
    
    @Override
    public void enterComandoRepeticao(GyhLangParser.ComandoRepeticaoContext ctx) {
        // Valida a expressão relacional
        validarExpressaoRelacional(ctx.expressaoRelacional());
    }
    
    /**
     * Valida se os tipos sendo comparados são compatíveis em uma expressão relacional
     */
    private void validarExpressaoRelacional(GyhLangParser.ExpressaoRelacionalContext ctx) {
        if (ctx == null) return;
        
        // Processa o termo relacional na expressão
        validarTermoRelacional(ctx.termoRelacional());
    }
    
    /**
     * Valida um termo relacional individual
     */
    private void validarTermoRelacional(GyhLangParser.TermoRelacionalContext ctx) {
        if (ctx == null) return;
        
        // Se tem duas expressões aritméticas separadas por operador relacional
        if (ctx.expressaoAritmetica().size() >= 2) {
            String tipoEsq = obterTipoExpressao(ctx.expressaoAritmetica(0));
            String tipoDir = obterTipoExpressao(ctx.expressaoAritmetica(1));
            
            // Verifica se um é STRING (não pode comparar STRING com números)
            if ("STRING".equals(tipoEsq) || "STRING".equals(tipoDir)) {
                erros.add("Erro Semântico: Não é permitido comparar tipos incompatíveis (STRING com números).");
            }
        }
    }

    private String obterTipoExpressao(GyhLangParser.ExpressaoAritmeticaContext ctx) {
        // Verifica se tem subexpressão (lado esquerdo)
        if (ctx.getRuleContext(GyhLangParser.ExpressaoAritmeticaContext.class, 0) != null) {
            String tipoEsq = obterTipoExpressao(ctx.getRuleContext(GyhLangParser.ExpressaoAritmeticaContext.class, 0));
            String tipoDir = obterTipoTermo(ctx.getRuleContext(GyhLangParser.TermoAritmeticoContext.class, 0));
            
            // REAL + qualquer coisa = REAL
            if ("REAL".equals(tipoEsq) || "REAL".equals(tipoDir)) {
                return "REAL";
            }
            return "INTEGER";
        }
        
        // Caso base: apenas um termo
        if (ctx.getRuleContext(GyhLangParser.TermoAritmeticoContext.class, 0) != null) {
            return obterTipoTermo(ctx.getRuleContext(GyhLangParser.TermoAritmeticoContext.class, 0));
        }
        
        return "INTEGER";
    }
    
    private String obterTipoTermo(GyhLangParser.TermoAritmeticoContext ctx) {
        // Verifica se tem subtermo (lado esquerdo)
        if (ctx.getRuleContext(GyhLangParser.TermoAritmeticoContext.class, 0) != null) {
            String tipoEsq = obterTipoTermo(ctx.getRuleContext(GyhLangParser.TermoAritmeticoContext.class, 0));
            String tipoDir = obterTipoFator(ctx.getRuleContext(GyhLangParser.FatorAritmeticoContext.class, 0));
            
            if ("REAL".equals(tipoEsq) || "REAL".equals(tipoDir)) {
                return "REAL";
            }
            return "INTEGER";
        }
        
        // Caso base: apenas um fator
        if (ctx.getRuleContext(GyhLangParser.FatorAritmeticoContext.class, 0) != null) {
            return obterTipoFator(ctx.getRuleContext(GyhLangParser.FatorAritmeticoContext.class, 0));
        }
        
        return "INTEGER";
    }
    
    private String obterTipoFator(GyhLangParser.FatorAritmeticoContext ctx) {
        if (ctx.NumReal() != null) {
            return "REAL";
        }
        if (ctx.NumInt() != null) {
            return "INTEGER";
        }
        if (ctx.String() != null) {
            // String não pode ser usada em expressão aritmética
            erros.add("Erro Semântico: Cadeia de caracteres (String) não pode ser usada em expressão aritmética.");
            return "STRING"; // Tipo inválido para aritmética
        }
        if (ctx.Var() != null) {
            String nomeVar = ctx.Var().getText();
            if (tabelaSimbolos.containsKey(nomeVar)) {
                return tabelaSimbolos.get(nomeVar);
            }
            // Variável não foi declarada - reporta erro
            erros.add("Erro Semântico: Variável '" + nomeVar + "' não foi declarada.");
            return "INTEGER"; // Default
        }
        if (ctx.getRuleContext(GyhLangParser.ExpressaoAritmeticaContext.class, 0) != null) {
            return obterTipoExpressao(ctx.getRuleContext(GyhLangParser.ExpressaoAritmeticaContext.class, 0));
        }
        return "INTEGER";
    }
    
    private boolean tiposCompativeis(String tipoVar, String tipoExpressao) {
        // REAL pode receber INTEGER ou REAL
        if ("REAL".equals(tipoVar)) {
            return true;
        }
        // INTEGER só pode receber INTEGER
        return "INTEGER".equals(tipoVar) && "INTEGER".equals(tipoExpressao);
    }
    
    public List<String> getErros() {
        return erros;
    }
    
    public boolean temErros() {
        return !erros.isEmpty();
    }
    
    public void imprimirTabelaSimbolos() {
        System.out.println("\n========== TABELA DE SÍMBOLOS ==========");
        System.out.printf("%-20s | %-10s\n", "VARIÁVEL", "TIPO");
        System.out.println("----------------------------------------");
        for (Map.Entry<String, String> entry : tabelaSimbolos.entrySet()) {
            System.out.printf("%-20s | %-10s\n", entry.getKey(), entry.getValue());
        }
        System.out.println("========================================\n");
    }
    
    public Map<String, String> getTabelaSimbolos() {
        return tabelaSimbolos;
    }
}
