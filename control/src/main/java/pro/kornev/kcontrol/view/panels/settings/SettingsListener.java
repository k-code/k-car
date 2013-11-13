package pro.kornev.kcontrol.view.panels.settings;

import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.NetworkService;

public interface SettingsListener {
    // TODO : put parameters to methods
    public void changeJoystick(KJoystick joystick);
    public void changeProxy(NetworkService networkService);
}
