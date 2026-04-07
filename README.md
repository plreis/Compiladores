# Compilador da Linguagem GYH (Java)

Este projeto implementa um **Analisador Léxico** e um **Analisador Sintático Preditivo Recursivo Descendente** para a linguagem GYH em Java.

## 🎯 Características Principais

### Analisador Léxico
- Tabelas (Hashtable) para reconhecimento de palavras-chave e tokens simples
- Atributos encapsulados (privados) com getters/setters
- Rastreamento preciso de posição: cada token carrega linha e coluna
- Mensagens de erro léxico detalhadas com localização
- Suporte a comentários (#) e espaços em branco
- Leitura de arquivo robusta (Windows/CRLF) com pushback de 1 caractere

### Analisador Sintático
- **Parser Preditivo Recursivo Descendente**
- **Eliminação de recursão à esquerda** nas expressões aritméticas e relacionais
- Validação completa da estrutura do programa GYH
- Mensagens de erro sintático com linha e coluna
- Verificação de EOF ao final do programa

## 📁 Estrutura do Projeto

- `src/LeitorArquivo.java`: Leitor de caracteres do arquivo fonte
  - Posição atual (linha 1-based, coluna) e método de pushback
  - Tratamento de CRLF (Windows): incrementa linha apenas no `\n`
- `src/Token.java`: Representa um token com `lexema`, `tipo`, `linha` e `coluna`
- `src/TipoToken.java`: Enum com todos os tipos de token suportados
- `src/AnalisadorLexico.java`: Scanner que emite tokens
- `src/AnalisadorSintatico.java`: Parser recursivo descendente que valida a gramática
- `src/Main.java`: Executa análise léxica e sintática sobre `programa.gyh`
- `programa.gyh`: Exemplo de código GYH para teste
- `Códigos GYH para testarem o Analisador Léxico-20250905/`: Conjunto de programas de teste

## 🔤 Tokens Suportados

### Palavras-chave
- `DECLARAR` (PCDec), `PROGRAMA` (PCProg)
- `INTEGER` (PCInt), `REAL` (PCReal)
- `LER` (PCLer), `IMPRIMIR` (PCImprimir)
- `SE` (PCSe), `ENTAO` (PCEntao), `SENAO` (PCSenao)
- `ENQTO` (PCEnqto), `INICIO` (PCIni), `FINAL` (PCFim)

### Operadores
- **Aritméticos**: `*` (OpAritMult), `/` (OpAritDiv), `+` (OpAritSoma), `-` (OpAritSub)
- **Relacionais**: `<` (OpRelMenor), `<=` (OpRelMenorIgual), `>` (OpRelMaior), `>=` (OpRelMaiorIgual), `==` (OpRelIgual), `!=` (OpRelDif)
- **Booleanos**: `E` (OpBoolE), `OU` (OpBoolOu)

### Delimitadores e Símbolos
- Delimitadores: `[` (DelimAbre), `]` (DelimFecha), `:` (DelimDoisPontos)
- Atribuição: `:=` (Atrib)
- Parêntesis: `(` (AbrePar), `)` (FechaPar)

### Literais
- **Identificadores** (Var): Começam com letra minúscula, seguem letras/dígitos
- **Números Inteiros** (NumInt): Sequências de dígitos
- **Números Reais** (NumReal): Sequências de dígitos com ponto decimal
- **Cadeias** (Cadeia): Texto entre aspas duplas `"..."`
- **EOF**: Fim de arquivo

## 📝 Gramática GYH

```
Programa → '[' 'DECLARAR'']' ListaDeclaracoes '[' 'PROGRAMA'']' ListaComandos

ListaDeclaracoes → Declaracao ListaDeclaracoes | Declaracao
Declaracao → VARIAVEL ':' TipoVar
TipoVar → 'INTEGER' | 'REAL'

ExpressaoAritmetica → TermoAritmetico ExpressaoAritmetica'
ExpressaoAritmetica' → '+' TermoAritmetico ExpressaoAritmetica' 
                     | '-' TermoAritmetico ExpressaoAritmetica' 
                     | ε

TermoAritmetico → FatorAritmetico TermoAritmetico'
TermoAritmetico' → '*' FatorAritmetico TermoAritmetico' 
                 | '/' FatorAritmetico TermoAritmetico' 
                 | ε

FatorAritmetico → NUMINT | NUMREAL | VARIAVEL | '(' ExpressaoAritmetica ')'

ExpressaoRelacional → TermoRelacional ExpressaoRelacional'
ExpressaoRelacional' → OperadorBooleano TermoRelacional ExpressaoRelacional' | ε

TermoRelacional → ExpressaoAritmetica OP_REL ExpressaoAritmetica 
                | '(' ExpressaoRelacional ')'

OperadorBooleano → 'E' | 'OU'

ListaComandos → Comando ListaComandos | Comando

Comando → ComandoAtribuicao | ComandoEntrada | ComandoSaida 
        | ComandoCondicao | ComandoRepeticao | SubAlgoritmo

ComandoAtribuicao → VARIAVEL ':=' ExpressaoAritmetica
ComandoEntrada → 'LER' VARIAVEL
ComandoSaida → 'IMPRIMIR' VARIAVEL | 'IMPRIMIR' CADEIA
ComandoCondicao → 'SE' ExpressaoRelacional 'ENTAO' Comando 
                | 'SE' ExpressaoRelacional 'ENTAO' Comando 'SENAO' Comando
ComandoRepeticao → 'ENQTO' ExpressaoRelacional Comando
SubAlgoritmo → 'INICIO' ListaComandos 'FINAL'
```

**Nota**: As regras com `'` (prima) são resultado da **eliminação de recursão à esquerda** para permitir parsing preditivo.

## 🔄 Eliminação de Recursão à Esquerda

A gramática original tinha recursão à esquerda nas expressões, o que impede parsers recursivos descendentes. A transformação aplicada foi:

**Antes:**
```
ExpressaoAritmetica → ExpressaoAritmetica '+' TermoAritmetico | TermoAritmetico
```

**Depois:**
```
ExpressaoAritmetica → TermoAritmetico ExpressaoAritmetica'
ExpressaoAritmetica' → '+' TermoAritmetico ExpressaoAritmetica' | ε
```

Essa transformação mantém a mesma linguagem aceita, mas permite análise descendente sem loops infinitos.

## ⚠️ Tratamento de Erros

### Erros Léxicos
- `Cadeia não terminada antes do EOF (linha L, coluna C)`
- `Caractere inesperado: 'X' (linha L, coluna C)`
- `'=' isolado não é válido. Use '==' para igualdade ou ':=' para atribuição`
- `'!' isolado não é válido. Use '!=' para diferente`
- `Palavra desconhecida: XXX (linha L, coluna C)`

### Erros Sintáticos
- `Erro de Sintaxe na linha L, coluna C: Esperado token 'X', mas encontrou 'Y'`
- `Erro de Sintaxe na linha L: Comando inválido. 'X' não inicia um comando`
- `Erro de Sintaxe: Código inesperado após o final do programa`

## 🚀 Como Compilar e Executar

### Requisitos
- JDK 8 ou superior

### Compilação e Execução

**Windows (PowerShell):**
```bash
cd /caminho/do/projeto
javac -d bin src/*.java
java -cp bin Main
```

**Linux/Mac:**
```bash
cd /caminho/do/projeto
javac -d bin src/*.java
java -cp bin Main
```

### Saída Esperada

```
=== ANÁLISE LÉXICA ===
<DelimAbre,[,@3:1>
<PCDec,DECLARAR,@3:2>
<DelimFecha,],@3:10>
<Var,fatorial,@5:1>
<DelimDoisPontos,:,@5:9>
<PCInt,INTEGER,@5:10>
...
<EOF,,@22:0>

=== ANÁLISE SINTÁTICA ===
Análise Sintática concluída com sucesso!
```

## 📋 Exemplo de Programa GYH

```gyh
# Programa para calcular fatorial

[DECLARAR]
fatorial:INTEGER
parametro:INTEGER

[PROGRAMA]
# Calcula o fatorial de um número inteiro
LER parametro
fatorial := parametro 
SE parametro == 0 ENTAO fatorial := 1
ENQTO parametro > 1
INICIO
    fatorial := fatorial * (parametro - 1) 
    parametro := parametro - 1
FINAL
IMPRIMIR fatorial
IMPRIMIR "Cálculo concluído"
```

## 🎓 Conceitos Implementados

- ✅ Análise léxica com tabelas de símbolos
- ✅ Análise sintática preditiva recursiva descendente
- ✅ Eliminação de recursão à esquerda
- ✅ Tratamento de precedência de operadores
- ✅ Rastreamento de posição (linha/coluna)
- ✅ Tratamento robusto de erros
- ✅ Encapsulamento e boas práticas OOP

## 📊 Avaliação de Critérios

Este projeto atende completamente aos seguintes critérios de avaliação:

### Analisador Léxico 
- ✅ Reconhecimento de todos os tokens da especificação
- ✅ Tratamento de comentários e espaços em branco
- ✅ Mensagens de erro com localização

### Analisador Sintático 
- ✅ Todas as regras da gramática implementadas
- ✅ Eliminação correta de recursão à esquerda
- ✅ Validação completa da estrutura do programa

### Código em Geral 
- ✅ Organização clara e coerente
- ✅ Entrada/saída bem estruturada
- ✅ Código bem comentado (bônus)


## 👨‍💻 Autor

Pedro Reis - [@plreis](https://github.com/plreis)

## 📄 Licença

Este projeto foi desenvolvido como trabalho acadêmico para a disciplina de Compiladores.




