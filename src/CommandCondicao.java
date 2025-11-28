import java.util.List;

public class CommandCondicao extends Command {
    
    private String condicao;
    private List<Command> comandosEntao;
    private List<Command> comandosSenao;
    
    public List<Command> getComandosEntao() {
        return comandosEntao;
    }
    
    public List<Command> getComandosSenao() {
        return comandosSenao;
    }
    
    public void setComandosSenao(List<Command> comandosSenao) {
        this.comandosSenao = comandosSenao;
    }
    
    public CommandCondicao(String condicao, List<Command> comandosEntao, List<Command> comandosSenao) {
        this.condicao = condicao;
        this.comandosEntao = comandosEntao;
        this.comandosSenao = comandosSenao;
    }
    
    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("if (").append(condicao).append(") {\n");
        for (Command cmd : comandosEntao) {
            String codigo = cmd.generateCode();
            String[] linhas = codigo.split("\n");
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    sb.append("    ").append(linha).append("\n");
                }
            }
        }
        sb.append("}");
        
        if (comandosSenao != null && !comandosSenao.isEmpty()) {
            sb.append(" else {\n");
            for (Command cmd : comandosSenao) {
                String codigo = cmd.generateCode();
                String[] linhas = codigo.split("\n");
                for (String linha : linhas) {
                    if (!linha.trim().isEmpty()) {
                        sb.append("    ").append(linha).append("\n");
                    }
                }
            }
            sb.append("}");
        }
        
        return sb.toString();
    }
}
