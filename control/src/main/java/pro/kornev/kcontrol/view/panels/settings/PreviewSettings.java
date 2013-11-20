package pro.kornev.kcontrol.view.panels.settings;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;
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
    private JTextField fps = null;
    private JTextField quality = null;
    private JButton resetCamera;

    public PreviewSettings(String title) {
        super(title);
        JButton apply = new JButton("Apply");
        apply.addActionListener(this);
        resetCamera = new JButton("Reset camera");
        resetCamera.addActionListener(this);
        apply.addActionListener(this);
        JLabel fpsLabel = new JLabel("FPS:");
        fps = new JTextField("1");
        JLabel qualityLabel = new JLabel("Quality:");
        quality = new JTextField("50");

        add(fpsLabel, getGbl().setGrid(0, 0).weightH(0.3));
        add(fps, getGbl().setGrid(1, 0).weightH(0.7));
        add(qualityLabel, getGbl().setGrid(0, 1));
        add(quality, getGbl().setGrid(1, 1));

        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.add(resetCamera, getGbl().setGrid(0, 0));
        buttons.add(apply, getGbl().setGrid(1, 0));
        add(buttons, getGbl().setGrid(0,2).colSpan());

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
        if (button.equals(resetCamera)) {
            Data data = new Data();
            data.cmd = Protocol.Cmd.camReset();
            proxyService.send(data);
            return;
        }

        Data data = new Data();
        data.cmd = Protocol.Cmd.camFps();
        data.bData = Byte.valueOf(fps.getText());
        proxyService.send(data);

        data = new Data();
        data.cmd = Protocol.Cmd.camQuality();
        data.bData = Byte.valueOf(quality.getText());
        proxyService.send(data);
    }
}
