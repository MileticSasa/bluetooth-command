#include <string.h>
#include "bt_connection.h"
#include "led_functions.h"


bool is_connected = false;
unsigned int received_data = 0;
int MAX = 125;
bool green_turned_on = false;
bool red_turned_on = false;
bool command_reading = false;


void setup() 
{
  communication.SETUP();
  leds.SETUP();
}

void loop() 
{
  
}

//INTERRUPT FROM STATE PIN ON HC-05 MODULE
ISR(INT1_vect)
{
  if(PIND & (1 << PIN3))
  {
    is_connected = true;
    communication.USART_send_char(MAX);
  }
  else
  {
    is_connected = false;
    leds.turn_off_green();
    leds.turn_off_red();
  }

  communication.validate_connection(is_connected);
}

String command = "";
ISR(USART_RX_vect)
{
  received_data = communication.USART_receive();
  if(command_reading)
  {
    if(received_data != '\n')
    {
      command += (char)received_data;
    }
    else
    {
      command_reading = false;
      if(command == "green on")
      {
        leds.turn_on_green(MAX);
        green_turned_on = true;
      }
      if(command == "green off")
      {
        leds.turn_off_green();
        green_turned_on = false;
      }
      if(command == "red on")
      {
        leds.turn_on_red(MAX);
        red_turned_on = true;
      }
      if(command == "red off")
      {
        leds.turn_off_red();
        red_turned_on = false;
      }

      command = "";
    }
  }
  else
  {
    switch(received_data)
    {
      case 0:
        command_reading = true;
        break;
      default:
        MAX = received_data;
        if(green_turned_on)
        {
          leds.turn_on_green(MAX);
        }   
        if(red_turned_on)
        {
          leds.turn_on_red(MAX);
        }
        break;
    }
  }
}

