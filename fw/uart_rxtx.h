#ifndef _UART_HEADER_
#define _UART_HEADER_

#include <msp430x20x2.h>

#ifndef F_CPU
#define F_CPU 12000000UL
#endif

#define T_CLK 12000000UL

#define TXD BIT1        // TXD on P1.1
#define RXD BIT2        // RXD on P1.2

// #define BAUDRATE 9600
#define BAUDRATE 4800
// #define BIT_TIME        (T_CLK / BAUDRATE)
#define BIT_TIME        2500
#define HALF_BIT_TIME   (BIT_TIME / 2)
#define USEC (F_CPU/1000000)

// Conditions for 4800 Baud SW UART, ACK = 32768,  32768 / 4800 = 6.82 ~ 7
// #define BIT_TIME      0x06
// #define HALF_BIT_TIME 0x03

// #define DEBUG 1 

#define LEDBIT BIT4

#ifdef MSP430
//______________________________________________________________________
static void __inline__ brief_pause(register uint16_t n) {
    __asm__ __volatile__ (
                "1: \n"
                " dec      %[n] \n"
                " jne      1b \n"
        : [n] "+r"(n));

}
#define _delay_us(n)	brief_pause(n*USEC/2)

#else

#define _delay_us(n)	__delay_cycles(USEC)
//typedef unsigned char	uint8_t;
//typedef unsigned int	uint16_t;

#endif


uint8_t uart_in(void);
void uart_init(void);
uint8_t uart_getc();
void uart_putc(uint8_t c);

#endif
