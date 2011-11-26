TARGET=ant_master
CC=msp430-gcc
# CFLAGS=-Os -Wall -g -mmcu=msp430x2012
CFLAGS=-Os -Wall -mmcu=msp430x2012

OBJSTX=$(TARGET).o uart_tx.o
OBJSRXTX=$(TARGET).o uart_rxtx.o

all: rxtx

tx: $(OBJSTX)
	$(CC) $(CFLAGS) -o $(TARGET).elf $(OBJSTX)
	msp430-objcopy -O ihex $(TARGET).elf $(TARGET).hex
	msp430-size -d --format=berkeley $(TARGET).elf

rxtx: $(OBJSRXTX)
	$(CC) $(CFLAGS) -o $(TARGET).elf $(OBJSRXTX)
	msp430-objcopy -O ihex $(TARGET).elf $(TARGET).hex
	msp430-size -d --format=berkeley $(TARGET).elf

$(TARGET).o: $(TARGET).c
	$(CC) $(CFLAGS) -c $<

uart_tx.o: uart_tx.c
	$(CC) $(CFLAGS) -c $<

uart_rxtx.o: uart_rxtx.c
	$(CC) $(CFLAGS) -c $<

clean:
	rm -fr $(TARGET).elf $(OBJSTX) $(OBJSRXTX)

upload: all
	mspdebug rf2500 "prog $(TARGET).elf"

uploadez: all
	mspdebug -d /dev/ttyUSB0 uif "prog $(TARGET).elf"
