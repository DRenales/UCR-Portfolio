FLAGS = -g -Wall -ansi -pedantic -std=c++11
BISON = mini_l.y
FLEX = mini_l.lex
FILES = $(FLEX) $(BISON)
parse: $(FILES)
	bison -d -v $(BISON) 
	flex $(FLEX)
	g++ $(FLAGS) lex.yy.c mini_l.tab.c -lfl -o mini_l
clean:
	rm -f mini_l.output *.tab.* lex.yy.c mini_l