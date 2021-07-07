/*
 * CS120_USART_Lead.c
 *
 * Created: 5/23/2019 12:09:43 PM
 * Author : Renal
 */ 

#include <avr/io.h>
#include "usart_ATmega1284.h"


int main(void)
{
	DDRA = 0x00; PORTA = 0xFF;
	DDRB = 0x00; PORTB = 0xFF;
	DDRC = 0x00; PORTC = 0xFF;
	DDRD = 0x00; PORTD = 0xFF;
	
	initUSART(0);
	
    while (1) 
    {
		USART_Send((~PINA & 0x01) + (~PINA & 0x02), 0);
    }
}

