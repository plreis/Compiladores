public class CommandEntrada extends Command {
    
    private String variavel;
    private String tipo;
    
    public CommandEntrada(String variavel, String tipo) {
        this.variavel = variavel;
        this.tipo = tipo;
    }
    
    @Override
    public String generateCode() {
        String formato = tipo.equals("INTEGER") ? "%d" : "%lf";
        return "scanf(\"" + formato + "\", &" + variavel + ");";
    }
}
