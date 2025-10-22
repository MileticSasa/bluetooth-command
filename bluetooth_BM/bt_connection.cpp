#include "bt_connection.h"


Communication::Communication()
{

}


void Communication::SETUP()
{
  //ENABLE THE TRANSMISSION AND RECEPTION FOR USART B
  UCSR0B |= (1 << RXEN0) | (1 << TXEN0);

  //SET DATA SIZE FOR COMMUNICATION
  UCSR0C &= (~(1 << UMSEL00)) & (~(1 << UMSEL01)) & (~(1 << UPM00)) & (~(1 << UPM01)) & (~(1 << USBS0));

  //SET DATA LENGHT TO BE 8 BITS
  UCSR0B &= ~(1 << UCSZ02);
  UCSR0C |= (1 << UCSZ00) | (1 << UCSZ01);

  //SET THE SPEED OF TRANSMISSION
  UCSR0A &= ~(1 << U2X0); //HIGH SPEED

  //SET BAUD RATE
  UBRR0 = 103; //9600 HZ

   //ENABLE INTERRUPTS
  SREG |= (1 << 7);  //enable global interrupt
  UCSR0B |= (1 << RXCIE0);    //UART INTERRUPT
  EIMSK |= (1 << INT1);       //PIN 3 INTERRUPT (STATE pin on bluetooth module for connection validation is connected to pin 3)
  //SETTING EDGE SELECT FOR INTERRUPT
  EICRA |= (1 << ISC10);
  EICRA &= ~(1 << ISC11);

  DDRD &= ~(1 << DDD3); //SET PIN 3 AS INPUT
  DDRB |= 1 << DDB0;  //SET PIN 8 AS OUTPUT

  PORTB &= ~(1 << PB0);  //INITIALY THERE IS NO CONNECTION, SO PIN 8 IS SET TO LOW
}


//LED IS TURNED ON IF THERE IS CONNECTION
void Communication::validate_connection(bool isConnected)
{
  if(isConnected)
  {
    PORTB |= 1 << PB0;
  }
  else
  {
    PORTB &= ~(1 << PB0);
  }
}


void Communication::USART_send_char(char c)
{
  while(!UCSR0A & (1 << UDRE0)); //WAIT FOR EMPTY TRANSMIT BUFFER
  UDR0 = c;
}


unsigned char Communication::USART_receive()
{
  while(!(UCSR0A & (1 << RXC0)));
  return UDR0;
}


