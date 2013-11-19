/*
 * motors.c
 *
 *  Created on: May 8, 2013
 *      Author: kvv
 */

#include "motors.h"
#include "stm32f4xx_rcc.h"
#include "stm32f4xx_tim.h"
#include "stm32f4xx_gpio.h"


static void initTimer();
static void initPWM();
static void initPins();

void MOTORS_init() {
	initTimer();
	initPWM();
	initPins();
}

void MOTORS_forward(uint32_t speed) {
    TIM_SetCompare1(TIM3, speed);
    TIM_SetCompare2(TIM3, speed);
    GPIO_SetBits(GPIOB, GPIO_Pin_6);
    GPIO_ResetBits(GPIOB, GPIO_Pin_7);
    GPIO_SetBits(GPIOB, GPIO_Pin_8);
    GPIO_ResetBits(GPIOB, GPIO_Pin_9);
}

void MOTORS_back() {

}

void MOTORS_left() {

}

void MOTORS_right(uint32_t speed) {
    TIM_SetCompare1(TIM3, speed);
    TIM_SetCompare2(TIM3, speed);
    GPIO_ResetBits(GPIOB, GPIO_Pin_6);
    GPIO_SetBits(GPIOB, GPIO_Pin_7);
    GPIO_SetBits(GPIOB, GPIO_Pin_8);
    GPIO_ResetBits(GPIOB, GPIO_Pin_9);
}


static void initTimer() {
	RCC_ClocksTypeDef RCC_Clocks;
	RCC_GetClocksFreq(&RCC_Clocks);

    /* TIM4 clock enable */
    RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM3, ENABLE);

    /* Compute the prescaler value */
    //uint32_t PrescalerValue = RCC_Clocks.PCLK1_Frequency/1000 - 1;
    uint32_t PrescalerValue = (uint16_t) ((SystemCoreClock / 2) / 21000000) - 1;

    TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
    /* Time base configuration */
    TIM_TimeBaseStructure.TIM_Period = 1000;
    TIM_TimeBaseStructure.TIM_Prescaler = PrescalerValue;
    TIM_TimeBaseStructure.TIM_ClockDivision = 0;
    TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;

    TIM_TimeBaseInit(TIM3, &TIM_TimeBaseStructure);
}

static void initPWM() {
    TIM_OCInitTypeDef TIM_OCInitStructure;

    TIM_OCStructInit(&TIM_OCInitStructure);
    TIM_OCInitStructure.TIM_OCMode = TIM_OCMode_PWM1;
    TIM_OCInitStructure.TIM_OutputState = TIM_OutputState_Enable;
    TIM_OCInitStructure.TIM_Pulse = 50;
    TIM_OCInitStructure.TIM_OCPolarity = TIM_OCPolarity_High;

    /* PWM1 Mode configuration: Channel1 (GPIOB Pin 4)*/
    TIM_OC1Init(TIM3, &TIM_OCInitStructure);
    TIM_OC1PreloadConfig(TIM3, TIM_OCPreload_Enable);

    /* PWM1 Mode configuration: Channel1 (GPIOB Pin 5)*/
    TIM_OC2Init(TIM3, &TIM_OCInitStructure);
    TIM_OC2PreloadConfig(TIM3, TIM_OCPreload_Enable);

    TIM_Cmd(TIM3, ENABLE);
}

static void initPins() {
    GPIO_InitTypeDef pwm_gpio;

    RCC_AHB1PeriphClockCmd(RCC_AHB1Periph_GPIOB, ENABLE);

    pwm_gpio.GPIO_Pin = GPIO_Pin_4 | GPIO_Pin_5;
    pwm_gpio.GPIO_Mode = GPIO_Mode_AF;
    pwm_gpio.GPIO_Speed = GPIO_Speed_100MHz;
    pwm_gpio.GPIO_OType = GPIO_OType_PP;
    pwm_gpio.GPIO_PuPd = GPIO_PuPd_DOWN;
    GPIO_Init(GPIOB, &pwm_gpio);

    GPIO_PinAFConfig(GPIOB, GPIO_PinSource4, GPIO_AF_TIM3);
    GPIO_PinAFConfig(GPIOB, GPIO_PinSource5, GPIO_AF_TIM3);

    TIM_SetCompare1(TIM3, 0);
    TIM_SetCompare1(TIM3, 0);

    GPIO_InitTypeDef gpio;
    GPIO_StructInit(&gpio);
    gpio.GPIO_Mode = GPIO_Mode_OUT;
    gpio.GPIO_Pin = GPIO_Pin_6|GPIO_Pin_7|GPIO_Pin_8|GPIO_Pin_9;
    gpio.GPIO_PuPd = GPIO_PuPd_DOWN;
    GPIO_Init(GPIOB, &gpio);

    GPIO_ResetBits(GPIOB, GPIO_Pin_6|GPIO_Pin_7|GPIO_Pin_8|GPIO_Pin_9);
}

void MOTORS_LMS(uint8_t speed) {
	if (speed >= 50) {
		TIM_SetCompare1(TIM3, speed);
		GPIO_ResetBits(GPIOB, GPIO_Pin_6);
		GPIO_SetBits(GPIOB, GPIO_Pin_7);
	} else {
		TIM_SetCompare1(TIM3, speed);
		GPIO_ResetBits(GPIOB, GPIO_Pin_6);
		GPIO_SetBits(GPIOB, GPIO_Pin_7);
	}
}

void MOTORS_RMS(uint8_t speed) {
	if (speed >= 50) {
		TIM_SetCompare2(TIM3, speed);
		GPIO_ResetBits(GPIOB, GPIO_Pin_6);
		GPIO_SetBits(GPIOB, GPIO_Pin_7);
	} else {
		TIM_SetCompare1(TIM3, speed);
		GPIO_ResetBits(GPIOB, GPIO_Pin_6);
		GPIO_SetBits(GPIOB, GPIO_Pin_7);
	}
}
