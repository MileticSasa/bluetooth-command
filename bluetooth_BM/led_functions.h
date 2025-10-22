#ifndef led_functions_h
#define led_functions_h

#include "Arduino.h"
#include "stdint.h"

class LED_functions
{
  private:
    bool first_led_turned_on = false;

  public:
    LED_functions();

    void SETUP();
    void turn_on_green(int x);
    void turn_on_red(int x);
    void turn_off_green();
    void turn_off_red();
};

extern LED_functions leds;

#endif