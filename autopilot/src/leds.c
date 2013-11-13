/*
 * leds.c
 *
 *  Created on: Apr 21, 2013
 *      Author: kvv
 */

#include "leds.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_rcc.h"

void LEDS_init() {

    RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOD, ENABLE);
    GPIO_InitTypeDef gpio;
    GPIO_StructInit(&gpio);
    gpio.GPIO_Mode = GPIO_Mode_OUT;
    gpio.GPIO_Pin = GPIO_Pin_12|GPIO_Pin_13|GPIO_Pin_14|GPIO_Pin_15;
    GPIO_Init(GPIOD, &gpio);

    GPIO_ResetBits(GPIOD, GPIO_Pin_12|GPIO_Pin_13|GPIO_Pin_14|GPIO_Pin_15);
}

void LEDS_live(uint8_t state) {
	if (state == LEDS_Off) {
		GPIO_ResetBits(GPIOD, GPIO_Pin_12);
	}
	else {
		GPIO_SetBits(GPIOD, GPIO_Pin_12);
	}
}

void LEDS_trigger(uint8_t state) {
	if (state == LEDS_Off) {
		GPIO_ResetBits(GPIOD, GPIO_Pin_13);
	}
	else {
		GPIO_SetBits(GPIOD, GPIO_Pin_13);
	}
}
