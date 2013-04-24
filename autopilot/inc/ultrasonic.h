/*
 * ultrasonic.h
 *
 *  Created on: Apr 6, 2013
 *      Author: kvv
 */

#ifndef ULTRASONIC_H_
#define ULTRASONIC_H_

#include "stm32f4xx.h"

__IO uint32_t US_DISTANCE;

void ultrasonic_init();
void trigger();

#endif /* ULTRASONIC_H_ */
