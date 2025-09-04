import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LeitorArquivo {

    // atributos privados + getters/setters
    private BufferedReader br;
    private Integer pushbackChar = null; // 1-char pushback
    private int linha = 1;               // 1-based
    private int coluna = 0;              // coluna do último char lido (0 antes do primeiro)

    // para reportar a posição do char devolvido (pushback)
    private int pushLinha;
    private int pushColuna;

    public LeitorArquivo(String nomeArq) throws IOException {
        br = new BufferedReader(new FileReader(nomeArq));
    }

    // Lê próximo caractere e atualiza linha/coluna
    public int lerProxCaracter() throws IOException {
        if (pushbackChar != null) {
            int c = pushbackChar;
            pushbackChar = null;
            // posição já é a do char devolvido
            linha = pushLinha;
            coluna = pushColuna;
            return c;
        }
        int c = br.read();
        if (c == -1) return -1;

        // atualiza posição
        if (c == '\n') {
            linha++;
            coluna = 0;
        } else if (c == '\r') {
            // em Windows (CRLF), consideramos nova linha apenas no '\n' para não contar em dobro
            // portanto, não alteramos linha/coluna aqui
        } else {
            coluna++;
        }
        return c;
    }

    // Devolve um caractere lido (pushback) preservando posição
    public void devolverCaracter(int c) {
        if (c == -1) return;
        if (pushbackChar == null) {
            pushbackChar = c;
            pushLinha = linha;
            pushColuna = coluna;
        }
    }

    public void fecharArquivo() throws IOException {
        if (br != null) {
            br.close();
        }
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public int getLinhaAtual() {
        return linha;
    }

    public int getColunaAtual() {
        return coluna;
    }
}