#include <avr/io.h>
#include "pwm.c"
#include "frequency_definitions.h"
#include "usart_ATmega1284.h"
#include "avr/eeprom.h"

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

enum Transpose {WAIT, UP, DOWN, RELEASE_WAIT} transpose_state;

void Transpose_SM()
{
	switch(transpose_state)
	{
		case WAIT:
			transpose_state = (~PINC & 0x80) ? UP 
							: (~PINC & 0x40) ? DOWN
							:                  WAIT;
			break;
			
		case UP:
			transpose_state = RELEASE_WAIT;
			break;
		
		case DOWN:
			transpose_state = RELEASE_WAIT;
			break;
			
		case RELEASE_WAIT:
			transpose_state = (~PINC & 0x80) || (~PINC & 0x40) ? RELEASE_WAIT : WAIT;
			break;
			
		default:
			transpose_state = WAIT;
	}
	
	switch(transpose_state)
	{
		case UP:
			transpose = transpose < 5 ? transpose + 1 : transpose;
			eeprom_write_byte(0x00, transpose);
			break;
			
		case DOWN:
			transpose = transpose > 0 ? transpose - 1 : transpose;
			eeprom_write_byte(0x00, transpose);
			break;
			
		default: 
			eeprom_write_byte(0x00, transpose);
			break;
	}
}

int main(void)
{
	DDRA = 0x00; PORTA = 0xFF;
	DDRB = 0xFF; PORTB = 0x00;
	DDRC = 0x00; PORTC = 0xFF;
	DDRD = 0x00; PORTD = 0xFF;
	
	//initUSART(0);
	
	PWM_on();
	
    while (1) 
    {
		//if(eeprom_read_byte(0x00) == 0xFF) eeprom_write_byte(0x00, 2); 
		//else eeprom_write_byte(0x00, transpose);
		transpose = (eeprom_read_byte(0x00) == 0xFF) ? 2 : eeprom_read_byte(0x00);
		
		Transpose_SM();		
	
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
			
		//USART_Send(transpose, 0);
    }
}

