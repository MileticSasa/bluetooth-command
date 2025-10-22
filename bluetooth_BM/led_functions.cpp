#include "led_functions.h"


LED_functions::LED_functions()
{
  
}


void LED_functions::SETUP()
{
  //SET MODE OF THE TIMER; I USE 0101 FOR FAST PWM 8 BIT 
  TCCR1B |= (1 << WGM12);
  TCCR1B &= ~(1 << WGM13);
  TCCR1A |= (1 << WGM10);
  TCCR1A &= ~(1 << WGM11);

  //SET PWM MODE
  //TCCR1A |= (1 << COM1A1);
  //TCCR1A &= ~(1 << COM1A0);
  //TCCR1A |= (1 << COM1B1);
  //TCCR1A &= ~(1 << COM1B0);

  //SET THE PRESCALER FOR THE TIMER FOR SETTING PWM FREQUENCY
  //MY CLOCK IS 16MHz. I WANT 250KHz, SO I HAVE TO DEVIDE BY 64. FOR 64 BITS IN TCCR1B ARE 011
  TCCR1B |= (1 << CS10) | (1 << CS11);
  TCCR1B &= ~(1 << CS12);

  //CONFIGURE THE OUTPUT COMPARE PINS AS O/P
  DDRB |= (1 << DDB1) | (1 << DDB2);  //PINS 9 AND 10
  
}


void enable_pwm_pin9()
{
  TCCR1A |= (1 << COM1A1);
  TCCR1A &= ~(1 << COM1A0);
}

void enable_pwm_pin10()
{
  TCCR1A |= (1 << COM1B1);
  TCCR1A &= ~(1 << COM1B0);
}

void disable_pwm_pin9()
{
  TCCR1A &= ~(1 << COM1A1);
  TCCR1A &= ~(1 << COM1A0);
}

void disable_pwm_pin10()
{
  TCCR1A &= ~(1 << COM1B1);
  TCCR1A &= ~(1 << COM1B0);
}


void LED_functions::turn_on_green(int x)
{
  enable_pwm_pin9();

  OCR1A = x;
}

void LED_functions::turn_on_red(int x)
{
  enable_pwm_pin10();

  OCR1B = x;
}

void LED_functions::turn_off_green()
{
  disable_pwm_pin9();
}

void LED_functions::turn_off_red()
{
  disable_pwm_pin10();
}



