%{
#include <stdio.h>
#include <stdlib.h>
void yyerror( const char* msg );
extern int currLine;
extern int currPos;
FILE* yyin;
int yylex();
%}

%union
{
        int num_val;
        char* id_val;
}

%error-verbose
%start prog_start
%token FUNCTION BEGIN_PARAMS END_PARAMS BEGIN_LOCALS END_LOCALS 
       INTEGER ARRAY ENUM OF IF THEN ENDIF ELSE WHILE DO BEGIN_LOOP 
       END_LOOP CONTINUE READ WRITE AND OR NOT TRUE FALSE RETURN 
       BEGIN_BODY END_BODY MINUS PLUS MULT DIV MOD EQ NEQ LT GT LTE GTE 
       SEMICOLON COLON COMMA L_PAREN R_PAREN L_SQUARE_BRACKET 
       R_SQUARE_BRACKET ASSIGN 

%token <id_val> IDENT
%token <num_val> NUMBER

%right ASSIGN
%left OR
%left AND
%right NOT
%left LT LTE GT GTE EQ NEQ
%left PLUS MINUS
%left MULT DIV MOD
%left L_PAREN R_PAREN
%left L_SQUARE_BRACKET R_SQUARE_BRACKET

%%

prog_start:     functions { printf("prog_start -> functions\n"); }
        ;

functions:      function functions { printf("functions -> function functions\n"); }
        |       { printf("functions -> epsilon\n"); }
        ;

function:       FUNCTION identifier SEMICOLON
                BEGIN_PARAMS declarations END_PARAMS
                BEGIN_LOCALS declarations END_LOCALS
                BEGIN_BODY statements END_BODY { printf("function -> FUNCTION identifiers SEMICOLON BEGIN_PARAMS declarations END_PARAMS BEGIN_LOCALS declarations END_LOCALS BEGIN_BODY statements END_BODY\n");}
        ;

identifiers:    identifier COMMA identifiers { printf("identifiers -> identifier COMMA identifiers\n"); }
        |       identifier { printf("identifers -> identifier\n"); }
        ;

identifier:     IDENT { printf("identifier -> IDENT %s\n", $1); }
        ;

declarations:   { printf("declarations -> epsilon\n"); }
        |       declaration SEMICOLON declarations { printf("declarations -> declaration SEMICOLON declarations\n"); }
        ;

declaration:    identifiers COLON INTEGER { printf("declaration -> identifiers COLON INTEGER\n"); }
        |       identifiers COLON ENUM L_PAREN identifiers R_PAREN { printf("identifiers COLON ENUM L_PAREN identifiers R_PAREN\n"); }
        |       identifiers COLON ARRAY L_SQUARE_BRACKET NUMBER R_SQUARE_BRACKET OF INTEGER { printf("declaration -> identifiers COLON ARRAY L_SQUARE_BRACKET NUMBER %d R_SQUARE_BRACKET OF INTEGER\n", $5); }
        ;

statements:     statement SEMICOLON { printf("statements -> statement SEMICOLON\n"); }
        |       statement SEMICOLON statements { printf("statements -> statement SEMICOLON statements\n"); }
        ;

statement:      var ASSIGN expression { printf("statement -> vars ASSIGN expressions\n"); }
        |       IF bool_expr THEN statements ENDIF { printf("statement -> IF bool_expr THEN statements ENDIF\n"); }
        |       IF bool_expr THEN statements ELSE statements ENDIF { printf("statement -> IF bool_expr THEN statements ELSE statements ENDIF\n"); }
        |       WHILE bool_expr BEGIN_LOOP statements END_LOOP { printf("statement -> WHILE bool_expr BEGIN_LOOP statements END_LOOP\n"); }
        |       DO BEGIN_LOOP statements END_LOOP WHILE bool_expr { printf("statement -> DO BEGIN_LOOP statements END_LOOP WHILE bool_expr\n"); }
        |       READ vars { printf("statement -> READ vars\n"); }
        |       WRITE vars { printf("statement -> WRITE vars\n"); }
        |       CONTINUE { printf("statement -> CONTINUE\n"); }
        |       RETURN expression { printf("statement -> RETURN expressions\n"); }
        ;

bool_expr:      relation_and_expr { printf("bool_expr -> relation_and_expr\n"); }
        |       relation_and_expr OR bool_expr { printf("bool_expr -> relation_and_expr OR bool_expr\n"); }
        ;

relation_and_expr: relation_expr { printf("relation_and_expr -> relation_expr\n"); }
        |       relation_expr AND relation_and_expr { printf("relation_and_expr -> relation_expr AND relation_and_expr\n"); }
        ;

relation_expr:  expression comp expression { printf("relation_expr -> expressions comp expressions\n"); }
        |       TRUE { printf("relation_expr -> TRUE\n"); }
        |       FALSE { printf("relation_expr -> FALSE\n"); }
        |       L_PAREN bool_expr R_PAREN { printf("relation_expr -> L_PAREN bool_expr R_PAREN\n"); }
        |       NOT expression comp expression { printf("relation_expr -> NOT expressions comp expressions\n"); }
        |       NOT TRUE { printf("relation_expr -> NOT TRUE\n"); }
        |       NOT FALSE { printf("relation_expr -> NOT FALSE\n"); }
        |       NOT L_PAREN bool_expr R_PAREN { printf("relation_expr -> NOT L_PAREN bool_expr R_PAREN\n"); }
        ;

comp:           EQ  { printf("comp -> EQ\n");  }
        |       NEQ { printf("comp -> NEQ\n"); }
        |       LT  { printf("comp -> LT\n");  }
        |       GT  { printf("comp -> GT\n");  }
        |       LTE { printf("comp -> LTE\n"); }
        |       GTE { printf("comp -> GTE\n"); }
        ;

expressions:    expression { printf("expressions -> expression\n"); }
        |       expression COMMA expressions { printf("expressions -> expression COMMA expressions\n"); }
        ;

expression:     multiplicative_expr { printf("expression -> multiplicative_expr\n"); }
        |       multiplicative_expr PLUS expression { printf("expression -> multiplicative_expr PLUS expression\n"); }
        |       multiplicative_expr MINUS expression { printf("expression -> multiplicative_expr MINUS expression\n"); }
        ;

multiplicative_expr: term { printf("multiplicative_expr -> term\n"); }
        |       term MULT multiplicative_expr { printf("multiplicative_expr -> term MULT multiplicative_expr\n"); }
        |       term DIV multiplicative_expr { printf("multiplicative_expr -> term DIV multiplicative_expr\n"); }
        |       term MOD multiplicative_expr { printf("multiplicative_expr -> term MOD multiplicative_expr\n"); }
        ;

term:           var { printf("term -> vars\n"); }
        |       NUMBER { printf("term -> NUMBER %d\n", $1); }
        |       L_PAREN expression R_PAREN { printf("term -> L_PAREN expressions R_PAREN\n"); }
        |       MINUS var { printf("term -> NEGATIVE vars\n"); }
        |       MINUS NUMBER { printf("term -> NEGATIVE NUMBER %d\n", $2); }
        |       MINUS L_PAREN expression R_PAREN { printf("term -> NEGATIVE L_PAREN expressions R_PAREN\n"); }
        |       identifier L_PAREN expressions R_PAREN { printf("term -> identifiers L_PAREN expressions R_PAREN\n"); }
        ;

vars:           var COMMA vars { printf("vars -> var COMMA vars\n"); }
        |       var { printf("vars -> var\n"); }
        ;

var:            identifier { printf("var -> identifiers\n"); }
        |       identifier L_SQUARE_BRACKET expression R_SQUARE_BRACKET { printf("var -> identifiers L_SQUARE_BRACKET expressions R_SQUARE_BRACKET\n"); }
        ;
        
%%

int main( int argc, char **argv ){
        if( argc > 1 ) 
        {
                yyin = fopen( argv[1], "r" );
                if( yyin == NULL ) printf( "error opening file" );
        }
        yyparse();
        return 0;
}

void yyerror( const char* msg ){ printf( "** line %d, position %d: %s\n", currLine, currPos, msg ); }