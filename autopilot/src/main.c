/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "leds.h"
#include "time.h"
#include "ultrasonic.h"
#include "motors.h"
#include "usb.h"
#include "protocol.h"

/* Private typedef -----------------------------------------------------------*/
/* Private define ------------------------------------------------------------*/

/* Private macro -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
/* Private function prototypes -----------------------------------------------*/
/* Private functions ---------------------------------------------------------*/

/**
 * @brief  Main program.
 * @param  None
 * @retval None
 */
int main(void) {
	LEDS_init();
	TIME_init();
	US_init();
	MOTORS_init();
	USB_init();
	//int x = 500;

	do {
		//TIME_delay(1000);

		for (int i=0; i < 10000000; i++) {}
		LEDS_live(!LEDS_live_state());
		/*if (US_distance > 30) {
			MOTORS_forward(1000);
		}
		else {
			MOTORS_right(100);
		}*/
		/*PROTOCOL_data data = {0,0,0,0,0};//PROTOCOL_emptyData;
		data.id = 1;
		data.cmd = 2;
		data.type = 0;
		data.bData = 3;
		USB_write(data);*/
		/*LEDS_live(LEDS_On);
		TIME_delay(US_distance*10);
		LEDS_live(LEDS_Off);
		TIME_delay(US_distance*10);*/

		/*if (x > 1000) x = 600;
		TIME_delay(1000);
		MOTORS_forward(x);
		x+= 10;*/
		USB_power(1);
		//TIME_delay(100);
		for (int i=0; i < 10000000; i++) {}
		USB_power(0);

	} while (1);
}

/**
 * @brief  Inserts a delay time.
 * @param  nTime: specifies the delay time length, in 10 ms.
 * @retval None
 */


#ifdef  USE_FULL_ASSERT

/**
 * @brief  Reports the name of the source file and the source line number
 *   where the assert_param error has occurred.
 * @param  file: pointer to the source file name
 * @param  line: assert_param error line source number
 * @retval None
 */
void assert_failed(uint8_t* file, uint32_t line)
{
    /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */

    /* Infinite loop */
    while (1)
    {
    }
}
#endif

/**
 * @}
 */

/******************* (C) COPYRIGHT 2011 STMicroelectronics *****END OF FILE****/
