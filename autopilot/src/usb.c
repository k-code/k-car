#include "usb.h"
#include "usbd_cdc_core.h"
#include "usbd_usr.h"
#include "usbd_desc.h"
#include "usbd_cdc_vcp.h"
#include "leds.h"
#include "ultrasonic.h"

#ifdef USB_OTG_HS_INTERNAL_DMA_ENABLED
  #if defined ( __ICCARM__ ) /*!< IAR Compiler */
    #pragma data_alignment = 4
  #endif
#endif /* USB_OTG_HS_INTERNAL_DMA_ENABLED */
__ALIGN_BEGIN USB_OTG_CORE_HANDLE  USB_OTG_dev __ALIGN_END;

typedef struct dataStackType {
	PROTOCOL_data data;
	struct dataStackType *next;
} dataStackType;

dataStackType *stackHead = NULL;
dataStackType *stackTail = NULL;

void USB_init() {
	/* USB configuration */
	USBD_Init(&USB_OTG_dev,
		USB_OTG_FS_CORE_ID,
		&USR_desc,
		&USBD_CDC_cb,
		&USR_cb);
}

void USB_write(PROTOCOL_data data) {
	uint8_t buf[64];
	uint32_t len = PROTOCOL_toByteArray(data, buf);
    VCP_DataTx(buf, len);
}

PROTOCOL_data USB_nextData() {
	PROTOCOL_data data = {0,0,0,0,0};//PROTOCOL_emptyData;
	if (stackHead == NULL) {
		return data;
	}
	data = stackHead->data;
	stackHead = stackHead->next;
	return data;
}

void USB_read(uint8_t *buf, uint32_t len) {
	PROTOCOL_data data = PROTOCOL_fromByteArray(buf, len);
	if (data.id == 0) {
		return;
	} else if (data.cmd == 1) {
		data.bData = 3;
		USB_write(data);
	} else if (data.cmd == 2) {
		if (data.bData == 0) {
			LEDS_live(LEDS_Off);
		}
		else {
			LEDS_live(LEDS_On);
		}
	} else if (data.cmd == 3) {
		data.id = 12;
		data.cmd = 4;
		data.type = 1;
		data.iData = US_distance;
		USB_write(data);
	} else if (data.cmd == 45) {
		MOTORS_LMS(data.bData);
	} else if (data.cmd == 46) {
		MOTORS_RMS(data.bData);
	}
	/*
	dataStackType tmp;
	tmp.data = data;

	if (stackHead) {
		stackTail->next = &tmp;
		stackTail = &tmp;
	}
	else {
		stackHead = stackTail = &tmp;
	}*/
}

