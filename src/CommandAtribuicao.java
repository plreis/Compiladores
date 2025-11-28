public class CommandAtribuicao extends Command {
    
    private String id;
    private String expressao;
    
    public CommandAtribuicao(String id, String expressao) {
        this.id = id;
        this.expressao = expressao;
    }
    
    @Override
    public String generateCode() {
        return id + " = " + expressao + ";";
    }
}
