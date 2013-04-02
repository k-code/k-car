/* Includes ------------------------------------------------------------------*/
#include "main.h"

/* Private typedef -----------------------------------------------------------*/
/* Private define ------------------------------------------------------------*/

/* Private macro -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
#ifdef USB_OTG_HS_INTERNAL_DMA_ENABLED
#if defined ( __ICCARM__ ) /*!< IAR Compiler */
#pragma data_alignment = 4
#endif
#endif /* USB_OTG_HS_INTERNAL_DMA_ENABLED */
__ALIGN_BEGIN USB_OTG_CORE_HANDLE USB_OTG_dev __ALIGN_END;

extern __I uint32_t SysTime;
extern CDC_IF_Prop_TypeDef  VCP_fops;

/* Private function prototypes -----------------------------------------------*/
static void sendData(void);
static void Delay(__IO uint32_t nTime);
static void getData(void);

/* Private functions ---------------------------------------------------------*/

/**
 * @brief  Main program.
 * @param  None
 * @retval None
 */
int main(void) {
    Data_get = 0;

    PERIPH_Init_SysTick();
    PERIPH_Init_Leds();
    PERIPH_Init_Timer();
    PERIPH_Init_PWM();
    /*PERIPH_Init_Spi();
    LIS302DL_Init();*/

    /* USB configuration */
    //USBD_Init(&USB_OTG_dev, USB_OTG_FS_CORE_ID, &USR_desc, &USBD_CDC_cb, &USR_cb);

    while (1) {
        GPIO_SetBits(GPIOD, GPIO_Pin_12);
        Delay(500000);
        GPIO_ResetBits(GPIOD, GPIO_Pin_12);
        Delay(500000);
    }
}

/**
 * @brief  Inserts a delay time.
 * @param  nTime: specifies the delay time length, in 10 ms.
 * @retval None
 */
static void Delay(__IO uint32_t nTime) {
    nTime += SysTime;

    while (nTime > SysTime) {
        __NOP();
    }
}


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
