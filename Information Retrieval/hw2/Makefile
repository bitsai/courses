# Makefile for 'stemmed' and 'tokenized' histograms by Devin Balkcom
# (TA for CS 466)  balkcom@jhu.edu

CC = gcc
STEMMER = ./stemmer/nstemmer
LEX_LIB = l

all: tools data misc

tools:  token1

data:  cacm.stemmed.hist cacm.tokenized.hist query.stemmed.hist query.tokenized.hist tools

misc:  common_words.stemmed


# this is a bit of a hack and will spit out some warnings, but works
token1: token1.l
	$(LEX) -t token1.l >token1.c
	gcc -c -o token1.o token1.c
	gcc -o token1 token1.o -l$(LEX_LIB)


# Rules for tokenizing and vectorizing the CACM data:

cacm.stemmed.hist:  cacm.stemmed
	cat cacm.stemmed | ./make_hist.prl >cacm.stemmed.hist 

cacm.stemmed:  cacm.raw
	$(STEMMER) cacm.raw >cacm.stemmed

cacm.tokenized.hist:  cacm.tokenized
	cat cacm.tokenized | ./make_hist.prl >cacm.tokenized.hist 

cacm.tokenized: cacm.raw 
	./tokenize cacm

# Rules for tokenizing and vectorizing the query data:

query.stemmed.hist:  query.stemmed
	cat query.stemmed | ./make_hist.prl >query.stemmed.hist 

query.stemmed:  query.raw
	$(STEMMER) query.raw >query.stemmed

query.tokenized.hist:  query.tokenized
	cat query.tokenized | ./make_hist.prl >query.tokenized.hist 

query.tokenized: query.raw
	./tokenize query

# Rules for building the necessary tools from the sources:

token1: token1.o
	$(CC) -o token1 token1.o -lfl

common_words.stemmed:  common_words
	$(STEMMER) common_words >common_words.stemmed

# Miscellaneous rules:

clean:
	rm -f *~ *.hist *.tokenized *.stemmed *.o token1 *~ token1.c lex.yy.c		


.SUFFIXES: .o .c .l
.c.o: 
	$(CC) -c $<
