import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestadorGyhLang {
    
    private static final String PASTA_TESTES = "src/Testess";
    private List<ResultadoTeste> resultados;
    
    public TestadorGyhLang() {
        this.resultados = new ArrayList<>();
    }
    
    public static void main(String[] args) throws Exception {
        TestadorGyhLang testador = new TestadorGyhLang();
        testador.executarTodosTestes();
        testador.exibirRelatorio();
    }
    
    /**
     * Executa todos os testes encontrados em src/Testess
     */
    public void executarTodosTestes() {
        File pastaTestess = new File(PASTA_TESTES);
        
        if (!pastaTestess.exists()) {
            System.out.println("Erro: Pasta " + PASTA_TESTES + " não encontrada!");
            return;
        }
        
        File[] arquivos = pastaTestess.listFiles((dir, name) -> name.endsWith(".gyh"));
        
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo .gyh encontrado em " + PASTA_TESTES);
            return;
        }
        
        // Ordena os arquivos alfabeticamente
        Arrays.sort(arquivos);
        
        System.out.println("========== TESTADOR GYHLANG ==========\n");
        System.out.println("Encontrados " + arquivos.length + " arquivos de teste.\n");
        System.out.println("Iniciando execução dos testes...\n");
        System.out.println("=====================================\n");
        
        for (File arquivo : arquivos) {
            executarTeste(arquivo);
        }
    }
    
    /**
     * Executa um teste individual
     */
    private void executarTeste(File arquivo) {
        String nomeArquivo = arquivo.getName();
        System.out.println("▶ Testando: " + nomeArquivo);
        
        ResultadoTeste resultado = new ResultadoTeste(nomeArquivo);
        
        try {
            // Lê o arquivo
            CharStream input = CharStreams.fromPath(Paths.get(arquivo.getAbsolutePath()));
            
            // ========== ANÁLISE LÉXICA ==========
            GyhLangLexer lexer = new GyhLangLexer(input);
            lexer.removeErrorListeners();
            GyhLangErrorListener errorLexer = new GyhLangErrorListener();
            lexer.addErrorListener(errorLexer);
            
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            tokens.fill();
            
            if (errorLexer.temErros()) {
                resultado.setResultadoLexico("❌ FALHOU");
                resultado.setErroLexico(true);
                resultado.setStatusFinal("ERRO LÉXICO");
            } else {
                resultado.setResultadoLexico("✓ OK");
                
                // ========== ANÁLISE SINTÁTICA ==========
                GyhLangParser parser = new GyhLangParser(tokens);
                parser.removeErrorListeners();
                GyhLangErrorListener errorParser = new GyhLangErrorListener();
                parser.addErrorListener(errorParser);
                
                ParseTree tree = parser.programa();
                
                if (parser.getNumberOfSyntaxErrors() > 0) {
                    resultado.setResultadoSintatico("❌ FALHOU");
                    resultado.setErroSintatico(true);
                    resultado.setStatusFinal("ERRO SINTÁTICO");
                } else {
                    resultado.setResultadoSintatico("✓ OK");
                    
                    // ========== ANÁLISE SEMÂNTICA ==========
                    AnalisadorSemantico analisador = new AnalisadorSemantico();
                    ParseTreeWalker walker = new ParseTreeWalker();
                    walker.walk(analisador, tree);
                    
                    if (analisador.temErros()) {
                        resultado.setResultadoSemantico("❌ FALHOU");
                        resultado.setErroSemantico(true);
                        resultado.setErrosSemantcos(analisador.getErros());
                        resultado.setStatusFinal("ERRO SEMÂNTICO");
                    } else {
                        resultado.setResultadoSemantico("✓ OK");
                        
                        // ========== GERAÇÃO DE CÓDIGO ==========
                        try {
                            GeradorCodigo gerador = new GeradorCodigo(analisador.getTabelaSimbolos());
                            ParseTreeWalker walker2 = new ParseTreeWalker();
                            walker2.walk(gerador, tree);
                            
                            String codigoC = gerador.gerarCodigoC();
                            System.out.println(codigoC);
                            resultado.setResultadoGeracao("✓ OK");
                            resultado.setStatusFinal("SUCESSO");
                        } catch (Exception e) {
                            resultado.setResultadoGeracao("❌ FALHOU");
                            resultado.setStatusFinal("ERRO NA GERAÇÃO");
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            resultado.setStatusFinal("ERRO: " + e.getMessage());
            resultado.setResultadoLexico("❌ ERRO");
        }
        
        // Exibe resultado do teste
        exibirResultadoTeste(resultado);
        resultados.add(resultado);
    }
    
    /**
     * Exibe o resultado de um teste individual
     */
    private void exibirResultadoTeste(ResultadoTeste resultado) {
        System.out.println("  Léxica:     " + resultado.getResultadoLexico());
        System.out.println("  Sintática:  " + resultado.getResultadoSintatico());
        System.out.println("  Semântica:  " + resultado.getResultadoSemantico());
        System.out.println("  Geração:    " + resultado.getResultadoGeracao());
        System.out.println("  Status:     " + resultado.getStatusFinal());
        
        if (resultado.temErrosSemantcos()) {
            System.out.println("  Erros:");
            for (String erro : resultado.getErrosSemantcos()) {
                System.out.println("    - " + erro);
            }
        }
        
        System.out.println();
    }
    
    /**
     * Exibe o relatório final com estatísticas
     */
    public void exibirRelatorio() {
        System.out.println("=====================================\n");
        System.out.println("========== RELATÓRIO FINAL ==========\n");
        
        int totalTestes = resultados.size();
        int sucessos = 0;
        int falhasLexico = 0;
        int falhasSintatico = 0;
        int falhasSemantico = 0;
        int falhasGeracao = 0;
        
        // Conta os resultados
        for (ResultadoTeste resultado : resultados) {
            if (resultado.getStatusFinal().equals("SUCESSO")) {
                sucessos++;
            } else if (resultado.isErroLexico()) {
                falhasLexico++;
            } else if (resultado.isErroSintatico()) {
                falhasSintatico++;
            } else if (resultado.isErroSemantico()) {
                falhasSemantico++;
            } else {
                falhasGeracao++;
            }
        }
        
        // Exibe estatísticas
        System.out.println("Total de Testes:        " + totalTestes);
        System.out.println("Sucessos:               " + sucessos + " (" + formatarPercentual(sucessos, totalTestes) + ")");
        System.out.println("Falhas Léxicas:         " + falhasLexico);
        System.out.println("Falhas Sintáticas:      " + falhasSintatico);
        System.out.println("Falhas Semânticas:      " + falhasSemantico);
        System.out.println("Falhas na Geração:      " + falhasGeracao);
        
        System.out.println("\n========== DETALHES DOS TESTES ==========\n");
        
        // Exibe detalhes de cada teste
        for (ResultadoTeste resultado : resultados) {
            String icone = resultado.getStatusFinal().equals("SUCESSO") ? "✓" : "✗";
            System.out.println(String.format("%s %-15s → %s", icone, resultado.getNomeArquivo(), 
                             resultado.getStatusFinal()));
        }
        
        System.out.println("\n=====================================\n");
    }
    
    /**
     * Formata um percentual
     */
    private String formatarPercentual(int parte, int total) {
        if (total == 0) return "0%";
        return ((parte * 100) / total) + "%";
    }
    
    /**
     * Classe interna para armazenar resultado de um teste
     */
    private static class ResultadoTeste {
        private String nomeArquivo;
        private String resultadoLexico;
        private String resultadoSintatico;
        private String resultadoSemantico;
        private String resultadoGeracao;
        private String statusFinal;
        private boolean erroLexico;
        private boolean erroSintaxico;
        private boolean erroSemantico;
        private List<String> errosSemantcos;
        
        public ResultadoTeste(String nomeArquivo) {
            this.nomeArquivo = nomeArquivo;
            this.resultadoLexico = "⏳ PENDENTE";
            this.resultadoSintatico = "⏳ PENDENTE";
            this.resultadoSemantico = "⏳ PENDENTE";
            this.resultadoGeracao = "⏳ PENDENTE";
            this.statusFinal = "PENDENTE";
            this.errosSemantcos = new ArrayList<>();
        }
        
        // Getters e Setters
        public String getNomeArquivo() { return nomeArquivo; }
        public String getResultadoLexico() { return resultadoLexico; }
        public void setResultadoLexico(String valor) { this.resultadoLexico = valor; }
        public String getResultadoSintatico() { return resultadoSintatico; }
        public void setResultadoSintatico(String valor) { this.resultadoSintatico = valor; }
        public String getResultadoSemantico() { return resultadoSemantico; }
        public void setResultadoSemantico(String valor) { this.resultadoSemantico = valor; }
        public String getResultadoGeracao() { return resultadoGeracao; }
        public void setResultadoGeracao(String valor) { this.resultadoGeracao = valor; }
        public String getStatusFinal() { return statusFinal; }
        public void setStatusFinal(String valor) { this.statusFinal = valor; }
        public boolean isErroLexico() { return erroLexico; }
        public void setErroLexico(boolean valor) { this.erroLexico = valor; }
        public boolean isErroSintatico() { return erroSintaxico; }
        public void setErroSintatico(boolean valor) { this.erroSintaxico = valor; }
        public boolean isErroSemantico() { return erroSemantico; }
        public void setErroSemantico(boolean valor) { this.erroSemantico = valor; }
        public List<String> getErrosSemantcos() { return errosSemantcos; }
        public void setErrosSemantcos(List<String> erros) { this.errosSemantcos = erros; }
        public boolean temErrosSemantcos() { return !errosSemantcos.isEmpty(); }
    }
}
