import java.util.List;

public class CommandRepeticao extends Command {
    
    private String condicao;
    private List<Command> comandos;
    
    public CommandRepeticao(String condicao, List<Command> comandos) {
        this.condicao = condicao;
        this.comandos = comandos;
    }
    
    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("while (").append(condicao).append(") {\n");
        for (Command cmd : comandos) {
            String codigo = cmd.generateCode();
            String[] linhas = codigo.split("\n");
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    sb.append("    ").append(linha).append("\n");
                }
            }
        }
        sb.append("}");
        
        return sb.toString();
    }
}
