lexer grammar Little;

KEYWORD: 'PROGRAM'  | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' |
'WRITE' | 'IF' | 'ELSE' | 'ENDIF' | 'WHILE' | 'ENDWHILE' |
'CONTINUE' | 'BREAK' | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT';

OPERATOR: ':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=';

IDENTIFIER: [a-zA-Z] [a-z-A-Z0-9]*;

INTLITERAL: [0-9]+;

FLOATLITERAL: [0-9]* '.' [0-9]+;

STRINGLITERAL: '"' .*? '"';

COMMENT: '--' .*? '\n';
