/*
 * ultrasonic.c
 *
 *  Created on: Apr 6, 2013
 *      Author: kvv
 */

#include "ultrasonic.h"
#include "time.h"
#include "leds.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_rcc.h"
#include "stm32f4xx_exti.h"
#include "stm32f4xx_syscfg.h"
#include "stm32f4xx_tim.h"

#define US_GPIO 		GPIOD
#define US_RCC 			RCC_AHB1Periph_GPIOD
#define US_EXTI_Port 	EXTI_PortSourceGPIOD
#define US_TRIGGER_PIN 	GPIO_Pin_2
#define US_ECHO_PIN 	GPIO_Pin_0
#define US_EXTI_PIN		EXTI_PinSource0
#define US_EXTI_Line	EXTI_Line0
#define US_EXTI_Chanel	EXTI0_IRQn

uint32_t echoTime = 0;

void US_init() {
	RCC_AHB1PeriphClockCmd(US_RCC, ENABLE);

	GPIO_InitTypeDef gpio_trig;
	GPIO_StructInit(&gpio_trig);
	gpio_trig.GPIO_Mode = GPIO_Mode_OUT;
	gpio_trig.GPIO_PuPd = GPIO_PuPd_NOPULL;
	gpio_trig.GPIO_Pin = US_TRIGGER_PIN;
	GPIO_Init(US_GPIO, &gpio_trig);
	GPIO_ResetBits(US_GPIO, US_TRIGGER_PIN );


	GPIO_InitTypeDef gpio_echo;
	GPIO_StructInit(&gpio_echo);
	gpio_echo.GPIO_Mode = GPIO_Mode_IN;
	gpio_echo.GPIO_PuPd = GPIO_PuPd_NOPULL;
	gpio_echo.GPIO_Pin = US_ECHO_PIN;
	GPIO_Init(GPIOD, &gpio_echo);
	GPIO_ResetBits(US_GPIO, US_ECHO_PIN );

	//Setup interrupts
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_SYSCFG, ENABLE);
	EXTI_InitTypeDef EXTI_InitStructure;
	SYSCFG_EXTILineConfig(US_EXTI_Port, US_EXTI_PIN );
	EXTI_InitStructure.EXTI_Line = US_EXTI_Line;
	EXTI_InitStructure.EXTI_Mode = EXTI_Mode_Interrupt;
	EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Rising_Falling;
	EXTI_InitStructure.EXTI_LineCmd = ENABLE;
	EXTI_Init(&EXTI_InitStructure);

	NVIC_InitTypeDef NVIC_InitStructure;
	NVIC_InitStructure.NVIC_IRQChannel = US_EXTI_Chanel;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 10;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 10;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);
	NVIC_SetPriority(EXTI0_IRQn, 10);

	NVIC_InitTypeDef NVIC_InitStructure1;
	/* Enable the TIM2 gloabal Interrupt */
	NVIC_InitStructure1.NVIC_IRQChannel = TIM2_IRQn;
	NVIC_InitStructure1.NVIC_IRQChannelPreemptionPriority = 11;
	NVIC_InitStructure1.NVIC_IRQChannelSubPriority = 11;
	NVIC_InitStructure1.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure1);
	NVIC_SetPriority(TIM2_IRQn, 11);

	RCC_ClocksTypeDef RCC_Clocks;
	RCC_GetClocksFreq(&RCC_Clocks);

	/* TIM2 clock enable */
	RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM2, ENABLE);
	/* Time base configuration */
	TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
	TIM_TimeBaseStructure.TIM_Period = 100 - 1; // 1 MHz down to 10- KHz (1 s)
	TIM_TimeBaseStructure.TIM_Prescaler = RCC_Clocks.PCLK1_Frequency/1000 - 1; // 42 MHz Clock down to 1 MHz (adjust per your clock)
	TIM_TimeBaseStructure.TIM_ClockDivision = 0;
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;
	TIM_TimeBaseInit(TIM2, &TIM_TimeBaseStructure);
	/* TIM IT enable */
	TIM_ITConfig(TIM2, TIM_IT_Update, ENABLE);
	/* TIM2 enable counter */
	TIM_Cmd(TIM2, ENABLE);
}

void trigger() {
	GPIO_SetBits(US_GPIO, US_TRIGGER_PIN );
	TIME_delayNano(10);
	GPIO_ResetBits(US_GPIO, US_TRIGGER_PIN );
}

void TIM2_IRQHandler(void) {
	if (TIM_GetITStatus(TIM2, TIM_IT_Update ) != RESET) {
		TIM_ClearITPendingBit(TIM2, TIM_IT_Update );
		trigger();
	}
}

void EXTI0_IRQHandler(void) {
	if (EXTI_GetITStatus(EXTI_Line0 ) != RESET) {
		/* Clear the EXTI line 0 pending bit */
		EXTI_ClearITPendingBit(EXTI_Line0 );
	} else {
		return;
	}
	if (GPIO_ReadInputDataBit(US_GPIO, US_ECHO_PIN ) == SET) {
		echoTime = TIME_nano;
	} else {
		uint32_t time = TIME_nano - echoTime;
		if (time > 38000) {
			US_distance = 400;
			return;
		}
		US_distance = time / 29 / 2;
		echoTime = 0;
	}
}
