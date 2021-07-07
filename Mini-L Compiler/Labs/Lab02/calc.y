%{
#include <stdio.h>
#include <stdlib.h>
void yyerror(const char* msg);
extern int currLine;
extern int currPos;
extern int numbers, operators, parentheses, equal;
FILE* yyin;
%}

%union
{
    double dval;
    int ival;
}

%error-verbose
%start input
%token MULT DIV PLUS MINUS EQUAL L_PAREN R_PAREN END
%token <dval> NUMBER
%type <dval> exp
%left PLUS MINUS
%left MULT DIV

%%

input:  
        | input line
        ;
line:   exp EQUAL END               { printf("\t%f\n", $1); }
        ;

exp:        NUMBER                  { $$ = $1; }
            | exp PLUS exp          { $$ = $1 + $3; }
            | exp MINUS exp         { $$ = $1 - $3; } 
            | exp MULT exp          { $$ = $1 * $3; }
            | exp DIV exp           { if ($3==0) yyerror("divide by zero"); else $$ = $1 / $3; }
            | MINUS exp             { $$ = -$2; }
            | L_PAREN exp R_PAREN   { $$ = $2; }
            ;

%%

int main(int argC, char** argV)
{
    if( argC > 1 )
    {
        yyin = fopen(argV[1],"r");
        if(yyin == NULL) printf("error opening file");
    }
    yyparse();

    printf("There are %d numbers\n", numbers);
    printf("There are %d operators\n", operators);
    printf("There are %d parentheses\n", parentheses);
    printf("There are %d equal signs\n", equal);
    return 0;
}

void yyerror(const char* msg) { printf("** Line %d, position %d: %s\n", currLine, currPos, msg); }