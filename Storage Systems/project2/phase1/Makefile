# This is an example Makefile that shows how to link
# the DiskSim libraries to your code.

GCC = gcc
GCCOPTS = -g
OBJS = partition.o
BIN = partition

all: partition

clean: 
	- rm -f ${BIN} *.o

partition: ${OBJS} ext2_fs.h genhd.h
	${GCC} -lm ${GCCOPTS} ${OBJS} -o ${BIN}

partition.o: ext2_fs.h genhd.h partition.c
	${GCC} ${GCCOPTS} partition.c -c