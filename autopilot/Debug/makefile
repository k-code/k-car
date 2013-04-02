################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include src/subdir.mk
-include inc/STM32_USB_OTG_Driver/src/subdir.mk
-include inc/STM32_USB_Device_Library/Core/src/subdir.mk
-include inc/STM32_USB_Device_Library/Class/cdc/src/subdir.mk
-include inc/STM32F4xx_StdPeriph_Driver/src/subdir.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(ASM_DEPS)),)
-include $(ASM_DEPS)
endif
ifneq ($(strip $(S_UPPER_DEPS)),)
-include $(S_UPPER_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 
SECONDARY_FLASH += \
autopilot.hex \

SECONDARY_LIST += \
autopilot.lst \

SECONDARY_SIZE += \
autopilot.siz \


# All Target
all: autopilot.elf secondary-outputs

# Tool invocations
autopilot.elf: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: ARM Sourcery Linux GCC C Linker'
	arm-none-eabi-gcc -T"/home/kvv/workspace/k-car/autopilot/stm32_flash.ld" -nostartfiles -Xlinker --gc-sections -Wl,-Map,autopilot.map -mcpu=cortex-m4 -mthumb -g3 -ggdb -o "autopilot.elf" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

autopilot.hex: autopilot.elf
	@echo 'Invoking: ARM Sourcery Linux GNU Create Flash Image'
	arm-none-eabi-objcopy -O binary autopilot.elf  "autopilot.hex"
	@echo 'Finished building: $@'
	@echo ' '

autopilot.lst: autopilot.elf
	@echo 'Invoking: ARM Sourcery Linux GNU Create Listing'
	arm-none-eabi-objdump -h -S autopilot.elf > "autopilot.lst"
	@echo 'Finished building: $@'
	@echo ' '

autopilot.siz: autopilot.elf
	@echo 'Invoking: ARM Sourcery Linux GNU Print Size'
	arm-none-eabi-size  --format=berkeley autopilot.elf
	@echo 'Finished building: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(SECONDARY_SIZE)$(OBJS)$(C_DEPS)$(ASM_DEPS)$(SECONDARY_FLASH)$(EXECUTABLES)$(SECONDARY_LIST)$(S_UPPER_DEPS) autopilot.elf
	-@echo ' '

secondary-outputs: $(SECONDARY_FLASH) $(SECONDARY_LIST) $(SECONDARY_SIZE)

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
