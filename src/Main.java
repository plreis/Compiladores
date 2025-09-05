public class Main {

	
	
	//Criando instancia do arquivo.

	
	public static void main(String[] args) {

		AnalisadorLexico alex = null;
		try {
			alex = new AnalisadorLexico("programa.gyh");
			Token t = alex.proxToken();
			while (t != null) {
				System.out.println(t);
				t = alex.proxToken();
			}
		} catch (Exception e) {
			System.err.println("Erro léxico: " + e.getMessage());
		} finally {
			try {
				if (alex != null && alex.getLdat() != null) {
					alex.getLdat().fecharArquivo();
				}
			} catch (Exception ignore) {}
		}
	}

}
