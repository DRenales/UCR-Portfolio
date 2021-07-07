/*
 * CS120B_USART_Follow.c
 *
 * Created: 5/23/2019 12:24:38 PM
 * Author : Renal
 */ 

#include <avr/io.h>
#include "usart_ATmega1284.h"


int main(void)
{
	DDRA = 0xFF; PORTA = 0x00;
	DDRB = 0xFF; PORTB = 0x00;
	DDRC = 0xFF; PORTC = 0x00;
	DDRD = 0xFF; PORTD = 0x00;
	
	initUSART(0);
	
    while (1) 
    {
		unsigned char tmp = USART_Receive(0); 
		PORTB = tmp;
    }
}

