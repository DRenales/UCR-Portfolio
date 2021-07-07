%{
    #include "y.tab.h"
    int currLine = 1, currPos = 1; 
    
    int numbers = 0,
        operators = 0,
            parentheses = 0,
                equal = 0;
%}

DIGIT       [0-9]
FLOAT       [0-9]*[.][0-9]+
EXPO1       [0-9][eE][+-]?[0-9]+
EXPO2       [0-9][.][0-9]+[eE][+-]?[0-9]+

%%

"-"         { currPos += yyleng; operators++;   return MINUS;   }
"+"         { currPos += yyleng; operators++;   return PLUS;    }
"*"         { currPos += yyleng; operators++;   return MULT;    }
"/"         { currPos += yyleng; operators++;   return DIV;     }
"="         { currPos += yyleng; equal++;       return EQUAL;   }
"("         { currPos += yyleng; parentheses++; return L_PAREN; }
")"         { currPos += yyleng; parentheses++; return R_PAREN; }

{FLOAT}     { currPos += yyleng; yylval.dval = atof(yytext); numbers++; return NUMBER; }
{DIGIT}+    { currPos += yyleng; yylval.dval = atof(yytext); numbers++; return NUMBER; }
{EXPO2}     { currPos += yyleng; yylval.dval = atof(yytext); numbers++; return NUMBER; }
{EXPO1}     { currPos += yyleng; yylval.dval = atof(yytext); numbers++; return NUMBER; }

[\ \t]+      { currPos += yyleng; }

"\n"        { currLine++; currPos = 1; return END; }
.           { printf("Error at line %d, column %d: unrecognized symbol \"%s\"\n", currLine, currPos, yytext); exit(0); }

%%