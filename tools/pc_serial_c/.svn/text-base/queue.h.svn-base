#ifndef _QUEUE_H_
#define _QUEUE_H_

#include "global.h"
#include "types.h"

typedef struct qitem {
	UCHAR data[MAXMSG];
	int length;
	struct qitem *next;
} queue;

queue *rxQueue, *txQueue;

int queue_add(queue **q, UCHAR *data, int length);
void queue_echo(queue **q);
queue queue_pop(queue **q);

#endif

