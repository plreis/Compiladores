import org.antlr.v4.runtime.*;

public class GyhLangErrorListener extends BaseErrorListener {
    
    private boolean temErros = false;
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                           Object offendingSymbol,
                           int line,
                           int charPositionInLine,
                           String msg,
                           RecognitionException e) {
        
        temErros = true;
        
        // Verifica se é um token inválido (identificador que começa com número)
        if (offendingSymbol instanceof Token) {
            Token token = (Token) offendingSymbol;
            String tokenText = token.getText();
            
            // Detecta INVALID_ID
            if (token.getType() == GyhLangLexer.INVALID_ID) {
                System.err.println("ERRO LÉXICO na linha " + line + ":" + charPositionInLine + 
                                 " - Identificador inválido '" + tokenText + 
                                 "' (identificadores não podem começar com número)");
                return;
            }
        }
        
        // Erro sintático padrão
        System.err.println("ERRO SINTÁTICO na linha " + line + ":" + charPositionInLine + " - " + msg);
    }
    
    public boolean temErros() {
        return temErros;
    }
}
