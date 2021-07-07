/*
	Name                 : Dominic Renales (drena001@ucr.edu)
	Partner              : None
	Lab Section          : CS120B 023
	Assignment           : Final Project
	Exercise Description : 

	Code for the Atmega1284 that handles outputs.
	
	I acknowledge all content contained herein, excluding
	template or example code, is my own original work
*/

#include <avr/io.h>
#include "usart_ATmega1284.h"
#include "pwm.c"
#include "timer.h"
#include "frequency_definitions.h"
#include "Thunderstruck.h"
//#include "songs.h"

unsigned char PLAY_BUTTON;

enum States {WAIT, PLAY, END_W} state;

unsigned char count = 0x00;
long double MAX = (sizeof(Thunderstruck_M) / sizeof(Thunderstruck_M[0]));

void transmit_data(unsigned char data)
{
	for(unsigned char i = 0; i < 8; i++)
	{
		PORTB = 0x08;
		PORTB |= ((data>>i) & 0x01);
		PORTB |= 0x02;
	}
	PORTB |= 0x04;
	PORTB = 0x00;
}

unsigned char countdown[] = 
{ 
	0b00001101, 0b00100101, 0b10011111, 0b00000011,
	
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,

	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,

	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
	0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011, 0b00000011,
};

void state_transitions()
{
	switch(state)
	{
		case WAIT:
		state = PLAY_BUTTON ? PLAY : WAIT;
		break;
		
		case PLAY:
		state = count < MAX ? PLAY : END_W;
		break;
		
		case END_W:
		state = PLAY_BUTTON ? END_W	: WAIT;
		PLAY_BUTTON = 0x00;
		break;
		
		default:
		state = WAIT; break;
	}
}

void state_actions()
{
	switch(state)
	{
		case WAIT:
			count = 0x00;
			transmit_data(0b10011001);
			PORTC = 0xFF;
			TimerFlag = 1;
			set_PWM(0);
			break;
		
		case PLAY:
			transmit_data(countdown[count]);	
			PORTC = ~Thunderstruck_B[count];
			TimerSet(Thunderstruck_D[count]);
			TimerFlag = 0;
			set_PWM(Thunderstruck_M[count]);
			count += 1;
			break;
		
		case END_W:
			PLAY_BUTTON = 0x00;
			break;
		
		default:
			set_PWM(0);
			break;
	}
}
int main(void)
{
	DDRA = 0xFF; PORTA = 0x00;
	DDRB = 0xFF; PORTB = 0x00;
	DDRC = 0xFF; PORTC = 0x00;
	DDRD = 0xFF; PORTD = 0x00;
	
	PORTA = 0xFF;
	
	initUSART(0);
	PWM_on();
	TimerOn();
	TimerSet(0);
	
    while (1) 
    {
		PLAY_BUTTON = USART_HasReceived(0) ? USART_Receive(0) : 0;
		
		state_transitions();
		state_actions();
		while(!TimerFlag);
		
		USART_Flush(0);
    }
}

