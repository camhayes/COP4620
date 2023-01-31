lexer grammar Little;

COMMENT: '--' .*? '\r'? '\n' -> skip; // Skips comments which must be single line.

WS : [ \t\r\n]+ -> skip; // Skips all whitespace and tabs etc.

KEYWORD: 'PROGRAM'  | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' |
'WRITE' | 'IF' | 'ELSE' | 'ENDIF' | 'WHILE' | 'ENDWHILE' |
'CONTINUE' | 'BREAK' | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT';

OPERATOR: ':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=';

IDENTIFIER : [a-zA-Z][a-zA-Z0-9]*;

INTLITERAL: [0-9]+;

FLOATLITERAL: [0-9]* '.' [0-9]+;

STRINGLITERAL: '"' .*? '"';
