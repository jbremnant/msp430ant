#include <msp430x20x2.h>
#include <signal.h>
#include <stdint.h>
#include <stdlib.h>
#include "uart_rxtx.h"

// #define DEBUG 0
// #define TIMERCLK TASSEL_1
#define TIMERCLK TASSEL_2
#define STOPBIT BIT7

static volatile uint8_t  bitCount;			// Bit count, used when transmitting byte
static volatile uint16_t tx_byte;			// Value sent over UART when uart_putc() is called
static volatile uint16_t rx_byte;			// Value recieved once hasRecieved is set
static volatile uint8_t isReceiving = 0;	// Status for when the device is receiving
static volatile uint8_t hasReceived = 0;	// Lets the program know when a byte is received
volatile uint8_t HALT = 1;


void uart_init(void)
{
  P1SEL |= TXD;    // selects primary peripheral module 
  P1DIR |= TXD;    // output direction
  P1IES |= RXD;	   // RXD hi -> lo edge interrupt
  P1IFG &= ~RXD;	 // Clear RXD (flag) before enabling interrupt
  P1IE  |= RXD;	   // Enable RXD interrupt
}


uint8_t uart_in(void)
{
  return hasReceived;
}

uint8_t uart_getc(void) 
{
	hasReceived = 0;
	return rx_byte;
}

void uart_putc(uint8_t c)
{
  tx_byte = c;

  while (isReceiving);			        // Wait for RX completion

  CCTL0 = OUT;					            // TXD Idle as Mark
  TACTL = TIMERCLK + MC_2;		      // SMCLK, continuous mode

  bitCount = 10;					          // Load Bit counter, 8 bits + ST/SP

  CCR0 = TAR;						            // Initialize compare register
  CCR0 += BIT_TIME;				          // Set time till first bit

  tx_byte |= 0x100;				          // Add stop bit to tx_byte (which is logical 1)
  tx_byte = tx_byte << 1;			      // Add start bit (which is logical 0)
  CCTL0 = CCIS0 + OUTMOD0 + CCIE;  	// set signal, intial value, enable interrupts

  while (CCTL0 & CCIE);			        // Wait for completion
}


#ifdef MSP430
interrupt(PORT1_VECTOR) PORT1_ISR(void)
#else
#pragma vector=PORT1_VECTOR
__interrupt void Port1_Vector(void)
#endif
{
  // receive section
  if((P1IFG & RXD))
  {
    isReceiving = 1;

    P1IE &= ~RXD;	             // Disable RXD interrupt so we don't get into race condition
    P1IFG &= ~RXD;	           // Clear RXD IFG (interrupt flag)

    TACTL = TIMERCLK + MC_2;	 // SMCLK, continuous mode
    CCR0 = TAR;					       // Initialize compare register
    //CCR0 += HALF_BIT_TIME;	 // Set time till first bit, not working for me
    CCR0 += BIT_TIME;			     // this works better for me
    CCTL0 = OUTMOD1 + CCIE;		 // Disable TX and enable interrupts
  
    rx_byte = 0;				       // Initialize rx_byte
    bitCount = 9;				       // Load Bit counter, 8 bits + start bit
  }
  // button was pushed
  else if((P1IFG & STOPBIT))
  {
    // _delay_ms(5); // wait for debounce

    // P1IE &= ~STOPBIT;	             // Disable RXD interrupt so we don't get into race condition
    P1IFG &= ~STOPBIT;	       // Clear RXD IFG (interrupt flag)

    _delay_ms(5); // wait for debounce

    HALT = (HALT==0) ? 1 : 0;

    if(HALT)
    {
      // TACTL = TIMERCLK + TACLR;  // stop the timer
      _delay_ms(20);
      P1OUT &= ~LEDBIT;
      P1OUT &= ~BIT6;
      // _BIS_SR(LPM0_bits);
    } 
    else
    {
      uart_init();
      // TACTL = TIMERCLK + MC_2; // restart the timer
      P1OUT |= LEDBIT;
      P1OUT |= BIT6;
      // _BIC_SR(LPM0_bits);
    }

    // P1IE |= STOPBIT;	             // Disable RXD interrupt so we don't get into race condition
  }
}


#ifdef MSP430
interrupt(TIMERA0_VECTOR) TIMERA0_ISR(void)
#else
#pragma vector=TIMERA0_VECTOR
__interrupt void Timer_A(void)
#endif
{
  CCR0 += BIT_TIME;
  // doing TX
  if(!isReceiving)
  {
    if(bitCount == 0)
    {
      CCTL0 &= ~CCIE ;   	// Disable interrupt
    }
    else
    {
      if(bitCount < 6)
        CCR0 -= 12;

      CCTL0 |= OUTMOD2;	  // Set TX bit to 0

      if(tx_byte & 0x01)
        CCTL0 &= ~OUTMOD2;

      tx_byte = tx_byte >> 1;
      bitCount--;
    }
  }
  // doing RX
  else
  {
    if(bitCount == 0)
    {
      TACTL = TIMERCLK;	   // SMCLK, timer off (for power consumption)
      CCTL0 &= ~CCIE ;	   // Disable interrupt

      isReceiving = 0;

      P1IFG &= ~RXD;		   // clear RXD IFG (interrupt flag)
      P1IE |= RXD;		     // enabled RXD interrupt

      if ((rx_byte & 0x201) == 0x200)
      {
        // the start and stop bits are correct
        rx_byte >>= 1; 		// Remove start bit
        rx_byte &= 0xff;	// Remove stop bit
        hasReceived = 1; 
      }
    }
    else
    {
      if ((P1IN & RXD) == RXD)	// If bit is set?
        rx_byte |= 0x400;		    // Set the value in the rx_byte

      rx_byte = rx_byte >> 1;		// Shift the bits down
      bitCount--;
    }
  }
}

