# Analisador Léxico da Linguagem GYH (Java)

Este projeto implementa um analisador léxico para a linguagem GYH em Java, com foco em:

- Tabelas (Hashtable) para reconhecimento de palavras-chave e tokens simples (sem `switch`).
- Atributos encapsulados (privados) com getters/setters onde aplicável.
- Rastreamento preciso de posição: cada token carrega a linha e a coluna do seu início.
- Mensagens de erro léxico com linha e coluna.
- Leitura de arquivo robusta (Windows/CRLF), com suporte a pushback de 1 caractere.

## Estrutura

- `src/LeitorArquivo.java`: leitor de caracteres do arquivo fonte com:
  - Posição atual (linha 1-based, coluna) e método de pushback de 1 caractere.
  - Tratamento de CRLF (Windows): só incrementa linha no `\n` para evitar contagem dupla.
- `src/Token.java`: representa um token com `lexema`, `tipo (TipoToken)`, `linha` e `coluna`.
- `src/TipoToken.java`: enum com todos os tipos de token suportados.
- `src/AnalisadorLexico.java`: scanner que consome caracteres do `LeitorArquivo` e emite `Token`s.
- `src/Main.java`: executa o léxico sobre `programa.gyh` e imprime os tokens.
- `programa.gyh`: exemplo de código GYH para teste.

## Tokens Suportados

- Palavras-chave: `DECLARAR(PCDec)`, `PROGRAMA(PCProg)`, `INTEGER(PCInt)`, `REAL(PCReal)`, `LER(PCLer)`, `IMPRIMIR(PCImprimir)`, `SE(PCSe)`, `ENTAO(PCEntao)`, `SENAO(PCSenao)`, `ENQTO(PCEnqto)`, `INICIO(PCIni)`, `FINAL(PCFim)`.
- Operadores aritméticos: `*(OpAritMult)`, `/(OpAritDiv)`, `+(OpAritSoma)`, `-(OpAritSub)`.
- Operadores relacionais: `<(OpRelMenor)`, `<= (OpRelMenorIgual)`, `>(OpRelMaior)`, `>= (OpRelMaiorIgual)`, `==(OpRelIgual)`, `!=(OpRelDif)`.
- Operadores booleanos: `E(OpBoolE)`, `OU(OpBoolOu)`.
- Delimitadores: `[ (DelimAbre)`, `] (DelimFecha)`, `: (DelimDoisPontos)`.
- Atribuição: `:=(Atrib)`.
- Parêntesis: `((AbrePar)`, `)(FechaPar)`.
- Identificadores: `Var` (começam com letra minúscula, seguem letras/dígitos).
- Números: `NumInt` (só dígitos), `NumReal` (com ponto decimal).
- Cadeia de caracteres: `Cadeia` (entre aspas duplas `"...")`.
- Fim de arquivo: `EOF`.

Observações:
- Palavras reservadas e operadores booleanos são reconhecidos apenas em MAIÚSCULAS, conforme a especificação.
- Identificadores (variáveis) começam com minúscula.
- Comentários de linha iniciados com `#` até o fim da linha são ignorados (facilitador; remissível se não desejar).

## Erros Léxicos

As mensagens incluem a posição do problema (linha, coluna), por exemplo:

- `Cadeia não terminada antes do EOF (linha L, coluna C)`
- `Caractere inesperado: 'X' (linha L, coluna C)`
- `'=' isolado não é válido. Use '==' para igualdade ou ':=' para atribuição. (linha L, coluna C)`

## Como compilar e executar

Requisitos: JDK 8+.

```bash
# Windows PowerShell
cd "C:\Users\pedro\OneDrive\Área de Trabalho\AnalisadorLexicoAP1"
javac -d bin src\*.java
java -cp bin Main
```

Saída típica (exemplo abreviado):

```
<Var,parametro,@2:1>
<PCProg,PROGRAMA,@5:2>
<OpRelIgual,==,@9:14>
<EOF,,@16:0>
```

## Encapsulamento e mudanças aplicadas

- Todos os atributos relevantes foram tornados privados.
- `Token`: agora possui `linha` e `coluna` com getters/setters; `toString()` exibe `<Tipo,Lexema,@linha:coluna>`.
- `LeitorArquivo`: fornece `getLinhaAtual()` e `getColunaAtual()`; adiciona `devolverCaracter(int)` para pushback.
- `AnalisadorLexico`: usa `Hashtable` para lookup (palavras reservadas e tokens simples); cria tokens com posição.
- `Main`: trata exceções léxicas e fecha o arquivo ao final.

## Próximos passos sugeridos

- Automatizar testes unitários para o léxico (happy path + erros comuns).
- Remover ou formalizar comentários `#` na gramática, se necessário.
- Integrar com o analisador sintático em seguida.

## Licença

Sem licença definida. Adicione uma se necessário.

