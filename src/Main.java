import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class Main {

	public static void main(String[] args) throws Exception {
		
		System.out.println("========== ANALISADOR GYH ==========\n");
		
		// Lê o arquivo de entrada
		CharStream input = CharStreams.fromFileName("src/Testess/L1.gyh");
		
		// ========== ANÁLISE LÉXICA ==========
		System.out.println("Iniciando análise léxica...");
		GyhLangLexer lexer = new GyhLangLexer(input);
		
		// Remove listeners padrão para usar o customizado
		lexer.removeErrorListeners();
		GyhLangErrorListener errorLexer = new GyhLangErrorListener();
		lexer.addErrorListener(errorLexer);
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill(); // Força a tokenização completa
		
		// Verifica erros léxicos
		if (errorLexer.temErros()) {
			System.out.println("ERRO: Análise léxica falhou!\n");
			return;
		}
		System.out.println("Análise léxica concluída sem erros!\n");
		
		// ========== ANÁLISE SINTÁTICA ==========
		System.out.println("Iniciando análise sintática...");
		GyhLangParser parser = new GyhLangParser(tokens);
		
		// Adiciona error listener no parser também
		parser.removeErrorListeners();
		GyhLangErrorListener errorParser = new GyhLangErrorListener();
		parser.addErrorListener(errorParser);
		
		ParseTree tree = parser.programa();
		
		// Verifica erros sintáticos
		if (parser.getNumberOfSyntaxErrors() > 0) {
			System.out.println("ERRO: Análise sintática falhou!");
			System.out.println("Foram encontrados " + parser.getNumberOfSyntaxErrors() + " erro(s) sintático(s).\n");
			return;
		}
		System.out.println("Análise sintática concluída sem erros!\n");
		
		// ========== ANÁLISE SEMÂNTICA ==========
		System.out.println("Iniciando análise semântica...");
		AnalisadorSemantico analisador = new AnalisadorSemantico();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(analisador, tree);
		
		// Exibe a tabela de símbolos
		analisador.imprimirTabelaSimbolos();
		
		// Verifica e exibe erros semânticos
		if (analisador.temErros()) {
			System.out.println("ERROS SEMÂNTICOS ENCONTRADOS:");
			System.out.println("========================================");
			for (String erro : analisador.getErros()) {
				System.out.println("  • " + erro);
			}
			System.out.println("========================================\n");
			System.out.println("========================================");
			System.out.println("   PROGRAMA COM ERROS!");
			System.out.println("========================================");
			return;
		} else {
			System.out.println("Análise semântica concluída sem erros!\n");
		}
		
		// ========== GERAÇÃO DE CÓDIGO ==========
		System.out.println("Iniciando geração de código...");
		GeradorCodigo gerador = new GeradorCodigo(analisador.getTabelaSimbolos());
		walker.walk(gerador, tree);
		
		String codigoCGerado = gerador.gerarCodigoC();
		System.out.println("Geração de código concluída!\n");
		
		// Exibe o código C gerado
		System.out.println("========== CÓDIGO C GERADO ==========");
		System.out.println(codigoCGerado);
		System.out.println("=====================================\n");
		
		// Salva o código em arquivo C
		try (java.io.PrintWriter writer = new java.io.PrintWriter("programa_gerado.c")) {
			writer.println(codigoCGerado);
			System.out.println("Código C salvo em: programa_gerado.c\n");
		} catch (Exception e) {
			System.out.println("Erro ao salvar arquivo: " + e.getMessage());
		}
		
		// Resultado final
		System.out.println("========================================");
		System.out.println("   COMPILAÇÃO COMPLETA! SEM ERROS!");
		System.out.println("========================================");
	}
	



}
