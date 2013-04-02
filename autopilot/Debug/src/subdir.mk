################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/lis302dl.c \
../src/main.c \
../src/periph_init.c \
../src/protocol.c \
../src/stm32f4_discovery.c \
../src/stm32f4xx_it.c \
../src/system_stm32f4xx.c \
../src/usb_bsp.c \
../src/usbd_cdc_vcp.c \
../src/usbd_desc.c \
../src/usbd_usr.c 

OBJS += \
./src/lis302dl.o \
./src/main.o \
./src/periph_init.o \
./src/protocol.o \
./src/stm32f4_discovery.o \
./src/stm32f4xx_it.o \
./src/system_stm32f4xx.o \
./src/usb_bsp.o \
./src/usbd_cdc_vcp.o \
./src/usbd_desc.o \
./src/usbd_usr.o 

C_DEPS += \
./src/lis302dl.d \
./src/main.d \
./src/periph_init.d \
./src/protocol.d \
./src/stm32f4_discovery.d \
./src/stm32f4xx_it.d \
./src/system_stm32f4xx.d \
./src/usb_bsp.d \
./src/usbd_cdc_vcp.d \
./src/usbd_desc.d \
./src/usbd_usr.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: ARM Sourcery Linux GCC C Compiler'
	arm-none-eabi-gcc -DUSE_STDPERIPH_DRIVER -DUSE_STM32F4_DISCOVERY -DUSE_USB_OTG_FS -DHSE_VALUE=8000000 -DSTM32F4XX -I"/home/kvv/workspace/k-car/autopilot/inc" -I"/home/kvv/workspace/k-car/autopilot/inc/CMSIS" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32F4xx" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32F4xx_StdPeriph_Driver/inc" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32_USB_Device_Library/Class/cdc/inc" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32_USB_Device_Library/Core/inc" -I"/home/kvv/workspace/k-car/autopilot/inc/STM32_USB_OTG_Driver/inc" -O0 -ffunction-sections -fdata-sections -Wall -std=c99 -Wa,-adhlns="$@.lst" -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -mcpu=cortex-m4 -mthumb -g3 -ggdb -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


