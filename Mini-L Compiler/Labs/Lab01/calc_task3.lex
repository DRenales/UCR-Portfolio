%{
    int currLine = 1, currPos = 1; 
    
    int numbers = 0,
        operators = 0,
            parentheses = 0,
                equal = 0;
%}

DIGIT       [0-9]

%%

"-"         { printf("MINUS\n")  ; currPos += yyleng; operators++; }
"+"         { printf("PLUS\n")   ; currPos += yyleng; operators++; }
"*"         { printf("MULT\n")   ; currPos += yyleng; operators++; }
"/"         { printf("DIV\n")    ; currPos += yyleng; operators++; }
"="         { printf("EQUAL\n")  ; currPos += yyleng; equal++; }
"("         { printf("L_PAREN\n"); currPos += yyleng; parentheses++; }
")"         { printf("R_PAREN\n"); currPos += yyleng; parentheses++; }

{DIGIT}+    { printf("NUMBER %s\n", yytext); currPos += yyleng; numbers++; }

[ \t]+      { currPos += yyleng; }

"\n"        { currLine++; currPos = 1; }

.           { printf("Error at line %d, column %d: unrecognized symbol \"%s\"\n", currLine, currPos, yytext); exit(0); }

%%

int main(int argc, char** argv)
{
    if(argc >= 2)
    {
        yyin = fopen(argv[1], "r");
        if(yyin == NULL) yyin = stdin;
    }
    else yyin = stdin;
    
    yylex();

    printf("There are %d numbers\n", numbers);
    printf("There are %d operators\n", operators);
    printf("There are %d parentheses\n", parentheses);
    printf("There are %d equal signs\n", equal);
    return 0;
}