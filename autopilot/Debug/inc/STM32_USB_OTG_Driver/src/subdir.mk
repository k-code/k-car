################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../inc/STM32_USB_OTG_Driver/src/usb_core.c \
../inc/STM32_USB_OTG_Driver/src/usb_dcd.c \
../inc/STM32_USB_OTG_Driver/src/usb_dcd_int.c 

OBJS += \
./inc/STM32_USB_OTG_Driver/src/usb_core.o \
./inc/STM32_USB_OTG_Driver/src/usb_dcd.o \
./inc/STM32_USB_OTG_Driver/src/usb_dcd_int.o 

C_DEPS += \
./inc/STM32_USB_OTG_Driver/src/usb_core.d \
./inc/STM32_USB_OTG_Driver/src/usb_dcd.d \
./inc/STM32_USB_OTG_Driver/src/usb_dcd_int.d 


# Each subdirectory must supply rules for building sources it contributes
inc/STM32_USB_OTG_Driver/src/%.o: ../inc/STM32_USB_OTG_Driver/src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: ARM Sourcery Linux GCC C Compiler'
	arm-none-eabi-gcc -DUSE_STDPERIPH_DRIVER -DUSE_STM32F4_DISCOVERY -DUSE_USB_OTG_FS -DHSE_VALUE=8000000 -DSTM32F4XX -I"/home/kvv/workspace/k-car/autopilot/inc" -I"/home/kvv/workspace/k-car/autopilot/inc/CMSIS" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32F4xx" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32F4xx_StdPeriph_Driver/inc" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32_USB_Device_Library/Class/cdc/inc" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32_USB_Device_Library/Core/inc" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32_USB_OTG_Driver/inc" -O0 -ffunction-sections -fdata-sections -Wall -std=c99 -Wa,-adhlns="$@.lst" -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -mcpu=cortex-m4 -mthumb -g3 -ggdb -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


