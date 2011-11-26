#ifndef _ANTLIB_H_
#define _ANTLIB_H_

// MESG_CAPABILITIES_ID (0x54) (Ant Message Protocol and Usage - Page 52)

typedef struct {
	UCHAR maxChannels;	
	UCHAR maxNetworks;
	UCHAR stdOptions;
	UCHAR advOptions[2];		
	UCHAR maxDataChannels;
} cfg_capabilities;

int ANT_init(char *devname);
int ANT_send(int args, ... );
int ANT_sendStr(int len, UCHAR *data);
int ANT_tx(int fd, UCHAR *data, int length);

int ANT_cfgCapabilties(UCHAR *data, cfg_capabilities *cfg, UCHAR size);

int hstr2hex(UCHAR *hex, char *hexstr, int size);
UCHAR checkSum(UCHAR *data, int length);

#endif

