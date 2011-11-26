
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "global.h"
#include "queue.h"

int queue_add(queue **q, UCHAR *data, int length)
{
	queue *tmp, *tmptail;

	tmp = malloc(sizeof(queue));
	memset( tmp, 0, sizeof( tmp ) );
	
	if (tmp == NULL)
		return ( RETURN_ERROR ) ;
	
	memcpy((char*) tmp->data, (char*) data, length);	
	tmp->length = length;
	tmp->next = NULL;

	if (*q == NULL)
	{
		*q = tmp;
	}
	else
	{		
	
		tmptail = *q;
		while(tmptail->next != NULL)  // Find the last element
			tmptail = tmptail->next;

		tmptail->next = tmp; // Update last rxchar pt to new char		
	}	

	return RETURN_SUCCESS;
}

void queue_echo(queue **q)
{
	int i;
	queue *qptr = *q;

	while(qptr!= NULL)
	{
		printf("data: ");
		for (i = 0; i < qptr->length; i++)
		{
			printf("%02x ", qptr->data[i]);
		}
		printf("\n");
		
		qptr = qptr->next;
	}
}

queue queue_pop(queue **q)
{
	queue *qtmp = *q;
	queue data;
	
	qtmp = qtmp->next;					
	
	memcpy(&data, *q, sizeof(queue));
	
	free(*q);
	*q = qtmp;
	
	return data;	
}