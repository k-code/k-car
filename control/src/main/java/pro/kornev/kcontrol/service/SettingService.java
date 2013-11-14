package pro.kornev.kcontrol.service;

import pro.kornev.kcontrol.service.joystick.JoystickService;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 13.11.13
 * Time: 15:13
 */
public enum  SettingService {
    i;
    private Set<SettingsListener> settingsListeners;
    private JoystickService joystickService;

    private SettingService() {
        settingsListeners = new HashSet<>();
        joystickService = new JoystickService();
        settingsListeners.add(joystickService);
    }

    public void fireChangeJoystick(KJoystick joystick) {
        joystick.addListener(joystickService);
        for (SettingsListener l: settingsListeners) {
            l.changeJoystick(joystick);
        }
    }

    public void fireChangeProxy(ProxyService proxyService) {
        for (SettingsListener l: settingsListeners) {
            l.changeProxy(proxyService);
        }
    }

    public void addListener(SettingsListener listener) {
        settingsListeners.add(listener);
    }
}
