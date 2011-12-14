/*
Author: JB Kim
Disclaimer: completely free, but use it at your own risk

Originally written for msp430, adopted for arduino/atmega168.
I really don't like Arduino IDE, but it has all the convenient libraries...
*/

#include <SoftwareSerial.h>
#include "Arduino.h"
#include "ant_lib.h"

// we pass by reference, but copies.. sigh.
AntMaster::AntMaster(SoftwareSerial& mySerial, uint8_t ledPin) :
  _swuart(mySerial),
  _ledPin(ledPin)
{
  _swuart.begin(4800);    // ant+ breakout is set at 4800 baud rate by default
  pinMode(_ledPin, OUTPUT);
}
  
void AntMaster::txMessage(uint8_t* message, uint8_t  messageSize)
{
  uint8_t i;

  txBufferPos   = 0;                       // set position to 0
  txBufferSize  = messageSize + 3;         // message plus syc, size and checksum
  txBuffer[0]   = 0xa4;                    // sync byte
  txBuffer[1]   = (uint8_t) messageSize - 1; // message size - command size (1)

  for(i=0; i<messageSize; i++)
    txBuffer[2+i] = message[i];

  // calculate the checksum
  for(i=0; i<txBufferSize - 1; ++i)
    txBuffer[txBufferSize - 1] = txBuffer[txBufferSize - 1] ^ txBuffer[i];

  
  // now send via UART
  for(i=0; i<txBufferSize; i++)
  {
    _swuart.write(txBuffer[i]);
    Serial.print(txBuffer[i], HEX);
    Serial.print(' ');
  }
  Serial.print('\n');
}


// Resets module
void AntMaster::reset()
{
  uint8_t msgbuf[2];
  msgbuf[0] = 0x4a; // ID Byte
  msgbuf[1] = 0x00; // Data Byte N (N=LENGTH)
  txMessage(msgbuf, 2);
}

// Assigns CH=0, CH Type=10(TX), Net#=0
void AntMaster::assignch()
{
  uint8_t msgbuf[4];
  msgbuf[0] = 0x42;
  msgbuf[1] = ANT_CH_ID;    // Channel ID, 0x00 for HRM, 0x01 for custom
  msgbuf[2] = ANT_CH_TYPE;  // CH Type
  msgbuf[3] = ANT_NET_ID;   // Network ID
  txMessage(msgbuf, 4);
}

// Assigns CH#, Device#=0000, Device Type ID=00, Trans Type=00
void AntMaster::setchid()
{
  uint8_t msgbuf[6];
  msgbuf[0] = 0x51;
  msgbuf[1] = ANT_CH_ID;      // Channel Number, 0x00 for HRM
  msgbuf[2] = ANT_DEV_ID1;    // Device Number LSB
  msgbuf[3] = ANT_DEV_ID2;    // Device Number MSB
  msgbuf[4] = ANT_DEV_TYPE;   // Device Type, 0x78 for HRM
  msgbuf[5] = ANT_TX_TYPE;
  txMessage(msgbuf, 6);
}

void AntMaster::setrf()
{
  uint8_t msgbuf[3];
  msgbuf[0] = 0x45;
  msgbuf[1] = (ANT_CH_FREQ & 0xFF00) >> 8;  // upper byte for RF freq
  msgbuf[2] = (ANT_CH_FREQ & 0xFF);         // lower byte for RF freq
  txMessage(msgbuf, 3);
}

void AntMaster::setchperiod()
{
  uint8_t msgbuf[3];
  msgbuf[0] = 0x43;
  msgbuf[1] = (ANT_CH_PER & 0xFF00) >> 8; // Ch period LSB
  msgbuf[2] = (ANT_CH_PER & 0xFF);        // Ch period MSB
  txMessage(msgbuf, 3);
}

// Opens CH 0
void AntMaster::opench()
{
  uint8_t msgbuf[2];
  msgbuf[0] = 0x4b;
  msgbuf[1] = 0x00;
  txMessage(msgbuf, 2);
}


// Sends 10 bytes of data that mimics HRM data trasmission
void AntMaster::sendHRM(uint8_t num)
{
  uint8_t msgbuf[10];
  msgbuf[0] = 0x4e;
  msgbuf[1] = 0x00;
  msgbuf[2] = 0x00;
  msgbuf[3] = 0x00;
  msgbuf[4] = 0x00;
  msgbuf[5] = 0x00;
  msgbuf[6] = 0x00;
  msgbuf[7] = 0x00;
  msgbuf[8] = 0x00;
  msgbuf[9] = num;
  txMessage(msgbuf, 10);
}

void AntMaster::ledon(void)
{
  digitalWrite(_ledPin, HIGH);
}

void AntMaster::ledoff(void)
{
  digitalWrite(_ledPin, LOW);
}

void AntMaster::config(uint8_t msec)
{
  ledon();

  reset();       delay(msec);
  assignch();    delay(msec);
  setrf();       delay(msec);
  setchperiod(); delay(msec);
  setchid();     delay(msec);
  opench();      delay(msec);

  ledoff();
}

void AntMaster::config_loop()
{
  uint8_t i;

  for(i=1; i<=20; i++)
  {
    config(5*i);
  }
}





