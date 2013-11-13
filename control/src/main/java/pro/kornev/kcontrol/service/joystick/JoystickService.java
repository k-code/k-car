package pro.kornev.kcontrol.service.joystick;

import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.SettingsListener;
import pro.kornev.kcontrol.service.network.ProxyServiceListener;
import pro.kornev.kcontrol.service.network.ProxyService;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 13.11.13
 * Time: 15:51
 */
public class JoystickService implements JoystickListener {
    private ProxyService proxyService;
    private boolean liveLed = false;

    public JoystickService() {
        SettingService.i.addListener(new SettingsListener() {
            @Override
            public void changeJoystick(KJoystick j) {
            }

            @Override
            public void changeProxy(ProxyService ps) {
                proxyService = ps;
            }
        });
    }

    @Override
    public void joystickAxisChanged(Joystick joystick) {

    }

    @Override
    public void joystickButtonChanged(Joystick joystick) {
        int buttons = joystick.getButtons();
        if ((buttons & Joystick.BUTTON1) > 0) {
            Data data = new Data();
            data.id = 1;
            data.cmd = 2;
            data.type = 0;
            data.bData = (byte) (liveLed ? 0 : 1);
            proxyService.send(data);
            liveLed = !liveLed;
        }
    }
}
