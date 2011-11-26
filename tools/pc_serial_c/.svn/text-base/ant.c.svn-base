#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <termios.h>
#include <fcntl.h>
#include <unistd.h>
#include <signal.h>

#include <pthread.h>
#include <semaphore.h>

#include <errno.h>
#include <ctype.h>

// Include Header Files
#include "types.h"
#include "global.h"

#include "antdefines.h"
#include "antmessage.h"
#include "antlib.h"

#include "queue.h"
#include "ansi.h"

// Globals
cfg_capabilities 	cfgCapb;
sem_t 				sem_argCopy, sem_txRdy;
volatile int 		running = TRUE; // global kill boolean

int ANT_txHandler( void *args );
int ANT_rxHandler( void *args );
int ANT_rxMsg( void *args );
int ANT_parseHRM(UCHAR *data, UCHAR size);

int ANT_responseHandler(UCHAR *data, UCHAR size);

int main (int args, char *arg[])
{
	int fd, rc;
	pthread_t thread_rxHandler, thread_txHandler;
	UCHAR buf[20];
	
	//clearScreen();
	
	if (args <= 1)
	{
		printf("Usage: ./ant <device>\n\n");
		exit(0);
	}	
	else if ((fd = ANT_init(arg[1])) == RETURN_ERROR)
	{
		perror("ANT_init");
		exit(0);
	}
		
	sem_init( &sem_txRdy, PTHREAD_PROCESS_SHARED, 1 );	// create the thread delay semaphore	
	sem_init( &sem_argCopy, PTHREAD_PROCESS_SHARED, 1 );	// create the thread delay semaphore	
	
	rc = pthread_create( &thread_rxHandler, NULL, (void*) &ANT_rxHandler, (void*) &fd );
	rc = pthread_create( &thread_txHandler, NULL, (void*) &ANT_txHandler, (void*) &fd );
	
	ANT_send(1+1, MESG_SYSTEM_RESET_ID, 0x00);	
	ANT_send(1+2, MESG_REQUEST_ID, CHAN0, MESG_CAPABILITIES_ID);
	ANT_send(1+2, MESG_REQUEST_ID, CHAN0, 0x3D);	// ??
	ANT_send(1+3, MESG_ASSIGN_CHANNEL_ID, CHAN0, 0x00, NET0); // chan, chtype (0=wildcard?), network
	ANT_send(1+5, MESG_CHANNEL_ID_ID, CHAN0, 0x00, 0x00, 0x00, 0x00); // chan, devno (2byte) (0=wildcard) (little-endian), devtype (0=wildcard), manid (0=wildcard));
	
	// MESG_NETWORK_KEY_ID, net1, GARMIN_KEY 
	buf[0] = MESG_NETWORK_KEY_ID;	
	buf[1] = NET0;
	hstr2hex(&buf[2], NETWORK_KEY, 16);  // dest, orig, size				
	ANT_sendStr(1+9, buf);
	
	ANT_send(1+2, MESG_CHANNEL_SEARCH_TIMEOUT_ID, CHAN0, TIMEOUT);    //	MESG_CHANNEL_SEARCH_TIMEOUT_ID, chan, timeout);	
	
	ANT_send(1+2, MESG_CHANNEL_RADIO_FREQ_ID, CHAN0, FREQ); 	
	
	ANT_send(1+3, MESG_CHANNEL_MESG_PERIOD_ID, CHAN0, PERIOD%256, PERIOD/256); // NOTE: Period = Little-endian
	ANT_send(1+1, MESG_OPEN_CHANNEL_ID, CHAN0);	// MESG_OPEN_CHANNEL_ID, chan  

	while (running)
	{
		sleep(1);
	}
	
	close(fd);
	printf("Exiting...\n");
	

	return 0;
}

int ANT_txHandler( void *args )
{
	int fd;
	memcpy(&fd, args, sizeof(int));
	
	while (TRUE)
	{		
		
		sem_wait( &sem_txRdy );		
		while (txQueue == NULL) usleep(100);
		
		//queue_echo(&txQueue);
		ANT_tx(fd, txQueue->data, txQueue->length);
		
		if (txQueue->data[2] == MESG_SYSTEM_RESET_ID) // This message doesn't send any response back, don't wait for one
		{
			queue_pop(&txQueue);		
			sem_post( &sem_txRdy );		
		}
	}
}

int ANT_rxHandler( void *args )
{
	int n, fd, rc, inmsg = FALSE;
	UCHAR chr, msgN, rxBuf[MAXMSG];
	pthread_t thread_rxMsg;
	
	memcpy(&fd, args, sizeof(int));
	
	printf("%i\n\n", fd);
	
	while (TRUE) 
	{
		n = read(fd, &chr, 1);
		
		if (n != 1)
		{
			perror("Unknown error has occured");
			running = FALSE;
			break;
		}
		else
		{
			if ((chr == MESG_TX_SYNC) && (inmsg == FALSE))
			{
				msgN = 0; // Always reset msg count if we get a sync
				inmsg = TRUE;
				
				rxBuf[msgN] = chr; // second byte will be msg size
				msgN++;				
				printf("RX: [sync]");				
			}
			else if (msgN == 1)
			{
				rxBuf[msgN] = chr; // second byte will be msg size
				msgN++;
				printf("..[0x%02x]", chr );
			}
			else if (msgN == 2)
			{
				rxBuf[msgN] = chr;
				msgN++;
				printf("..[0x%02x]", chr );
			}
			else if (msgN < rxBuf[1]+3) // sync, size, checksum x 1 byte
			{				
				rxBuf[msgN] = chr;
				msgN++;				
				printf("..[0x%02x]", chr );
			}
			else
			{
				inmsg = FALSE;
				rxBuf[msgN] = chr;
				printf("..[0x%02x]\n", chr );
				
				if (checkSum(rxBuf, msgN) == rxBuf[msgN]) // Check if chksum = msg chksum
				{				
					printf("RX: msg received [%i]\n", msgN);
				
					// Handle Message
					sem_wait( &sem_argCopy );				
					rc = pthread_create( &thread_rxMsg, NULL, (void*) &ANT_rxMsg, (void*) rxBuf );				
					pthread_detach(thread_rxMsg);
				}
				else
				{
					printf("RX: chksum mismatch -- data[0x%02x] != msg[0x%02x]\n", rxBuf[msgN], checkSum(rxBuf, msgN));
				}
			}
						
			//printf("size = %i, chr received [%c]\n", size, chr); 
		}
			
		
	} 
	
	pthread_exit( NULL );	
}

int ANT_rxMsg( void *args )
{
	//int i;
	UCHAR rxBuf[MAXMSG], msgID, msgSize;
	UCHAR *msgData;
	
	// copy args
	memcpy(rxBuf, args, sizeof(UCHAR) * MAXMSG);

	// let the handler know it's ok to go - we got the arguments copied
	sem_post( &sem_argCopy );
	
	msgID = rxBuf[2];
	msgSize = rxBuf[1];
	msgData = &rxBuf[3];

	switch (msgID)
	{
		case MESG_RESPONSE_EVENT_ID : 	printf("ID: MESG_RESPONSE_EVENT_ID\n");
										ANT_responseHandler(msgData, msgSize);
										break;
		case MESG_CAPABILITIES_ID   : 	printf("ID: MESG_CAPABILITIES_ID\n");
										ANT_cfgCapabilties(msgData, &cfgCapb, msgSize); // rxBuf[3] .. skip sync, size, msg id
										break;

		case MESG_BROADCAST_DATA_ID :   printf("ID: MESG_BROADCAST_DATA_ID\n");
										ANT_parseHRM(msgData, msgSize);
										break;
										
		default						:	printf("ID: Unknown [0x%02x]\n", msgID);
		
	}
	
	if (txQueue)
	{	
		queue_pop( &txQueue );		
		sem_post( &sem_txRdy );
	}
	
	printf("\n");

	pthread_exit( NULL );		
}

int ANT_parseHRM(UCHAR *data, UCHAR size)
{
	//UCHAR ch = data[0];
	//UCHAR id = data[1];
	//UCHAR code = data[2];
	
	//printf("offset=%d hr=%d\n", data[6], data[7]);
	
	int i;
	
	for (i=0; i < 9; i++)
		printf("o%i=%i ", i, data[i]);
		
	printf("\n");
	
	return 0;
}

int ANT_responseHandler(UCHAR *data, UCHAR size)
{
	UCHAR ch = data[0];
	UCHAR id = data[1];
	UCHAR code = data[2];

	printf("Response Handler (size=%i)\n", size);
	printf("Channel Num  0x%02x\n", ch);
	printf("Message ID   0x%02x ", id);
		
	switch (id)
	{
		case MESG_CHANNEL_SEARCH_TIMEOUT_ID: 
			printf("[MESG_CHANNEL_SEARCH_TIMEOUT_ID]\n");
			break;
		case MESG_ASSIGN_CHANNEL_ID : 	
			printf("[MESG_ASSIGN_CHANNEL_ID]\n");
			break;			
		case MESG_CHANNEL_RADIO_FREQ_ID : 	
			printf("[MESG_CHANNEL_RADIO_FREQ_ID]\n");
			break;		
		case MESG_CHANNEL_MESG_PERIOD_ID :
			printf("[MESG_CHANNEL_MESG_PERIOD_ID]\n");
			break;
		case MESG_OPEN_CHANNEL_ID :
			printf("[MESG_OPEN_CHANNEL_ID]\n");
			break;	
		case MESG_CHANNEL_ID_ID :
			printf("[MESG_CHANNEL_ID_ID]\n");
			break;
		case MESG_NETWORK_KEY_ID :
			printf("[MESG_NETWORK_KEY_ID]\n");
			break;			
		default	: 	
			printf("[unknown]\n");
			break;
	}
	
	printf("Message Code 0x%02x\n", code);
	
	return RETURN_SUCCESS;
}


