package pro.kornev.kcontrol.view.panels.settings;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.SettingsListener;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.view.GBLHelper;
import pro.kornev.kcontrol.view.panels.CustomPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 18.11.13
 * Time: 10:38
 */
public class PreviewSettings extends CustomPanel implements SettingsListener, ActionListener {
    private ProxyService proxyService = null;
    private TextField fps = null;
    private JButton resetCamera;
    private JButton apply;

    public PreviewSettings(String title) {
        super(title);
        apply = new JButton("Apply");
        apply.addActionListener(this);
        resetCamera = new JButton("Reset camera");
        resetCamera.addActionListener(this);
        apply.addActionListener(this);
        JLabel fpsLabel = new JLabel("FPS:");
        fps = new TextField("1");


        add(fpsLabel, getGbl().setGrid(0, 0).weightH(0.3));
        add(fps, getGbl().setGrid(1, 0).weightH(0.7));
        add(apply, getGbl().setGrid(1, 1));
        add(resetCamera, getGbl().setGrid(0, 1));
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
        JButton button = (JButton)e.getSource();
        byte cmd;
        if (button.equals(apply)) {
            cmd = 7;
        } else {
            cmd = 8;
        }

        Data data = new Data();
        data.id = 335;
        data.cmd = cmd;
        data.type = 0;
        data.bData = Byte.valueOf(fps.getText());
        proxyService.send(data);
    }
}
