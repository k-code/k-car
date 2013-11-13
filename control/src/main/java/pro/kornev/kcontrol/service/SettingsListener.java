package pro.kornev.kcontrol.service;

import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;

public interface SettingsListener {
    public void changeJoystick(KJoystick joystick);
    public void changeProxy(ProxyService proxyService);
}
