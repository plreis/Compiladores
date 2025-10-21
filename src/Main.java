
 public class Main {


	public static void main(String[] args) {

		AnalisadorLexico alex = null;
        AnalisadorSintatico asint = null;

		try {
			//Análise Léxica (visualizar tokens)
			System.out.println("=== ANÁLISE LÉXICA ===");
			alex = new AnalisadorLexico("programa.gyh");

			Token t = alex.proxToken();
			while (t != null) {
				System.out.println(t);
				t = alex.proxToken();
			}
			alex.getLdat().fecharArquivo();

			// Análise Sintática 
			System.out.println("\n=== ANÁLISE SINTÁTICA ===");
			alex = new AnalisadorLexico("programa.gyh");
			asint = new AnalisadorSintatico(alex);
			asint.parse();

		} catch (Exception e) {
			System.err.println("Erro: " + e.getMessage());
		} finally {

		try {
			if (alex != null && alex.getLdat() != null) {
				alex.getLdat().fecharArquivo();
			}
		} catch (Exception e) {
			System.err.println("Erro ao fechar arquivo: " + e.getMessage());
		}

	}


	}
}



