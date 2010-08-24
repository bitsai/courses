# This is an example Makefile that shows how to link
# the DiskSim libraries to your code.

GCC = gcc
GCCOPTS = -g
LIBS = DiskSim.a
OBJS = test.o
BIN = test

all: test

clean: 
	- rm -f ${BIN} *.o

test: ${LIBS} ${OBJS} project1.h
	${GCC} -lm ${GCCOPTS} ${OBJS} ${LIBS} -o ${BIN} 

test.o: project1.h test.c
	${GCC} ${GCCOPTS} test.c -c