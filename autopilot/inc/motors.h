/*
 * motors.h
 *
 *  Created on: May 8, 2013
 *      Author: kvv
 */

#ifndef MOTORS_H_
#define MOTORS_H_

#include "stm32f4xx.h"

void MOTORS_init();
void MOTORS_forward(uint32_t speed);
void MOTORS_back();
void MOTORS_left();
void MOTORS_right(uint32_t speed);
void MOTORS_LMS(uint8_t speed);
void MOTORS_RMS(uint8_t speed);

#endif /* MOTORS_H_ */
