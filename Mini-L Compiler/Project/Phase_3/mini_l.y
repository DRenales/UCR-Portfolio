%{
#define YY_NO_UNPUT
#include <stdio.h>
#include <stdlib.h>
#include <map>
#include <string.h>
#include <set>
#include <map>

int DEBUG_OUTPUT = 0;

using std::string;
using std::set;
using std::map;

void yyerror(const char* s);
int numTemps = 0, numLabels = 0;
string newTemp();
string newLabel();
int yylex();

extern int currLine;
extern int currPos;

bool hasMain = false;

map<string, string> varTemp;
map<string, int> arrSize;
set<string> definedFns;
set<string> KEYWORDS { "FUNCTION", "BEGIN_PARAMS", "END_PARAMS", "BEGIN_LOCALS", "END_LOCALS", "INTEGER", "ARRAY", "ENUM", "OF", "IF", "THEN", "ENDIF", "ELSE", "WHILE", "DO", "BEGINLOOP", "ENDLOOP", "CONTINUE", "READ", "WRITE", "AND", "OR", "NOT", "TRUE", "FALSE", "RETURN", "BEGINBODY", "ENDBODY", "SUB", "PLUS", "MULT", "DIV", "MOD", "EQ", "NEQ", "LT", "GT", "LTE", "GTE", "SEMICOLON", "COLON", "COMMA", "L_PAREN", "R_PAREN", "L_SQUARE_BRACKET", "R_SQUARE_BRACKET", "ASSIGN" };

%}

%union{
    int ival;
    char* sval;
    struct S {
        char* code;
    } statement;
    struct E {
        char* place;
        char* code;
        bool isArr;
    } expression;
}


%error-verbose
%start start
%token FUNCTION BEGIN_PARAMS END_PARAMS BEGIN_LOCALS END_LOCALS INTEGER ARRAY ENUM OF IF THEN ENDIF ELSE WHILE DO BEGINLOOP ENDLOOP CONTINUE READ WRITE AND OR NOT TRUE FALSE RETURN BEGINBODY ENDBODY SUB PLUS MULT DIV MOD EQ NEQ LT GT LTE GTE SEMICOLON COLON COMMA L_PAREN R_PAREN L_SQUARE_BRACKET R_SQUARE_BRACKET ASSIGN

/* sets the datatype for the token */
%token <sval> IDENT
%token <ival> NUMBER
%type <expression> function fnIdent ident declarations declaration var_list var expressions expression identifiers
%type <expression> bool_expr relation_and_expr relation_expr relation_sub comp multiplicative_expr term
%type <statement> statement statements

/* set pemdas */
%right ASSIGN
%left OR
%left AND
%right NOT
%left LT LTE GT GTE EQ NEQ
%left PLUS SUB
%left MULT DIV MOD


%%

start: 
    functions {
        if( DEBUG_OUTPUT ){ printf( "start -> functions\n" );  }
    }
;
functions: /* ε */ { 
    if( DEBUG_OUTPUT ){ printf( "functions -> ε\n"); }
    if( !hasMain ){
        printf( "No main function declared\n" );
    }
}
| function functions {
    if( DEBUG_OUTPUT ){ printf( "functions -> function functions\n"); }
}
;

function: FUNCTION fnIdent SEMICOLON BEGIN_PARAMS declarations  END_PARAMS BEGIN_LOCALS declarations  END_LOCALS BEGINBODY statements ENDBODY {
    if( DEBUG_OUTPUT ){ printf( "function -> FUNCTION ident SEMICOLON BEGIN_PARAMS declarations  END_PARAMS BEGIN_LOCALS declarations  END_LOCALS BEGINBODY statements ENDBODY\n"); }
    string t = "func ";
    string s = $2.place;
    t.append( s ).append( "\n" );
    t.append( $2.code );

    if( s == "main" ){
        hasMain = true;
    }
    t.append( $5.code );
    string declares = $5.code;
    int nDecs = 0;
    while( declares.find(".")!= string::npos ) {
        int pos = declares.find( "." );
        declares.replace( pos, 1, "=" );
        string part = ", $" + std::to_string( nDecs ) + "\n";
        nDecs++;
        declares.replace( declares.find( "\n", pos ), 1, part );
    }

    t.append( declares ).append( $8.code );

    string statements = $11.code;
    if( statements.find( "continue" ) != string::npos ){
        printf( "ERROR: Continue outside loop in function %s\n", $2.place);
    }

    //add the states to string
    t.append( statements ).append( "endfunc\n\n" );
    printf( t.c_str() );
}
;
declarations: /* ε */ { 
    if( DEBUG_OUTPUT ){ printf( "declarations -> ε\n"); }
    $$.code = strdup( "" );
    $$.place = strdup( "" );
 }
| declaration SEMICOLON declarations {
    if( DEBUG_OUTPUT ){ printf( "declarations -> declaration SEMICOLON declarations\n"); }
    string temp;
    temp.append( $1.code ).append( $3.code );
    $$.code = strdup( temp.c_str() );
    $$.place = strdup( "" );
}
;

declaration: identifiers COLON INTEGER { 
    if( DEBUG_OUTPUT ) printf( "declaration -> identifiers COLON INTEGER\n"); 
    size_t left = 0;
    size_t right = 0;
    string parse( $1.place );
    string temp;
    bool isDone = false;
    while( !isDone ){
        right = parse.find( "|", left ); //find other possible idents
        string ident;
        if( right == string::npos ){
            ident = parse.substr( left, right );
            temp.append( ". " ).append( ident ).append( "\n" );
            isDone = true;
        } else{
            ident = parse.substr( left, right - left );
            temp.append( ". " ).append( ident ).append( "\n" );
        }
        //check it isnt reserved
        if( KEYWORDS.find( ident ) != KEYWORDS.end() ){
            string s = "Identifier "+ ident + " is a resevered keyword. (declaration -> identifiers COLON INTEGER)\n";
            yyerror( s.c_str() );
        }
        //check it hasnt been defined as a function or a variable
        if( definedFns.find( ident ) != definedFns.end() || varTemp.find( ident ) != varTemp.end() ){
            string s = "Identifier " + s + " is already declared. (declaration -> identifiers COLON INTEGER)\n";
            yyerror( s.c_str() );
        } else {
            varTemp[ident] = ident;
            arrSize[ident] = 1;
        }
        left = right + 1;
    }

    $$.code = strdup( temp.c_str() );
    $$.place = strdup( "" );
        /** old code removed and optimized cause idk
        temp.append( ". " ); //add the `. k`
        if( right == string::npos ){ //only one ident 
            string ident = parse.substr( left, right );
            //check it isnt a keyword
            if( KEYWORDS.find( ident ) != KEYWORDS.end() ){
                printf( "Identifier %s is a resevered keyword. (declaration -> identifiers COLON INTEGER)\n", ident.c_str() );
            }
            //check it hasnt been defined as a function or a variable
            if( definedFns.find( ident ) != definedFns.end() || varTemp.find( ident ) != varTemp.end() ){
                printf( "Identifier %s is already declared. (declaration -> identifiers COLON INTEGER)\n", ident.c_str() );
            } else {
                varTemp[ident] = ident;
                arrSize[ident] = 1;
            }
            temp.append( ident );
            isDone = true;
        } else { //there are more idents on the same line
            string ident = parse.substr( left, right - left );

            if( KEYWORDS.find( ident ) != KEYWORDS.end() ){
                printf( "Identifier %s is a resevered keyword. (declaration -> identifiers COLON INTEGER)\n", ident.c_str() );
            }

            if( definedFns.find( ident ) != definedFns.end() || varTemp.find( ident ) != varTemp.end() ){
                printf( "Identifier %s is already declared. (declaration -> identifiers COLON INTEGER)\n", ident.c_str() );
            } else {
                varTemp[ident] = ident;
                arrSize[ident] = 1;
            }
            temp.append( ident );
            left = right + 1;
        }
        temp.append( "\n" );
    }
    $$.code = strdup( temp.c_str() );
    string s = "";
    $$.place = strdup( "" );
    */
}
| identifiers COLON ARRAY L_SQUARE_BRACKET NUMBER R_SQUARE_BRACKET OF INTEGER { 
    if( DEBUG_OUTPUT ) printf( "declaration -> identifiers COLON ARRAY L_SQUARE_BRACKET NUMBER R_SQUARE_BRACKET OF NUMBER\n");
    int index = $5;
    if( index <= 0 ){
        string s = "Array size can not be less than 1";
        yyerror( s.c_str() );
    }

    size_t left = 0;
    size_t right = 0;
    string parse( $1.place );
    string t;
    bool isDone = false;

    while( !isDone ){
        right = parse.find( "|", left );
        if( right == string::npos ){ //only one ident 
            string ident = parse.substr( left, right );
            t.append( ".[] " ); //add the `. k`
            //check it isnt a keyword
            if( KEYWORDS.find( ident ) != KEYWORDS.end() ){
                printf( "Identifier %s is a resevered keyword. (declaration -> identifiers COLON ARRAY L_SQUARE_BRACKET NUMBER R_SQUARE_BRACKET OF NUMBER)\n", ident.c_str() );
            }
            //check it hasnt been defined as a function or a variable
            if( definedFns.find( ident ) != definedFns.end() || varTemp.find( ident ) != varTemp.end() ){
                printf( "Identifier %s is already declared. (declaration -> identifiers COLON ARRAY L_SQUARE_BRACKET NUMBER R_SQUARE_BRACKET OF NUMBER)\n", ident.c_str() );
            } else {
                varTemp[ident] = ident;
                arrSize[ident] = index;
            }
            t.append( ident ).append( ", " ).append( std::to_string($5) );
            isDone = true;
        }
        else {
            string ident = parse.substr( left, right - left );
            t.append( ".[] " ); //add the `. k`
            //check it isnt a keyword
            if( KEYWORDS.find( ident ) != KEYWORDS.end() ){
                printf( "Identifier %s is a resevered keyword. (declaration -> identifiers COLON ARRAY L_SQUARE_BRACKET NUMBER R_SQUARE_BRACKET OF NUMBER)\n", ident.c_str() );
            }
            //check it hasnt been defined as a function or a variable
            if( definedFns.find( ident ) != definedFns.end() || varTemp.find( ident ) != varTemp.end() ){
                printf( "Identifier %s is already declared. (declaration -> identifiers COLON ARRAY L_SQUARE_BRACKET NUMBER R_SQUARE_BRACKET OF NUMBER)\n", ident.c_str() );
            } else {
                varTemp[ident] = ident;
                arrSize[ident] = index;
            }
            t.append( ident ).append( ", " ).append( std::to_string($5) );
            left = right + 1;
        }
        t.append( "\n" );
    }

    $$.code = strdup( t.c_str() );
    $$.place = strdup( "" );

}
| identifiers COLON ENUM L_PAREN identifiers R_PAREN {
    /*if( DEBUG_OUTPUT )*/ printf( "declaration -> identifiers COLON ENUM L_PAREN identifiers R_PAREN\n"); 
}
;

identifiers: ident {
    if( DEBUG_OUTPUT ) printf( "identifiers -> ident\n");
    $$.place = strdup( $1.place );
    $$.code = strdup( "" );
}
| ident COMMA identifiers {
    if( DEBUG_OUTPUT ) printf( "identifiers -> ident COMMA identifiers\n"); 
    string s;
    s.append( $1.place ).append( "|" ).append( $3.place );
    $$.place = strdup( s.c_str() );
    $$.code = strdup( "" );
}
;

ident: IDENT {
    if( DEBUG_OUTPUT ) printf( "ident -> IDENT %s\n", $1 ); 
    $$.place = strdup( $1 );
    $$.code = strdup( "" );
}
;

fnIdent: IDENT {
    if( DEBUG_OUTPUT ) printf( "fnIdent -> IDENT %s\n", $1 ); 
    if( definedFns.find( $1 ) != definedFns.end() ){
        string s;
        s.append( "function ").append( $1 ).append( " is declared already.\n");
        yyerror( s.c_str() );
    } else {
        definedFns.insert( $1 );
    }
    $$.place = strdup( $1 );
    $$.code = strdup( "" );
}
;

statements: statement SEMICOLON {
    if(DEBUG_OUTPUT) printf( "statements -> ε\n"); 
    $$.code = strdup( $1.code );
}
| statement SEMICOLON statements {
    if(DEBUG_OUTPUT) printf( "statements -> statement SEMICOLON statements\n"); 
    string temp;
    temp.append( $1.code );
    temp.append( $3.code );
    $$.code = strdup( temp.c_str() );
}
;

statement: var ASSIGN expression {
    if( DEBUG_OUTPUT ) printf( "statement -> var ASSIGN expression\n"); 
    string t;
    t.append( $1.code ).append( $3.code );
    string middle = $3.place;
    if($1.isArr && $3.isArr ){
        middle = newTemp();
        t.append( ". " + middle + "\n" );
        t.append( "[]= " + middle + ", ").append( $3.place ).append( "\n" );
        t.append ("[]= ");
    } else if ( $1.isArr ){
        t += "[]= ";
    } else if ( $3.isArr ){
        t += "[]= ";
    } else {
        t += "= ";
    }
    t.append( $1.place ).append( ", " ).append( middle ).append( "\n" );

    $$.code = strdup( t.c_str() );
}
| IF bool_expr THEN statements ENDIF {
    if( DEBUG_OUTPUT) printf( "statement -> IF bool_expr THEN statements ENDIF\n"); 
    string start = newLabel();
    string end = newLabel();
    string t;

    t.append($2.code );
    //if true go to start | ?:= label, predicate
    t.append( "?:= " + start + ", " ).append( $2.place ).append( "\n" );
    //if false go to end
    t.append( ":= " + end + "\n");
    //then
    t.append( ": " + start + "\n" );
    t.append( $4.code );
    t.append( ": " + end + "\n" );

    $$.code = strdup( t.c_str() );
}
| IF bool_expr THEN statements ELSE statements ENDIF {
    if( DEBUG_OUTPUT) printf( "statement -> IF bool_expr THEN statements ELSE statements ENDIF\n"); 
    string start = newLabel();
    string end = newLabel();
    string t;

    t.append($2.code );
    //if true go to start | ?:= label, predicate
    t.append( "?:= " + start + ", " ).append( $2.place ).append( "\n" );
    //else code
    t.append( $6.code );
    t.append( ":= " + end + "\n");
    //then
    t.append( ": " + start + "\n" );
    t.append( $4.code );
    t.append( ": " + end + "\n" );

    $$.code = strdup( t.c_str() );
    
}
| WHILE bool_expr BEGINLOOP statements ENDLOOP {
    if( DEBUG_OUTPUT ) printf( "statement -> WHILE bool_expr BEGINLOOP statements ENDLOOP\n"); 

    string t;
    string whileL = newLabel();
    string loopL = newLabel();
    string endL = newLabel();

    //replace continue with beginWhile label
    string contJump = ":= " + whileL;
    string state = $4.code;
    size_t pos = state.find("continue");
    while( pos != string::npos ){
        state.replace( pos, 8, contJump );
        pos = state.find( "continue", pos );
    }

    //begin loop
    t.append( ": " + whileL + "\n" );
    t.append( $2.code );
    t.append( "?:= " + loopL + ", " ).append( $2.place ).append( "\n" );
    t.append( ":= " + endL + "\n" );
    t.append( ": " + loopL + "\n" );
    t.append( state );
    t.append( ":= " + whileL + "\n" );
    t.append( ": " + endL + "\n" );

    $$.code = strdup( t.c_str() );
}
| DO BEGINLOOP statements ENDLOOP WHILE bool_expr {
    if( DEBUG_OUTPUT ) printf( "statement -> DO BEGINLOOP statements ENDLOOP WHILE bool_expr\n");
    string t;
    string beginL = newLabel();
    string whileL = newLabel();

    //replace continue with beginWhile label
    string contJump = ":= " + whileL;
    string state = $3.code;
    size_t pos = state.find("continue");
    while( pos != string::npos ){
        state.replace( pos, 8, contJump );
        pos = state.find( "continue", pos );
    }

    t.append( ": " + beginL + "\n");
    t.append( state );
    t.append( ": " + whileL + "\n" );
    t.append( $6.code );
    t.append( "?:= " + beginL + ", " ).append( $6.place ).append( "\n" );

    $$.code = strdup( t.c_str() );

}
| READ var_list {
    if(DEBUG_OUTPUT) printf( "statement -> READ var_list\n"); 
    string t = $2.code;
    size_t pos = t.find( "|", 0 );
    while( pos != string::npos ){
        t.replace( pos, 1, "<" );
        pos = t.find( "|", pos );
    }
    $$.code = strdup( t.c_str() );
}
| WRITE var_list {
    if(DEBUG_OUTPUT) printf( "statement -> WRITE var_list\n"); 
    string t = $2.code;
    size_t pos = t.find( "|", 0 );
    while( pos != string::npos ){
        t.replace( pos, 1, ">" );
        pos = t.find( "|", pos );
    }
    $$.code = strdup( t.c_str() );
}
| CONTINUE {
    if( DEBUG_OUTPUT) printf( "statement -> CONTINUE\n"); 
    $$.code = "continue\n";
}
| RETURN expression {
    if( DEBUG_OUTPUT) printf( "statement -> RETURN expression\n"); 
    string t;
    t.append( $2.code );
    t.append( "ret " ).append( $2.place ).append( "\n" );
    $$.code = strdup( t.c_str() );
}
;

bool_expr: relation_and_expr {
    if(DEBUG_OUTPUT) printf( "bool_expr -> relation_and_expr\n"); 
    $$.place = strdup( $1.place );
    $$.code = strdup( $1.code );
}
| relation_and_expr OR bool_expr {
    if( DEBUG_OUTPUT) printf( "bool_expr -> relation_and_expr OR bool_expr\n"); 
    string dst = newTemp();
    string t;

    t.append( $1.code ).append( $3.code );
    t.append( ". " + dst + "\n" );
    t.append( "|| " + dst + ", " ).append( $1.place ).append( ", ").append( $3.place ).append( "\n" );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
;

relation_and_expr: relation_expr {
    if(DEBUG_OUTPUT) printf( "relation_and_expr -> relation_expr\n"); 
    $$.place = strdup($1.place);
    $$.code = strdup($1.code);
}
| relation_expr AND relation_and_expr {
    if(DEBUG_OUTPUT) printf( "bool_expr -> relation_expr AND relation_and_expr\n"); 
    string dst = newTemp();
    string t;

    t.append( $1.code ).append( $3.code );
    t.append( ". " + dst + "\n" );
    t.append( "&& " + dst + ", " ).append( $1.place ).append( ", ").append( $3.place ).append( "\n" );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
;

relation_expr: relation_sub {
    if(DEBUG_OUTPUT) printf( "relation_expr -> relation_sub\n"); 
    $$.place = strdup($1.place);
    $$.code = strdup($1.code);
}
| NOT relation_sub  {
    if(DEBUG_OUTPUT) printf( "relation_expr -> NOT relation_sub\n"); 
    string dst = newTemp();
    string t;

    t.append( $2.code );
    t.append( ". " + dst + "\n" );
    t.append( "! " + dst + ", " ).append( $2.place ).append( "\n" );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
;

relation_sub: expression comp expression {
    if(DEBUG_OUTPUT) printf( "relation_sub -> expression comp expression\n"); 
    string t;
    string dst = newTemp();

    t.append( $1.code ).append( $3.code );
    t.append( ". " + dst + "\n");
    // > / < / == / != , dst, src, src
    t.append( $2.place ).append( " " + dst + ", " ).append( $1.place ).append(", ").append( $3.place ).append( "\n" );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
| TRUE {
    if(DEBUG_OUTPUT) printf( "relation-sub -> TRUE\n"); 
    string t = "1";
    $$.place = strdup( t.c_str() );
    $$.code = strdup( "" );
}
| FALSE {
    if(DEBUG_OUTPUT) printf( "relation-sub -> FALSE\n"); 
    string t = "0";
    $$.place = strdup( t.c_str() );
    $$.code = strdup( "" );
}
| L_PAREN bool_expr R_PAREN {
    if(DEBUG_OUTPUT) printf( "relation_sub -> L_PAREN bool_exprr R_PAREN\n"); 
    $$.place = strdup( $2.place );
    $$.code = strdup( $2.code );
}
;

comp: EQ {
    if(DEBUG_OUTPUT) printf( "comp -> EQ\n"); 
    string t = "==";
    $$.place = strdup( t.c_str() );
    $$.code = strdup( "" );
}
| NEQ  {
    if(DEBUG_OUTPUT) printf( "comp -> NEQ\n"); 
    string t = "!=";
    $$.place = strdup( t.c_str() );
    $$.code = strdup( "" );
}
| LT   {
    if(DEBUG_OUTPUT) printf( "comp -> LT\n"); 
    string t = "<";
    $$.place = strdup( t.c_str() );
    $$.code = strdup( "" );
}
| GT   {
    if(DEBUG_OUTPUT) printf( "comp -> GT\n"); 
    string t = ">";
    $$.place = strdup( t.c_str() );
    $$.code = strdup( "" );
}
| LTE  {
    if(DEBUG_OUTPUT) printf( "comp -> LTE"); 
    string t = "<=";
    $$.place = strdup( t.c_str() );
    $$.code = strdup( "" );
}
| GTE  {
    if(DEBUG_OUTPUT) printf( "comp -> GTE\n"); 
    string t = ">=";
    $$.place = strdup( t.c_str() );
    $$.code = strdup( "" );
}
;

var: ident {
    if( DEBUG_OUTPUT ) { printf( "var -> ident\n" );} 
    string id = $1.place;
    //check undeclared vars check1
    if( definedFns.find( id) == definedFns.end() && varTemp.find( id ) == varTemp.end() ){ //was not found
        string s = "Using an undeclared variable (var->ident)";
        s.append( $1.place );
        yyerror( s.c_str() );
    }
    else if( arrSize[id] > 1 ){ //check if array check6
        string s = "Identifier did not provide index for array identifer " + id + "(var->ident)";
        yyerror( s.c_str() );
    }
    $$.place = strdup( id.c_str() );
    $$.code = strdup( "" );
    $$.isArr = false;
}
| ident L_SQUARE_BRACKET expression R_SQUARE_BRACKET {
    if(DEBUG_OUTPUT) printf( " var -> ident L_SQUARE_BRACKET expression R_SQUARE_BRACKET\n" ); 
    string id = $1.place;
    //check undeclared vars check1
    if( definedFns.find( id) == definedFns.end() && varTemp.find( id ) == varTemp.end() ){ //was not found
        string s = "Using an undeclared variable ";
        s.append( $1.place );
        yyerror( s.c_str() );
    }
    else if( arrSize.find( id ) == arrSize.end() ){ //check if array check6
        string s = "Identifier is not an array identifer " + id;
        yyerror( s.c_str() );
    }
    string t;
    t.append( $1.place ).append( ", " ).append( $3.place );
    $$.code = strdup( $3.code );
    $$.place = strdup( t.c_str() );
    $$.isArr = true;
}
;

//only used in read (<) /write (>)  .[]</> dst/src idx | .</> dst/src
//using | as a placeholder to carry up to statements
var_list: var {
    if(DEBUG_OUTPUT) printf( "var_list -> var\n" );
    string t;
    t.append( $1.code );
    if( $1.isArr ){
        t.append( ".[]| ");
    }
    else {
        t.append( ".| " );
    }
    t.append( $1.place ).append( "\n" );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( "" );
}
| var COMMA var_list {
    if(DEBUG_OUTPUT) printf( "var_list -> var COMMA var_list\n" ); 
    string t;
    t.append( $1.code );
    if( $1.isArr ){
        t.append( ".[]| ");
    }
    else {
        t.append( ".| " );
    }
    t.append( $1.place ).append( "\n" ).append( $3.code );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( "" );
}
;
expression: multiplicative_expr {
    if( DEBUG_OUTPUT ) printf( "expression -> multiplicative_expr\n"); 
    $$.code = strdup( $1.code );
    $$.place = strdup( $1.place );
}
| multiplicative_expr PLUS expression {
    if( DEBUG_OUTPUT) printf( "expression -> multiplicative_expr PLUS expression\n");
    string t;
    string dst = newTemp();

    t.append( $1.code ).append( $3.code );
    t.append( ". " + dst + "\n" );
    t.append( "+ " + dst + ", " ).append( $1.place ).append( ", " ).append( $3.place ).append( "\n");

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
| multiplicative_expr SUB expression {
    if(DEBUG_OUTPUT) printf( "expression -> multiplicative_expr SUB expression\n");
    string t;
    string dst = newTemp();

    t.append( $1.code ).append( $3.code );
    t.append( ". " + dst + "\n" );
    t.append( "- " + dst + ", " ).append( $1.place ).append( ", " ).append( $3.place ).append( "\n");

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
;

multiplicative_expr: term {
    if(DEBUG_OUTPUT) printf( "multiplicative_expr -> term\n"); 
    $$.code = strdup( $1.code );
    $$.place = strdup( $1.place );
}
| term MULT multiplicative_expr {
    if( DEBUG_OUTPUT) printf( "multiplicative_expr -> term MULT multiplicative_expr\n"); 
    string t;
    string dst = newTemp();

    t.append( $1.code ).append( $3.code );
    t.append( ". " + dst + "\n" );
    t.append( "* " + dst + ", " ).append( $1.place ).append( ", " ).append( $3.place ).append( "\n");

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
| term DIV multiplicative_expr  {
    if( DEBUG_OUTPUT ) printf( "multiplicative_expr -> term DIV multiplicative_expr\n"); 
    string t;
    string dst = newTemp();

    t.append( $1.code ).append( $3.code );
    t.append( ". " + dst + "\n" );
    t.append( "/ " + dst + ", " ).append( $1.place ).append( ", " ).append( $3.place ).append( "\n");

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
| term MOD multiplicative_expr  {
    if( DEBUG_OUTPUT ) printf( "multiplicative_expr -> term MOD multiplicative_expr\n");
    string t;
    string dst = newTemp();

    t.append( $1.code ).append( $3.code );
    t.append( ". " + dst + "\n" );
    t.append( "% " + dst + ", " ).append( $1.place ).append( ", " ).append( $3.place ).append( "\n");

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
;

term: var {
    if( DEBUG_OUTPUT ) printf( "term -> var\n");
    string t;
    string dst = newTemp();
    if( $$.isArr ){
        t.append( $1.code );
        t.append( ". ").append( dst ).append( "\n" );
        t.append( "=[] ").append( dst ).append(", ").append( $1.place).append( "\n" );

        $$.code = strdup( t.c_str() );
        $$.place = strdup( dst.c_str() );
        $$.isArr = false; //because we change the place from the array to a loaded temp
    }
    else {
        t.append( ". " + dst + "\n" );
        t.append( "= " + dst + ", " ).append( $1.place).append( "\n");
        t.append( $1.code );
    }
    if( varTemp.find( $1.place ) != varTemp.end() ){
        varTemp[$1.place] = dst;
    }
    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
| SUB var { 
    if(DEBUG_OUTPUT) printf( "term -> SUB var\n" ); 
    string dst = newTemp();
    string t;

    if( $2.isArr ){
        t.append( $2.code );
        t.append( ". " + dst + "\n");
        t.append( "=[] " + dst + ", ").append( $2.place).append( "\n" );
    }
    else {
        t.append( ". " + dst + "\n");
        t.append( "= " + dst + ", " ).append( $2.place ).append( "\n" );
    }
    if( varTemp.find( $2.place ) != varTemp.end() ){
        varTemp[$2.place] = dst;
    }
    t.append( "* ").append( dst + ", " + dst ).append( ", -1\n");

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
| NUMBER {
    if(DEBUG_OUTPUT) printf( "term -> NUMBER\n" ); 
    string s = std::to_string( $1 );
    string dst = newTemp();
    string t;
    t.append( ". " + dst + "\n");
    t.append( "= " + dst + ", " + s + "\n");
    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
| SUB NUMBER {
    if(DEBUG_OUTPUT) printf( "term -> SUB NUMBER\n" ); 
    string s = "-" + std::to_string( $2 );
    string dst = newTemp();
    string t;
    t.append( ". " + dst + "\n");
    t.append( "= " + dst + ", " + s + "\n");
    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
| L_PAREN expression R_PAREN {
    if( DEBUG_OUTPUT) printf( "term -> L_PAREN expression R_PAREN\n" ); 
    $$.code = strdup( $2.code );
    $$.place = strdup( $2.place );
}
| SUB L_PAREN expression R_PAREN {
    if( DEBUG_OUTPUT ) printf( "term -> SUB L_PAREN expression R_PAREN\n" ); 
    string t;
    t.append( $3.code ).append( "* ").append($3.place).append( ", ").append( $3.place);
    t.append( ", -1\n" );

    $$.code= strdup( t.c_str() );
    $$.place = strdup( $3.place );
}
| ident L_PAREN expressions R_PAREN {
    if( DEBUG_OUTPUT) printf( "term -> ident L_PAREN expressions R_PAREN\n" ); 
    string t;
    //check undeclared fn
    if( definedFns.find( $1.place ) == definedFns.end() ) {
        string s = "Using an undeclared function ";
        s.append( $1.place );
        yyerror( s.c_str() );
    }

    string dst = newTemp();
    //. __temp__11
    //call fibonacci, __temp__11
    t.append( $3.code );
    t.append( ". " + dst + "\n" );
    t.append( "call " ).append($1.place ).append( ", " + dst + "\n" );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( dst.c_str() );
}
;

expressions: {
    if( DEBUG_OUTPUT ) printf( "expressions -> \n");
    $$.code = strdup( "" );
    $$.place = strdup( "" );
}
| expression {
    if(DEBUG_OUTPUT ) printf( "expressions -> expression\n"); 
    string t;
    t.append( $1.code );
    t.append( "param " ).append( $1.place ).append( "\n" );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( "" );
}
| expression COMMA expressions {
    if(DEBUG_OUTPUT) printf( "expression COMMA expressions\n"); 
    string t;

    t.append( $1.code );
    t.append( "param ").append( $1.place ).append( "\n" );
    t.append( $3.code );

    $$.code = strdup( t.c_str() );
    $$.place = strdup( "" );
}

%%

string newTemp(){
    string temp = "_t" + std::to_string( numTemps );
    numTemps++;
    return temp;
}

string newLabel(){
    string temp = "_l" + std::to_string( numLabels );
    numLabels++;
    return temp;
}

void yyerror( const char* msg ){
    printf( "** line %d, position %d: %s\n", currLine, currPos, msg );
}
