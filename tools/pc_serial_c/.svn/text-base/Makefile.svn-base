CC=gcc
CFLAGS=-Wall -g
OPTIMIZED=-O
LIBS=-lpthread -lm

all: ant

##### ANT
ant: clean ant.o ansi.o antlib.o queue.o
	${CC} ${CFLAGS} ${LIBS} ant.o ansi.o antlib.o queue.o -o ant

ant.o: ant.c 
	${CC} ${CFLAGS} -c ant.c

ansi.o: ansi.c 
	${CC} ${CFLAGS} -c ansi.c

antlib.o: antlib.c 
	${CC} ${CFLAGS} -c antlib.c
	
queue.o: queue.c
	${CC} ${CFLAGS} -c queue.c	

##### CLEAN
clean:
	@rm -rf *.o ant  ; 

