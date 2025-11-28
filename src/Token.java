public class Token {

	private String lexema;
	private TipoToken padrao;
	private int linha;
	private int coluna;
	// mais infos
	
	
	public Token(String lexema, TipoToken padrao, int linha, int coluna) {
		this.lexema=lexema;
		this.padrao=padrao;
		this.linha = linha;
		this.coluna = coluna;

	}


	@Override
	
	public String toString() {
		return "<"+padrao+","+lexema+",@"+linha+":"+coluna+">";
	}


	public String getLexema() {
	    return lexema;
	}

	public void setLexema(String lexema) {
	    this.lexema = lexema;
	}

	public TipoToken getPadrao() {
	    return padrao;
	}

	public void setPadrao(TipoToken padrao) {
	    this.padrao = padrao;
	}

	public int getLinha() {
	    return linha;
	}

	public void setLinha(int linha) {
	    this.linha = linha;
	}

	public int getColuna() {
	    return coluna;
	}

	public void setColuna(int coluna) {
	    this.coluna = coluna;
	}

}
