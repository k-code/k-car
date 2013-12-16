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
public class SensorsSettings extends CustomPanel implements SettingsListener, ActionListener {
    private ProxyService proxyService;
    private final JCheckBox gps;
    private final JCheckBox location;
    private final JCheckBox orientation;
    private final JCheckBox light;

    public SensorsSettings() {
        super("Sensors settings");

        location = new JCheckBox("Location");
        gps = new JCheckBox("GPS");
        orientation = new JCheckBox("Orientation");
        light = new JCheckBox("Light");

        add(location, getGbl().setGrid(0,0));
        add(gps, getGbl().setGrid(0,1));
        add(orientation, getGbl().setGrid(0,2));
        add(light, getGbl().setGrid(0,3));

        JButton apply = new JButton("Apply");
        add(apply, getGbl().setGrid(0, 4).anchorT().fillB());
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
        data.cmd = Protocol.Cmd.sensLocation();
        data.bData = location.isSelected() ? Protocol.Req.on() : Protocol.Req.off();
        proxyService.send(data);
        data = new Data();
        data.cmd = Protocol.Cmd.sensGps();
        data.bData = gps.isSelected() ? Protocol.Req.on() : Protocol.Req.off();
        proxyService.send(data);
        data = new Data();
        data.cmd = Protocol.Cmd.sensOrient();
        data.bData = orientation.isSelected() ? Protocol.Req.on() : Protocol.Req.off();
        proxyService.send(data);
        data = new Data();
        data.cmd = Protocol.Cmd.sensLight();
        data.bData = light.isSelected() ? Protocol.Req.on() : Protocol.Req.off();
        proxyService.send(data);
    }
}
