/*
 * ultrasonic.c
 *
 *  Created on: Apr 6, 2013
 *      Author: kvv
 */

#include "ultrasonic.h"
#include "stm32f4xx_gpio.h"
#include "stm32f4xx_it.h"

extern __IO uint32_t SysTime;

#define US_GPIO GPIOD
#define US_RCC RCC_AHB1Periph_GPIOD
#define US_TRIGGER_PIN GPIO_Pin_1
#define US_ECHO_PIN GPIO_Pin_2

uint32_t echoTime = 0;

void ultrasonic_init() {
    RCC_AHB1PeriphClockCmd(US_RCC, ENABLE);

    GPIO_InitTypeDef gpio_trig;
    GPIO_StructInit(&gpio_trig);
    gpio_trig.GPIO_Mode = GPIO_Mode_OUT;
    gpio_trig.GPIO_PuPd = GPIO_PuPd_DOWN;
    gpio_trig.GPIO_Pin = US_TRIGGER_PIN;
    GPIO_Init(US_GPIO, &gpio_trig);

    GPIO_InitTypeDef gpio_echo;
    GPIO_StructInit(&gpio_echo);
    gpio_echo.GPIO_Mode = GPIO_Mode_IN;
    gpio_echo.GPIO_PuPd = GPIO_PuPd_NOPULL;
    gpio_echo.GPIO_Pin = US_ECHO_PIN;
    GPIO_Init(GPIOD, &gpio_echo);

    GPIO_ResetBits(US_GPIO, US_TRIGGER_PIN);

    //Setup interrupts
    // TODO : move to define


    EXTI_InitTypeDef   EXTI_InitStructure;
    SYSCFG_EXTILineConfig(EXTI_PortSourceGPIOD, EXTI_PinSource2);
    EXTI_InitStructure.EXTI_Line = EXTI_Line2;
    EXTI_InitStructure.EXTI_Mode = EXTI_Mode_Interrupt;
    EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Rising_Falling;
    EXTI_InitStructure.EXTI_LineCmd = ENABLE;
    EXTI_Init(&EXTI_InitStructure);

    NVIC_InitTypeDef NVIC_InitStructure;
    NVIC_InitStructure.NVIC_IRQChannel = EXTI2_IRQn;
    NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0x0F;
    NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0x0F;
    NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
    NVIC_Init(&NVIC_InitStructure);
    NVIC_EnableIRQ (EXTI2_IRQn);
}

void trigger() {
    GPIO_ResetBits(US_GPIO, US_TRIGGER_PIN);
    uint32_t time=SysTime + 2;
    while (time > SysTime) {
        __NOP();
    }
    GPIO_SetBits(US_GPIO, US_TRIGGER_PIN);
    time=SysTime + 10;
    while (time > SysTime) {
        __NOP();
    }
    GPIO_ResetBits(US_GPIO, US_TRIGGER_PIN);
}

void EXTI2_IRQHandler(){
    GPIO_ToggleBits(GPIOD, GPIO_Pin_13);
    if (GPIO_ReadInputDataBit(US_GPIO, US_ECHO_PIN) == SET) {
        echoTime = SysTime;
        EXTI_ClearITPendingBit(EXTI_Line0);
    }
    else {
        uint32_t time = SysTime - echoTime;
        if (time > 38000) {
            US_DISTANCE = 400;
            return;
        }
        US_DISTANCE = time /29 / 2;
        echoTime = 0;
        EXTI_ClearITPendingBit(EXTI_Line0);
    }
}
