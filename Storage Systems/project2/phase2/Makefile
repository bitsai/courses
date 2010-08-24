# This is an example Makefile that shows how to link
# the DiskSim libraries to your code.

GCC = gcc
GCCOPTS = -g
OBJS = phase2.o
BIN = phase2

all: phase2

clean: 
	- rm -f ${BIN} *.o

phase2: ${OBJS} ext2_fs.h genhd.h
	${GCC} -lm ${GCCOPTS} ${OBJS} -o ${BIN}

phase2.o: ext2_fs.h genhd.h partition.c
	${GCC} ${GCCOPTS} phase2.c -c