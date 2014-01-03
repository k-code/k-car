package pro.kornev.kcontrol.view.panels.drive;

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
 * User: kvv
 * Date: 1/3/14
 * Time: 12:48 PM
 */
public class DrivePanel extends CustomPanel implements ActionListener, SettingsListener {
    private JTextField front;
    private JTextField right;
    private ProxyService proxyService;

    public DrivePanel() {
        super("Drive view");
        front = new JTextField("0");
        right = new JTextField("0");

        add(front, getGbl().setGrid(0,0));
        add(right, getGbl().setGrid(0,1));

        JButton apply = new JButton("Run");

        add(apply, getGbl().setGrid(0,2));
        apply.addActionListener(this);
        SettingService.i.addListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (proxyService == null) return;
        Data data = new Data();
        data.cmd = Protocol.Cmd.autoLMS();
        data.bData = Byte.valueOf(front.getText());
        proxyService.send(data);

        data = new Data();
        data.cmd = Protocol.Cmd.autoRMS();
        data.bData = Byte.valueOf(front.getText());
        proxyService.send(data);
        front.setText("0");
    }

    @Override
    public void changeJoystick(KJoystick joystick) {

    }

    @Override
    public void changeProxy(ProxyService proxyService) {
        this.proxyService = proxyService;
    }
}
