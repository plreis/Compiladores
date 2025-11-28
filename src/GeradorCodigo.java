import java.util.*;

public class GeradorCodigo extends GyhLangBaseListener {
    
    private Map<String, String> tabelaSimbolos;
    private List<Command> programa;
    private Stack<List<Command>> pilhaComandos;
    
    // Pilhas para controlar estruturas aninhadas
    private Stack<CommandCondicao> pilhaCondicoes;
    private Stack<CommandRepeticao> pilhaRepeticoes;
    private Stack<Boolean> pilhaDentroENTAO; // Controla se estamos no ENTAO ou SENAO
    
    public GeradorCodigo(Map<String, String> tabelaSimbolos) {
        this.tabelaSimbolos = tabelaSimbolos;
        this.programa = new ArrayList<>();
        this.pilhaComandos = new Stack<>();
        this.pilhaComandos.push(programa);
        this.pilhaCondicoes = new Stack<>();
        this.pilhaRepeticoes = new Stack<>();
        this.pilhaDentroENTAO = new Stack<>();
    }
    
    @Override
    public void enterComandoAtribuicao(GyhLangParser.ComandoAtribuicaoContext ctx) {
        String variavel = ctx.Var().getText();
        String expressao = ctx.expressaoAritmetica().getText();
        
        CommandAtribuicao cmd = new CommandAtribuicao(variavel, expressao);
        pilhaComandos.peek().add(cmd);
    }
    
    @Override
    public void enterComandoEntrada(GyhLangParser.ComandoEntradaContext ctx) {
        String variavel = ctx.Var().getText();
        String tipoVar = tabelaSimbolos.get(variavel);
        
        CommandEntrada cmd = new CommandEntrada(variavel, tipoVar);
        pilhaComandos.peek().add(cmd);
    }
    
    @Override
    public void enterComandoSaida(GyhLangParser.ComandoSaidaContext ctx) {
        String conteudo;
        boolean isString = false;
        String tipo = "INTEGER";
        
        if (ctx.Var() != null) {
            conteudo = ctx.Var().getText();
            tipo = tabelaSimbolos.get(conteudo);
        } else {
            conteudo = ctx.String().getText();
            isString = true;
        }
        
        CommandSaida cmd = new CommandSaida(conteudo, isString, tipo);
        pilhaComandos.peek().add(cmd);
    }
    
    @Override
    public void enterComandoCondicao(GyhLangParser.ComandoCondicaoContext ctx) {
        String condicao = processarExpressaoRelacional(ctx.expressaoRelacional());
        
        List<Command> comandosEntao = new ArrayList<>();
        List<Command> comandosSenao = null;
        
        // Se tem SENAO, cria lista para os comandos do SENAO
        if (ctx.SENAO() != null) {
            comandosSenao = new ArrayList<>();
        }
        
        CommandCondicao cmd = new CommandCondicao(condicao, comandosEntao, comandosSenao);
        pilhaCondicoes.push(cmd);
        
        // Marca que estamos processando o ENTAO
        pilhaDentroENTAO.push(true);
        
        // Empilha lista de comandos do ENTAO
        pilhaComandos.push(comandosEntao);
    }
    
    @Override
    public void exitComando(GyhLangParser.ComandoContext ctx) {
        // Verifica se acabou de processar o comando do ENTAO e tem SENAO
        if (!pilhaDentroENTAO.isEmpty() && pilhaDentroENTAO.peek() && 
            ctx.getParent() instanceof GyhLangParser.ComandoCondicaoContext) {
            
            GyhLangParser.ComandoCondicaoContext condicaoCtx = 
                (GyhLangParser.ComandoCondicaoContext) ctx.getParent();
            
            // Se tem SENAO e estávamos no ENTAO, muda para processar SENAO
            if (condicaoCtx.SENAO() != null && ctx == condicaoCtx.comando(0)) {
                pilhaDentroENTAO.pop();
                pilhaDentroENTAO.push(false);
                
                // Desempilha comandos do ENTAO
                pilhaComandos.pop();
                
                // Empilha comandos do SENAO
                CommandCondicao cmd = pilhaCondicoes.peek();
                cmd.setComandosSenao(new ArrayList<>());
                pilhaComandos.push(cmd.getComandosSenao());
            }
        }
    }
    
    @Override
    public void exitComandoCondicao(GyhLangParser.ComandoCondicaoContext ctx) {
        // Desempilha lista de comandos (ENTAO ou SENAO)
        pilhaComandos.pop();
        
        // Remove marcador de ENTAO/SENAO
        if (!pilhaDentroENTAO.isEmpty()) {
            pilhaDentroENTAO.pop();
        }
        
        // Adiciona o comando condicional completo ao escopo atual
        CommandCondicao cmd = pilhaCondicoes.pop();
        pilhaComandos.peek().add(cmd);
    }
    
    @Override
    public void enterComandoRepeticao(GyhLangParser.ComandoRepeticaoContext ctx) {
        String condicao = processarExpressaoRelacional(ctx.expressaoRelacional());
        List<Command> comandos = new ArrayList<>();
        
        CommandRepeticao cmd = new CommandRepeticao(condicao, comandos);
        pilhaRepeticoes.push(cmd);
        
        // Empilha lista de comandos do ENQTO
        pilhaComandos.push(comandos);
    }
    
    @Override
    public void exitComandoRepeticao(GyhLangParser.ComandoRepeticaoContext ctx) {
        // Desempilha lista de comandos
        pilhaComandos.pop();
        
        // Adiciona o comando de repetição completo ao escopo atual
        CommandRepeticao cmd = pilhaRepeticoes.pop();
        pilhaComandos.peek().add(cmd);
    }
    
    @Override
    public void enterSubAlgoritmo(GyhLangParser.SubAlgoritmoContext ctx) {
        // INICIO...FINAL: os comandos já vão para o escopo atual
        // Não precisa criar novo escopo pois já estamos dentro do ENQTO
    }
    
    private String processarExpressaoRelacional(GyhLangParser.ExpressaoRelacionalContext ctx) {
        String expressao = ctx.getText();
        
        // Substitui operadores booleanos para C
        expressao = expressao.replaceAll("\\bE\\b", "&&");
        expressao = expressao.replaceAll("\\bOU\\b", "||");
        
        return expressao;
    }
    
    public String gerarCodigoC() {
        StringBuilder codigo = new StringBuilder();
        
        codigo.append("#include <stdio.h>\n");
        codigo.append("#include <stdlib.h>\n\n");
        codigo.append("int main() {\n");
        
        // Declaração de variáveis
        for (Map.Entry<String, String> entry : tabelaSimbolos.entrySet()) {
            String nomeVar = entry.getKey();
            String tipo = entry.getValue();
            
            String tipoC = tipo.equals("INTEGER") ? "int" : "double";
            codigo.append("    ").append(tipoC).append(" ").append(nomeVar).append(";\n");
        }
        
        if (!tabelaSimbolos.isEmpty()) {
            codigo.append("\n");
        }
        
        // Comandos do programa
        for (Command cmd : programa) {
            String codigoCmd = cmd.generateCode();
            String[] linhas = codigoCmd.split("\n");
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    codigo.append("    ").append(linha).append("\n");
                }
            }
        }
        
        codigo.append("\n    return 0;\n");
        codigo.append("}\n");
        
        return codigo.toString();
    }
    
    public List<Command> getPrograma() {
        return programa;
    }
}
