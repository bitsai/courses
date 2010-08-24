CC=gcc


# Makefile for a stemmer program.
# Written and tested by Christopher Fox 4/15/91
#
# Directives:
#
#       stemmer         A program to generate a stemmed list of terms from
#                       a file.  The program is executed by typing
#                               stemmer <filename>
#                       where <filename> is an ASCII file to be stemmed.
#
#       nstemmer	The stemmer program modified to retain the .[A-Z]
#                       Smart-style segment delimiters without modification
#
#       all (default)   Make the programs above.
#
#       regression      Execute a regression test.  The stemmer program
#                       should write a list of stemmed words to stdout.
#
#       lint            Run the stemmer code through lint
#


CFLAGS=-O
LDFLAGS=
LIBS=

all:		stemmer nstemmer

stemmer:	stemmer.o stem.o
	$(CC) -o $@ stemmer.o stem.o

stemmer.o:	stemmer.c stem.h
	$(CC) $(CFLAGS) -c stemmer.c

nstemmer:	nstemmer.o stem.o
	$(CC) -o $@ nstemmer.o stem.o

nstemmer.o:	nstemmer.c stem.h
	$(CC) $(CFLAGS) -c nstemmer.c

stem.o:	stem.c stem.h
	$(CC) $(CFLAGS) -c stem.c

lint:
	lint stemmer.c stem.c

regression: 	stemmer testfile
	./stemmer testfile

clean:	
	rm *.o 
