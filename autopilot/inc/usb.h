/*
 * usb.h
 *
 *  Created on: Oct 5, 2013
 *      Author: kvv
 */

#ifndef USB_H_
#define USB_H_

#include "stm32f4xx.h"
#include "protocol.h"

#define USB_POWER_PIN GPIO_Pin_8
#define USB_POWER_BUS GPIOA

void USB_init();
void USB_write(PROTOCOL_data data);
void USB_read(uint8_t *, uint32_t);
void USB_power(uint8_t);

#endif /* USB_H_ */
