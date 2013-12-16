package pro.kornev.kcontrol.view.panels.settings;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.SettingsListener;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.view.panels.CustomPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 12/12/13
 * Time: 1:10 PM
 *
 */
public class GpsSettings extends CustomPanel implements SettingsListener, ActionListener {
    private ProxyService proxyService;
    private final JCheckBox gpsEnabled;

    public GpsSettings() {
        super("GPS settings");
        gpsEnabled = new JCheckBox("GPS enabled");
        add(gpsEnabled, getGbl().setGrid(0,0));
        JButton apply = new JButton("Apply");
        add(apply, getGbl().setGrid(1, 0).anchorT().fillB());
        apply.addActionListener(this);
        SettingService.i.addListener(this);
    }

    @Override
    public void changeJoystick(KJoystick joystick) {

    }

    @Override
    public void changeProxy(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (proxyService == null) {
            return;
        }
        Data data = new Data();
        data.cmd = Protocol.Cmd.sensGps();
        data.bData = gpsEnabled.isSelected() ? (byte)1 : (byte)0;
        proxyService.send(data);
    }
}
