public class CommandSaida extends Command {
    
    private String conteudo;
    private boolean isString;
    private String tipo;
    
    public CommandSaida(String conteudo, boolean isString, String tipo) {
        this.conteudo = conteudo;
        this.isString = isString;
        this.tipo = tipo;
    }
    
    @Override
    public String generateCode() {
        if (isString) {
            // Remove aspas e adiciona \n
            String texto = conteudo.substring(1, conteudo.length() - 1);
            return "printf(\"" + texto + "\\n\");";
        } else {
            String formato = tipo.equals("INTEGER") ? "%d" : "%lf";
            return "printf(\"" + formato + "\\n\", " + conteudo + ");";
        }
    }
}
