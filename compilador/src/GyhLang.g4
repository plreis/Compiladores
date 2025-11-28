grammar GyhLang;


programa : '[' DECLARAR ']' listaDeclaracoes '[' PROGRAMA ']' listaComandos;

listaDeclaracoes : declaracao+;

declaracao : Var ':' tipoVar;

tipoVar : INTEGER | REAL;

listaComandos : comando+;

comando : comandoAtribuicao 
        | comandoEntrada 
        | comandoSaida 
        | comandoCondicao 
        | comandoRepeticao 
        | subAlgoritmo;

comandoAtribuicao : Var ':=' expressaoAritmetica;

comandoEntrada : LER Var;

comandoSaida : IMPRIMIR Var 
             | IMPRIMIR String;

comandoCondicao : SE expressaoRelacional ENTAO comando
                | SE expressaoRelacional ENTAO comando SENAO comando;

comandoRepeticao : ENQTO expressaoRelacional comando;

subAlgoritmo : INICIO listaComandos FINAL;

expressaoAritmetica : expressaoAritmetica '+' termoAritmetico
                    | expressaoAritmetica '-' termoAritmetico
                    | termoAritmetico;

termoAritmetico : termoAritmetico '*' fatorAritmetico
                | termoAritmetico '/' fatorAritmetico
                | fatorAritmetico;

fatorAritmetico : NumInt 
                | NumReal 
                | Var 
                | String
                | '(' expressaoAritmetica ')';

expressaoRelacional : expressaoRelacional operadorBooleano termoRelacional
                    | termoRelacional;

termoRelacional : expressaoAritmetica OpRel expressaoAritmetica
                | '(' expressaoRelacional ')';

operadorBooleano : E | OU;




DECLARAR : 'DECLARAR';
PROGRAMA : 'PROGRAMA';
INTEGER : 'INTEGER';
REAL : 'REAL';
LER : 'LER';
IMPRIMIR : 'IMPRIMIR';
SE : 'SE';
ENTAO : 'ENTAO';
SENAO : 'SENAO';
ENQTO : 'ENQTO';
INICIO : 'INICIO';
FINAL : 'FINAL';
E : 'E';
OU : 'OU';

OpRel : '<' | '<=' | '>' | '>=' | '==' | '!=';

// Captura identificadores inválidos (que começam com número) antes de Var
INVALID_ID : [0-9]+ [a-zA-Z] ([a-z] | [A-Z] | [0-9])*;

Var : [a-z] ([a-z] | [A-Z] | [0-9])*;

NumInt : [0-9]+;

NumReal : [0-9]+ '.' [0-9]+;

String : '"' (~["\n\r])* '"';

EmBranco : (' ' | '\n' | '\r' | '\t') -> skip;

Comentario : '#' (~[\n\r])* -> skip;

  

  