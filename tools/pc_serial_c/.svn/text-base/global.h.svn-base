#ifndef _GLOBAL_H_
#define _GLOBAL_H_

#include <semaphore.h>

#define RETURN_ERROR	-1
#define RETURN_SUCCESS	 0 

#define DEBUG 		1 

// Ant Stuff
#define MAXMSG 		14 // SYNC,LEN,MSG,data[8],CHKSUM

#define CHAN0		0
#define CHAN1		1

#define NET0		0
#define NET1		1

#define TIMEOUT     30

#define BAUD	B4800   // Sparkfun Default Baud
//#define BAUD	B115200  // Garmin FOB

#define FREQ        0x39	// Garmin radio frequency
//#define FREQ		0x41;	// Suunto radio frequency

#define PERIOD      0x1F86	// Garmin search period
//#define PERIOD		0x199a; // Suunto search period

// Might be right...
//#define PERIOD      0x1FF6      // Footpod
//#define PERIOD      0x1F96      // Footpod

#define NETWORK_KEY			"B9A521FBBD72C345" // Garmin HRM
//#define NETWORK_KEY		"B9AD3228757EC74D" // Suunto HRM

// Macros
#define hexval(c) ((c >= '0' && c <= '9') ? (c-'0') : ((c&0xdf)-'A'+10))

#endif
