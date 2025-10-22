#ifndef bt_connection_h
#define bt_connection_h

#include "Arduino.h"
//this is class for sending and receiving comands via bluetooth module

class Communication
{
  public:
    Communication();
    
    void SETUP();
    void validate_connection(bool isConnected);
    void USART_send_char(char c);
    unsigned char USART_receive();
};

extern Communication communication;

#endif