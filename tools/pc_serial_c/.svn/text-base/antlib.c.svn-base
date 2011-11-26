#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include <termios.h>
#include <fcntl.h>
#include <unistd.h>
#include <signal.h>

#include <string.h>

#include "global.h"

#include "antdefines.h"
#include "antmessage.h"
#include "antlib.h"
#include "queue.h"

int ANT_init(char *devname)
{
	struct termios tp;
	int fd;
	
	fd = open(devname, O_RDWR | O_NOCTTY );
	if (fd < 0) {
		perror(devname);
		return RETURN_ERROR;
	}

	
	bzero(&tp, sizeof(tp));
	//tcgetattr(fd, &tp);
	
	//tp.c_iflag &= ~(IGNBRK|BRKINT|PARMRK|ISTRIP|INLCR|IGNCR|ICRNL|IXON|IXOFF|IXANY|INPCK|IUCLC);

	/*
	  IGNPAR  : ignore bytes with parity errors
	  ICRNL   : map CR to NL (otherwise a CR input on the other computer
				will not terminate input)
	  otherwise make device raw (no other input processing)
	*/
	tp.c_iflag = 0;
	tp.c_oflag  = 0;
	tp.c_lflag  = 0;
	
	/* 
	  BAUDRATE: Set bps rate. You could also use cfsetispeed and cfsetospeed.
	  CRTSCTS : output hardware flow control (only used if the cable has
				all necessary lines. See sect. 7 of Serial-HOWTO)
	  CS8     : 8n1 (8bit,no parity,1 stopbit)
	  CLOCAL  : local connection, no modem contol
	  CREAD   : enable receiving characters
	*/	
	tp.c_cflag |= CS8 | CLOCAL | CREAD; //  | CRTSCTS

	cfsetispeed(&tp, BAUD);
	cfsetospeed(&tp, BAUD);
	
	/*
	VMIN = 0 and VTIME = 0
    This is a completely non-blocking read - the call is satisfied immediately directly 
	from the driver's input queue. If data are available, it's transferred to the caller's 
	buffer up to nbytes and returned. Otherwise zero is immediately returned to indicate "no data". 
	We'll note that this is "polling" of the serial port, and it's almost always a bad idea. 
	If done repeatedly, it can consume enormous amounts of processor time and is highly inefficient. 
	Don't use this mode unless you really, really know what you're doing.  
	
	VMIN = 0 and VTIME > 0
    This is a pure timed read. If data are available in the input queue, it's transferred to the caller's 
	buffer up to a maximum of nbytes, and returned immediately to the caller. Otherwise the driver blocks 
	until data arrives, or when VTIME tenths expire from the start of the call. If the timer expires without 
	data, zero is returned. A single byte is sufficient to satisfy this read call, but if more is available in 
	the input queue, it's returned to the caller. Note that this is an overall timer, not an intercharacter one.
	
	VMIN > 0 and VTIME > 0
    A read() is satisfied when either VMIN characters have been transferred to the caller's buffer, or when VTIME 
	tenths expire between characters. Since this timer is not started until the first character arrives, this 
	call can block indefinitely if the serial line is idle. This is the most common mode of operation, and we 
	consider VTIME to be an intercharacter timeout, not an overall one. This call should never return zero bytes read. 
	
	VMIN > 0 and VTIME = 0
    This is a counted read that is satisfied only when at least VMIN characters have been transferred to the caller's 
	buffer - there is no timing component involved. This read can be satisfied from the driver's input queue (where 
	the call could return immediately), or by waiting for new data to arrive: in this respect the call could block indefinitely. 
	We believe that it's undefined behavior if nbytes is less then VMIN. 
	*/
		
	tp.c_cc[VMIN] = 1;
	tp.c_cc[VTIME] = 0;
	
	tcflush(fd, TCIFLUSH);	
	tcsetattr(fd, TCSANOW, &tp);

	return fd;
}

int ANT_send(int args, ... )
{
	va_list ap;
	int i; 	
	UCHAR buf[MAXMSG];
	
	va_start(ap, args);	
	//fd = va_arg(ap, int); 	// Get file descriptor
	
	buf[0] = MESG_TX_SYNC;	// Everything starts with sync
	buf[1] = args-1; 		// Number of bytes to TX (don't count fd)

	for(i = 2; i <= args+1; i++) 
	{
		buf[i] = va_arg(ap, int);
	}
	
	buf[i] = checkSum(buf, i);  // Count sync byte + checksum
	
	queue_add(&txQueue, buf, i+1);
	
	return RETURN_SUCCESS;
}


int ANT_sendStr(int len, UCHAR *data)
{
	UCHAR buf[MAXMSG];
	int i;
	
	buf[0] = MESG_TX_SYNC;	// Everything starts with sync
	buf[1] = len-1;    		// Number of bytes to TX (don't count fd)
	
	for (i=2; i < len+2; i++)
	{
		buf[i] = data[i-2];
	}
	
	buf[i] = checkSum(buf, i);	
	queue_add(&txQueue, buf, i+1);

	perror("TX");	
	
	return RETURN_SUCCESS;
}


int ANT_tx(int fd, UCHAR *data, int length)
{
	int rc, i;
	
	printf("txda: ");
	for (i = 0; i < length; i++)
	{
		printf("%02x ", data[i]);
	}
	printf("\n");	
	
	if (length != (rc=write(fd, data, length))) {
		perror("ANT_send");
		return RETURN_ERROR;
	}		
		
	perror("TX");
	
	return RETURN_SUCCESS;
}

int ANT_cfgCapabilties(UCHAR *data, cfg_capabilities *cfg, UCHAR size)
{
	bzero(cfg, sizeof(cfg_capabilities));
	
	cfg->maxChannels	= data[0];	
	cfg->maxNetworks	= data[1];
	cfg->stdOptions		= data[2];
	cfg->advOptions[0]	= data[3];
	
	if (size == 6) // Expect size 7 for this msg type
	{
		cfg->advOptions[1] = data[4];
		cfg->maxDataChannels = data[5];
	}
	
	if (DEBUG)
	{
		printf("\n[DEBUG] Device Capabilites\n");
		printf("--------------------------\n");
		printf("Max Ch                %4i\n", cfg->maxChannels);
		printf("Max Networks          %4i\n", cfg->maxNetworks);
		printf("Standard Opts         0x%02x\n", cfg->stdOptions);
		printf("Advanced Opts1        0x%02x ", cfg->advOptions[0]);
		
		if (cfg->advOptions[0] & 0x02) printf("[NETWORK_EN] ");
		if (cfg->advOptions[0] & 0x08) printf("[SERIAL_NUMBER_EN] ");
		if (cfg->advOptions[0] & 0x10) printf("[PER_CHANNEL_TX_POWER_EN] ");
		if (cfg->advOptions[0] & 0x20) printf("[LOW_PRIORITY_SEARCH_EN] ");
		if (cfg->advOptions[0] & 0x40) printf("[SENSRCORE_EN] ");
		if (cfg->advOptions[0] & 0x80) printf("[SEARCH_LIST_EN] ");
				
		printf("\nAdvanced Opts2        0x%02x\n", cfg->advOptions[1]);
		printf("Max Data Ch           %4i\n\n", cfg->stdOptions);
	}
	
	return RETURN_SUCCESS;
	
}

int hstr2hex(UCHAR *hex, char *hexstr, int size)
{
	int i;
	
	if ((size % 2) != 0)
	{
		printf("hstr2hex error: input hex string has to be divisible by 2 [%i]\n", size);
		exit(RETURN_ERROR);
	}
	
	for (i=0; i < (size/2); i++)
	{
		hex[i] = hexval(hexstr[i*2])*16 + hexval(hexstr[i*2 + 1]);
	}
	
	return RETURN_SUCCESS;
}

UCHAR checkSum(UCHAR *data, int length)
{
	int i;
	UCHAR chksum = data[0]; 
		
	for (i = 1; i < length; i++)
		chksum ^= data[i];  // +1 since skip prefix sync code, we already counted it
	
	return chksum;
}
