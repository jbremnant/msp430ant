/*
Author: JB Kim
Disclaimer: completely free, but use it at your own risk

Acts as ANT Master. Communicates with ANT Slaves.
You need to define the following:
  - Network
  - RF Freq
  - Trasmission Type
  - Device Type
  - Channel Type
  - Channel Period
  - Data Types
  - Data Format

Config format
  Network
    Network Number : 1 byte, default is 0x00
    Network Key    : 8 byte, ant assigns default key for network id 0

  RF Frequency     : 1 byte, supports 125 uniq freq from 2400 - 2534MHz
                     default value is 66 (2466MHz)

  Channel ID
    Device Number  : 2 bytes, uniq string, 0x0000 for wildcard
    Device Type    : 1 byte, used to differentiate multiple nodes 
    Tx Type        : 1 byte, used to define tx characteristics

  Channel Type     : 1 byte, 0x00 bidirectional rx, 0x10 bidirectional tx
  
  Channel Period   : 2 byte, chan_per = 32768 / msg_rate = 32768 / 4 = 8192
                     0x2000 == 8192 is the chan_per provided you want 4Hz.

  Data Type        
*/

/*
Android slave demo app config:
@param networkNumber the network number           0x01   (1byte)  1 (ANT+)
@param channelNumber the channel number           0x01   (1byte) 
@param deviceNumber the device number             0x0000 (2bytes) wildcard
@param deviceType the device type                 0x77   (1byte) 119 distance
@param txType the tx type                         0x00   (1byte)  wildcard
@param channelPeriod the channel period           0x2000 (2bytes) 8192 (4Hz)
@param radioFreq the radio freq                   0x39   (1byte) 57 (ANT+)
@param proxSearch the prox search                 0x07   (1byte)
@return true, if successfully configured and opened channel
*/

#include  <msp430x20x2.h>
#include "uart_rxtx.h"

#define ANT_CH_ID    0x00
#define ANT_CH_TYPE  0x10
#define ANT_NET_ID   0x00
#define ANT_DEV_ID1  0x00
#define ANT_DEV_ID2  0x00
#define ANT_DEV_TYPE 0x00
#define ANT_TX_TYPE  0x00
#define ANT_CH_FREQ  0x0039
#define ANT_CH_PER   0x1f86

#define STOPBIT BIT7
#define LEDBIT BIT4

extern volatile uint8_t HALT;


void _delay_ms(int n)
{
  int i;
  // assumes 2 instructions for loop - could be false
  for(i=0; i<n/2; i++)
    _delay_us(1000); // 1 ms
}


typedef uint8_t uchar;

uchar	txBuffer[32];
uint8_t	txBufferSize;
uint8_t	txBufferPos;

void txMessage(uchar*	message, uint8_t	messageSize)
{
  uint8_t i; 
  _BIC_SR(GIE);  // disable interrupt

	txBufferPos		= 0;							   // set position to 0
	txBufferSize	= messageSize + 3;	 // message plus syc, size and checksum
	txBuffer[0]		= 0xa4;							 // sync byte
	txBuffer[1]		= (uchar) messageSize - 1;		// message size - command size (1)

  for(i=0; i<messageSize; i++)
    txBuffer[2+i] = message[i];

	// calculate the checksum
	for(i=0; i<txBufferSize - 1; ++i)
		txBuffer[txBufferSize - 1] = txBuffer[txBufferSize - 1] ^ txBuffer[i];

  _BIS_SR(GIE);   // enable interrupt

  // now send via UART
  for(i=0; i<txBufferSize; i++)
    uart_putc(txBuffer[i]);
}


// Resets module
void reset() 
{
	uchar setup[2];
	setup[0] = 0x4a; // ID Byte
	setup[1] = 0x00; // Data Byte N (N=LENGTH)
	txMessage(setup, 2);
}

// Assigns CH=0, CH Type=10(TX), Net#=0
void assignch() 
{
	uchar setup[4];
	setup[0] = 0x42;
	setup[1] = ANT_CH_ID;    // Channel ID, 0x00 for HRM, 0x01 for custom
	setup[2] = ANT_CH_TYPE;  // CH Type
	setup[3] = ANT_NET_ID;   // Network ID
	txMessage(setup, 4);
}

// Assigns CH#, Device#=0000, Device Type ID=00, Trans Type=00
void setchid() 
{
	uchar setup[6];
	setup[0] = 0x51;
	setup[1] = ANT_CH_ID;      // Channel Number, 0x00 for HRM
	setup[2] = ANT_DEV_ID1;    // Device Number LSB
	setup[3] = ANT_DEV_ID2;    // Device Number MSB
	setup[4] = ANT_DEV_TYPE;   // Device Type, 0x78 for HRM
	setup[5] = ANT_TX_TYPE;
	txMessage(setup, 6);
}

void setrf()
{
	uchar setup[3];
	setup[0] = 0x45;
  setup[1] = (ANT_CH_FREQ & 0xFF00) >> 8;
	setup[2] = (ANT_CH_FREQ & 0xFF);    // RF Frequency
	// setup[1] = 0x00;    // Channel ID, 0x00 for HRM, 0x01 for custom
	// setup[2] = 0x39;    // RF Frequency
	txMessage(setup, 3);
}

void setchperiod()
{
	uchar setup[3];
	setup[0] = 0x43;
  setup[1] = (ANT_CH_PER & 0xFF00) >> 8;
	setup[2] = (ANT_CH_PER & 0xFF);    // RF Frequency
	// setup[1] = 0x1f;    // Channel Period LSB
	// setup[2] = 0x86;    // Channel Period MSB
	txMessage(setup, 3);
}


// Opens CH 0
void opench() 
{
	uchar setup[2];
	setup[0] = 0x4b;
	setup[1] = 0x00;
	txMessage(setup, 2);
}

// Sends Data=AAAAAAAAAAAAAAAA
void sendHRM(uchar num) 
{
	uchar setup[10];
	setup[0] = 0x4e;
	setup[1] = 0x00;
	setup[2] = 0x00;
	setup[3] = 0x00;
	setup[4] = 0x00;
	setup[5] = 0x00;
	setup[6] = 0x00;
	setup[7] = 0x00;
	setup[8] = 0x00;
	setup[9] = num;
	txMessage(setup, 10);
}

void sendData(uchar rand)
{
  uchar setup[4];
  setup[0] = 0x4e;
  setup[1] = rand * rand;
  setup[2] = rand - setup[1];
  setup[3] = rand;
	txMessage(setup, 4);
}


void config(uint8_t msec)
{
  P1OUT |= LEDBIT;

  reset();       _delay_ms(msec);
  assignch();    _delay_ms(msec);
  setrf();       _delay_ms(msec);
  setchperiod(); _delay_ms(msec);
  setchid();     _delay_ms(msec);
  opench();      _delay_ms(msec);

  P1OUT &= ~LEDBIT;
}

void setupButton_hilo(void)
{
  P1DIR &= ~STOPBIT;      // Set P1.0 to output direction

  P1OUT |= STOPBIT;       // set hi
  P1REN |= STOPBIT;       // pull up enable to hi (determined by P1OUT dir)
  P1IES |= STOPBIT;       // P1.3 Hi/lo edge

  P1IE  |= STOPBIT;       // P1.3 interrupt enabled
  P1IFG &= ~STOPBIT;      // P1.3 IFG cleared
}


void setupButton_lohi(void)
{
  P1DIR &= ~STOPBIT;      // Set P1.0 to output direction

  P1OUT &= ~STOPBIT;      // set lo
  P1REN |= STOPBIT;       // pull down enable to lo (determined by P1OUT dir)
  P1IES &= ~STOPBIT;      // P1.3 lo/hi edge

  P1IE  |= STOPBIT;       // P1.3 interrupt enabled
  P1IFG &= ~STOPBIT;      // P1.3 IFG cleared
}

int main()
{
  int j,i;
  uint8_t needconfig, data;
  char c;

  WDTCTL = WDTPW + WDTHOLD;          // Stop WDT
  BCSCTL1 = CALBC1_12MHZ;   
  DCOCTL  = CALDCO_12MHZ;
  // BCSCTL1 = CALBC1_1MHZ;   
  // DCOCTL  = CALDCO_1MHZ;
  // BCSCTL3 = LFXT1S_0 | XT2S_0 |  XCAP_1;  // ACLK uses external crystal at 32k

  P1SEL = P2SEL = 0x00;
  P1DIR = P2DIR = 0x00;
  P1OUT = P2OUT = 0x00;

  P1DIR |= LEDBIT + BIT6 + BIT5 + BIT0;
  P1DIR |= BIT3;
  P1OUT &= ~BIT0;    // low for ant breakout
  P1OUT &= ~BIT5;    // low for status LED

  setupButton_hilo();

  uart_init();

  _BIS_SR(GIE);   // enable interrupt
  // _BIS_SR(LPM0_bits + GIE);

  P1OUT |= BIT6; _delay_ms(500); P1OUT &= ~BIT6;
  _delay_ms(300);
  P1OUT |= BIT6; _delay_ms(500); P1OUT &= ~BIT6;
  _delay_ms(300);
  P1OUT |= LEDBIT; _delay_ms(500); P1OUT &= ~LEDBIT;
  _delay_ms(300);
  P1OUT |= LEDBIT; _delay_ms(500); P1OUT &= ~LEDBIT;

  // P1SEL  = BIT4; //0x10;                     // P1.4 SMCLK output
  // P1OUT |= BIT6;  // if started ok, green light on
  // config();
  needconfig = 1;

  while(1)
  {
    if(HALT==1 && needconfig==1) continue;

    if(HALT==1)
    {
      if(needconfig==0)
      { 
        // reboot the breakout, since it keeps sending the last good data
        P1OUT |= BIT3;
        _delay_ms(50);
        P1OUT &= ~BIT3; 
      }
      needconfig = 1;
      _delay_ms(50);

      continue;
    }

    // configure again multiple times
    if(needconfig)
    {
      _delay_ms(50);

      for(i=1; i<=20; i++)
      {
        if(HALT) break;

        config(5*i);
        if(i!=3) _delay_ms(5*i);
      }
      needconfig = 0;
      continue;
    }

    // send data 20 times
    for(j=0; j<10; j++)
    {
      if(uart_in())
      {
        c = uart_getc(); 
      }
      else
      {
        sendHRM(data++);
        P1OUT ^= BIT6;  // toggle on and off to indicate we are in the main loop
        _delay_ms(100); // 100 msec is crucial here!
      }

      if(HALT) break;
    }

  }

  return 0;
}
