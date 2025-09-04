import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class AnalisadorLexico {


	private LeitorArquivo ldat;
	private Map<String, TipoToken> palavrasReservadas; // inclui palavras-chave e operadores booleanos
	private Map<Character, TipoToken> tokensSimples;   // tokens de 1 caractere
	private boolean eofEmitido = false;                 // controla emissão única de EOF

	public AnalisadorLexico(String nomeArq) throws IOException {

		ldat = new LeitorArquivo(nomeArq);
		inicializarTabelas();

	}

	public LeitorArquivo getLdat() {
	    return ldat;
	}

	public void setLdat(LeitorArquivo ldat) {
	    this.ldat = ldat;
	}

	private void inicializarTabelas() {
		palavrasReservadas = new Hashtable<>();
		// Palavras-chave
		palavrasReservadas.put("DECLARAR", TipoToken.PCDec);
		palavrasReservadas.put("PROGRAMA", TipoToken.PCProg);
		palavrasReservadas.put("INTEGER", TipoToken.PCInt);
		palavrasReservadas.put("REAL", TipoToken.PCReal);
		palavrasReservadas.put("LER", TipoToken.PCLer);
		palavrasReservadas.put("IMPRIMIR", TipoToken.PCImprimir);
		palavrasReservadas.put("SE", TipoToken.PCSe);
		palavrasReservadas.put("ENTAO", TipoToken.PCEntao);
		palavrasReservadas.put("SENAO", TipoToken.PCSenao);
		palavrasReservadas.put("ENQTO", TipoToken.PCEnqto);
		palavrasReservadas.put("INICIO", TipoToken.PCIni);
		palavrasReservadas.put("FINAL", TipoToken.PCFim);
		// Operadores booleanos como palavras
		palavrasReservadas.put("E", TipoToken.OpBoolE);
		palavrasReservadas.put("OU", TipoToken.OpBoolOu);

		tokensSimples = new Hashtable<>();
		tokensSimples.put('*', TipoToken.OpAritMult);
		tokensSimples.put('/', TipoToken.OpAritDiv);
		tokensSimples.put('+', TipoToken.OpAritSoma);
		tokensSimples.put('-', TipoToken.OpAritSub);
		tokensSimples.put('[', TipoToken.DelimAbre);
		tokensSimples.put(']', TipoToken.DelimFecha);
		tokensSimples.put('(', TipoToken.AbrePar);
		tokensSimples.put(')', TipoToken.FechaPar);
		tokensSimples.put(':', TipoToken.DelimDoisPontos); // observar ":=" abaixo
	}

	private int lerChar() throws IOException {
		return ldat.lerProxCaracter();
	}

	private void devolverChar(int c) {
		ldat.devolverCaracter(c);
	}

	private void pularEspacos() throws IOException {
		int c;
		while (true) {
			c = lerChar();
			// EOF
			if (c == -1) { devolverChar(c); return; }
			// comentário: do '#' até fim da linha
			if (c == '#') {
				int d;
				while ((d = lerChar()) != -1 && d != '\n') {
					// consome
				}
				// após comentário, continua laço para consumir mais espaços/comentários
				continue;
			}
			// espaços em branco
			if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
				continue;
			}
			devolverChar(c);
			return;
		}
	}

	private static boolean ehLetraMinuscula(int c) { return c >= 'a' && c <= 'z'; }
	private static boolean ehLetraMaiuscula(int c) { return c >= 'A' && c <= 'Z'; }
	private static boolean ehDigito(int c) { return c >= '0' && c <= '9'; }

	public Token proxToken() throws Exception {
		if (eofEmitido) return null;

		pularEspacos();
		int c = lerChar();
		int startLinha = ldat.getLinhaAtual();
		int startColuna = ldat.getColunaAtual();

		if (c == -1) {
			eofEmitido = true;
			return new Token("", TipoToken.EOF, startLinha, startColuna);
		}

		// Strings: "..."
		if (c == '"') {
			StringBuilder sb = new StringBuilder();
			sb.append('"');
			int d;
			while ((d = lerChar()) != -1 && d != '"') {
				sb.append((char)d);
			}
			if (d == '"') {
				sb.append('"');
				return new Token(sb.toString(), TipoToken.Cadeia, startLinha, startColuna);
			}
			throw new Exception("Cadeia não terminada antes do EOF (linha " + startLinha + ", coluna " + startColuna + ")");
		}

		// Números: inteiro ou real (um ponto opcional)
		if (ehDigito(c)) {
			StringBuilder sb = new StringBuilder();
			boolean temPonto = false;
			sb.append((char)c);
			int d;
			while (true) {
				d = lerChar();
				if (d == '.') {
					if (temPonto) { // segundo ponto encerra número anterior
						devolverChar(d);
						break;
					}
					temPonto = true;
					sb.append('.');
					continue;
				}
				if (!ehDigito(d)) { devolverChar(d); break; }
				sb.append((char)d);
			}
			return new Token(sb.toString(), temPonto ? TipoToken.NumReal : TipoToken.NumInt, startLinha, startColuna);
		}

		// Identificadores/Palavras: distinguindo minúsculas (Var) de MAIÚSCULAS (reservadas/booleanos)
		if (ehLetraMinuscula(c)) {
			StringBuilder sb = new StringBuilder();
			sb.append((char)c);
			int d;
			while (true) {
				d = lerChar();
				if (ehLetraMaiuscula(d) || ehLetraMinuscula(d) || ehDigito(d)) {
					sb.append((char)d);
				} else {
					devolverChar(d);
					break;
				}
			}
			return new Token(sb.toString(), TipoToken.Var, startLinha, startColuna);
		}
		if (ehLetraMaiuscula(c)) {
			StringBuilder sb = new StringBuilder();
			sb.append((char)c);
			int d;
			while (true) {
				d = lerChar();
				if (ehLetraMaiuscula(d)) {
					sb.append((char)d);
				} else {
					devolverChar(d);
					break;
				}
			}
			String palavra = sb.toString();
			TipoToken tk = palavrasReservadas.get(palavra);
			if (tk != null) return new Token(palavra, tk, startLinha, startColuna);
			throw new Exception("Palavra desconhecida: " + palavra + " (linha " + startLinha + ", coluna " + startColuna + ")");
		}

		// Operadores compostos e simples
		if (c == ':') {
			int d = lerChar();
			if (d == '=') return new Token(":=", TipoToken.Atrib, startLinha, startColuna);
			devolverChar(d);
			return new Token(":", TipoToken.DelimDoisPontos, startLinha, startColuna);
		}
		if (c == '<') {
			int d = lerChar();
			if (d == '=') return new Token("<=", TipoToken.OpRelMenorIgual, startLinha, startColuna);
			devolverChar(d);
			return new Token("<", TipoToken.OpRelMenor, startLinha, startColuna);
		}
		if (c == '>') {
			int d = lerChar();
			if (d == '=') return new Token(">=", TipoToken.OpRelMaiorIgual, startLinha, startColuna);
			devolverChar(d);
			return new Token(">", TipoToken.OpRelMaior, startLinha, startColuna);
		}
		if (c == '=') {
			int d = lerChar();
			if (d == '=') return new Token("==", TipoToken.OpRelIgual, startLinha, startColuna);
			throw new Exception("'=' isolado não é válido. Use '==' para igualdade ou ':=' para atribuição. (linha " + startLinha + ", coluna " + startColuna + ")");
		}
		if (c == '!') {
			int d = lerChar();
			if (d == '=') return new Token("!=", TipoToken.OpRelDif, startLinha, startColuna);
			throw new Exception("'!' isolado não é válido. Use '!=' para diferente. (linha " + startLinha + ", coluna " + startColuna + ")");
		}

		// Tokens de 1 caractere via tabela
		TipoToken tk = tokensSimples.get((char)c);
		if (tk != null) {
			return new Token(String.valueOf((char)c), tk, startLinha, startColuna);
		}

		throw new Exception("Caractere inesperado: '" + (char)c + "' (linha " + startLinha + ", coluna " + startColuna + ")");
	}


}
