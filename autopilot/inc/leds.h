/*
 * leds.h
 *
 *  Created on: Apr 21, 2013
 *      Author: kvv
 */

#ifndef LEDS_H_
#define LEDS_H_

#include "stm32f4xx.h"

#define LEDS_Off 0
#define LEDS_On 1

void LEDS_init();
void LEDS_live(uint8_t state);
void LEDS_trigger(uint8_t state);
uint8_t LEDS_live_state();
uint8_t LEDS_trigger_state();


#endif /* LEDS_H_ */
