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

void USB_init();
void USB_write(PROTOCOL_data data);
PROTOCOL_data USB_nextData();
void USB_read(uint8_t *, uint32_t);

#endif /* USB_H_ */
