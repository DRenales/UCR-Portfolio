/*
	Name                 : Dominic Renales (drena001@ucr.edu)
	Partner              : None
	Lab Section          : CS120B 023
	Assignment           : Final Project - INPUTS PROGRAM
	Exercise Description :
	
	Code that handles all the inputs.
	
	I acknowledge all content contained herein, excluding
	template or example code, is my own original work
*/
#include <avr/io.h>
#include "usart_ATmega1284.h"
#include "avr/eeprom.h"
#include "frequency_definitions.h"
#include "pwm.h"

//KEYBOARD KEY DEFINITIONS
#define C  (~PINC & 0x10)
#define CS (~PINC & 0x08)
#define D  (~PINC & 0x04)
#define DS (~PINC & 0x02)
#define E  (~PINC & 0x01)
#define F  (~PINA & 0x80)
#define FS (~PINA & 0x40)
#define G  (~PINA & 0x20)
#define GS (~PINA & 0x10)
#define A  (~PINA & 0x08)
#define AS (~PINA & 0x04)
#define B  (~PINA & 0x02)
#define CO (~PINA & 0x01)

unsigned char transpose;

/*
	Function: Transpose_SM()
	Author: Dominic Renales
	Inputs: None
	Outputs: None
	Description:
	
	Function Transpose written to emulate the state machine for the states of two 
	different button presses. One button increments the transpose position as long
	as the transpose value is no greater than 5. The other button decrements the
	tranpsose position as long as the transpose value is no less than 0.
	
*/
enum Transpose {WAIT, UP, DOWN, RELEASE_WAIT} state;
void Transpose_SM()
{
	switch(state)
	{
		case WAIT:
		state = (~PINC & 0x80) ? UP
		: (~PINC & 0x40) ? DOWN
		:                  WAIT;
		break;
		
		case UP:
		state = RELEASE_WAIT;
		break;
		
		case DOWN:
		state = RELEASE_WAIT;
		break;
		
		case RELEASE_WAIT:
		state = (~PINC & 0x80) || (~PINC & 0x40) ? RELEASE_WAIT : WAIT;
		break;
		
		default:
		state = WAIT;
	}
	
	switch(state)
	{
		case UP:
		transpose = transpose < 5 ? transpose + 1 : transpose;
		eeprom_update_byte(0x00, transpose);
		break;
		
		case DOWN:
		transpose = transpose > 0 ? transpose - 1 : transpose;
		eeprom_update_byte(0x00, transpose);
		break;
		
		default: 
		eeprom_update_byte(0x00, transpose);
		break;
	}
}

/*
	Function: Tutoriale_SM()
	Author: Dominic Renales
	Inputs: None
	Outputs: None
	Description:
	
	Function Tutorial_SM written to emulate the state machine of a single button press.
	When the button is pressed and then released, a signal is sent from the USART0
	transmit pin on PORTD.
*/
enum Tutorial {T_WAIT, PRESS_W, PRESS} b_state;
void Tutorial_SM()
{
	switch(b_state)
	{
		case T_WAIT:
			b_state = (~PINB & 0x01) ? PRESS_W : T_WAIT;
			break;
			
		case PRESS_W:
			b_state = (~PINB & 0x01) ? PRESS_W : PRESS;
			break;
		
		case PRESS:
			b_state = T_WAIT;
		
		default:
			b_state = T_WAIT;		
	}
	
	switch(b_state)
	{
		case PRESS:
			//USART_Flush(0);
			USART_Send(1, 0);
			break;
			
		default:
			break;
	}
}

int main(void)
{
	DDRA = 0x00; PORTA = 0xFF;
	DDRB = 0xFE; PORTB = 0x01;
	DDRC = 0x00; PORTC = 0xFF;
	DDRD = 0x00; PORTD = 0xFF;
	
	initUSART(0);
	PWM_on();
	
	transpose = (eeprom_read_byte(0x00) == 0xFF) ? 2 : eeprom_read_byte(0x00);
	while (1) 
    {
		Transpose_SM();
		Tutorial_SM();
			
		     if(C ){ set_PWM(keys[transpose][0]);  }
		else if(CS){ set_PWM(keys[transpose][1]);  }
		else if(D ){ set_PWM(keys[transpose][2]);  }
		else if(DS){ set_PWM(keys[transpose][3]);  }
		else if(E ){ set_PWM(keys[transpose][4]);  }
		else if(F ){ set_PWM(keys[transpose][5]);  }
		else if(FS){ set_PWM(keys[transpose][6]);  }
		else if(G ){ set_PWM(keys[transpose][7]);  }
		else if(GS){ set_PWM(keys[transpose][8]);  }
		else if(A ){ set_PWM(keys[transpose][9]);  }
		else if(AS){ set_PWM(keys[transpose][10]); }
		else if(B ){ set_PWM(keys[transpose][11]); }
		else if(CO){ set_PWM(keys[transpose][12]); }
		else       { set_PWM(0);                   }
			
		transpose = (transpose >= 0 && transpose <= 4) ? transpose : 2;
    }
	
}

