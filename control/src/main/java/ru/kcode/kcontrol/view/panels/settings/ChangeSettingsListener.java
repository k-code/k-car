package ru.kcode.kcontrol.view.panels.settings;

import ru.kcode.kcontrol.service.drivers.DeviceDriver;

import com.centralnexus.input.Joystick;

public interface ChangeSettingsListener {
    public void changeJoystick(Joystick newJoystick);
    public void changeDriver(DeviceDriver newDriver);
}
