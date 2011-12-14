#ifndef _AntMaster_h
#define _AntMaster_h

#include <inttypes.h>
#include <SoftwareSerial.h>

#define ANT_CH_ID    0x00
#define ANT_CH_TYPE  0x10
#define ANT_NET_ID   0x00
#define ANT_DEV_ID1  0x00
#define ANT_DEV_ID2  0x00
#define ANT_DEV_TYPE 0x00
#define ANT_TX_TYPE  0x00
#define ANT_CH_FREQ  0x0039
#define ANT_CH_PER   0x1f86

// this PD4 pin on atmega168 maps to arduino's digital pin 4
// #define LEDBIT PD4

class AntMaster 
{
public:
  AntMaster(SoftwareSerial& mySerial, uint8_t ledPin = 4);
  void reset(void);
  void assignch(void); 
  void setrf(void); 
  void setchperiod(void);
  void setchid(void);  
  void opench(void);   
  void sendHRM(uint8_t num);
  void config(uint8_t msec);
  void config_loop();
  void ledon();
  void ledoff();
  
private:
  SoftwareSerial _swuart;
  uint8_t _ledPin;
  uint8_t txBuffer[32];
  uint8_t txBufferSize;
  uint8_t txBufferPos;
  void txMessage(uint8_t* message, uint8_t  messageSize);
};

#endif
