package pro.kornev.kcontrol.view.panels.state;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.service.network.ProxyServiceListener;
import pro.kornev.kcontrol.view.GBLHelper;
import pro.kornev.kcontrol.service.SettingsListener;
import pro.kornev.kcontrol.view.panels.CustomPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 12.11.13
 * Time: 16:09
 */
public class PingPanel extends CustomPanel {
    private static final String STATUS_DEFAULT = "-/-";
    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "ER";
    private static final int PING_TIMEOUT = 1000;

    private JLabel proxyStatus;
    private JLabel androidStatus;
    private JLabel stmStatus;
    private ProxyService proxyService;

    public PingPanel() {
        super("Ping all systems");

        JButton pingButton = new JButton("Ping");
        JLabel proxyLabel = new JLabel("Proxy:");
        JLabel androidLabel = new JLabel("Android:");
        JLabel stmLabel = new JLabel("STM:");

        proxyStatus = new JLabel(STATUS_DEFAULT);
        androidStatus = new JLabel(STATUS_DEFAULT);
        stmStatus = new JLabel(STATUS_DEFAULT);

        add(proxyLabel, getGbl().setGrid(0,0));
        add(androidLabel, getGbl().setGrid(0,1));
        add(stmLabel, getGbl().setGrid(0,2));
        add(proxyStatus, getGbl().setGrid(1,0));
        add(androidStatus, getGbl().setGrid(1,1));
        add(stmStatus, getGbl().setGrid(1,2));
        add(pingButton, getGbl().setGrid(0,3).colSpan());

        ActionListener pingButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (proxyService == null) {
                    return;
                }
                new Thread(new Ping()).start();
            }
        };
        pingButton.addActionListener(pingButtonListener);

        SettingsListener sl = new SettingsListener() {
            @Override
            public void changeJoystick(KJoystick joystick) {
            }

            @Override
            public void changeProxy(ProxyService ns) {
                proxyService = ns;
                proxyService.addListener(proxyServiceListener);
            }
        };
        SettingService.i.addListener(sl);
    }

    private ProxyServiceListener proxyServiceListener = new ProxyServiceListener() {
        @Override
        public void onPackageReceive(Data data) {
            if (data.cmd != Protocol.Cmd.ping()) {
                return;
            }
            if (data.bData == 1) {
                proxyStatus.setText(STATUS_OK);
            }
            else if (data.bData == 2) {
                androidStatus.setText(STATUS_OK);
            }
            else if (data.bData == 3) {
                stmStatus.setText(STATUS_OK);
            }
        }
    };

    private class Ping implements Runnable {

        @Override
        public void run() {
            proxyStatus.setText(STATUS_DEFAULT);
            androidStatus.setText(STATUS_DEFAULT);
            stmStatus.setText(STATUS_DEFAULT);

            Data data = new Data();
            data.cmd = Protocol.Cmd.ping();

            proxyService.send(data);

            try {
                Thread.sleep(PING_TIMEOUT);
            } catch (InterruptedException e) {
            }

            if (proxyStatus.getText().equals(STATUS_DEFAULT)) {
                proxyStatus.setText(STATUS_ERROR);
            }
            if (androidStatus.getText().equals(STATUS_DEFAULT)) {
                androidStatus.setText(STATUS_ERROR);
            }
            if (stmStatus.getText().equals(STATUS_DEFAULT)) {
                stmStatus.setText(STATUS_ERROR);
            }
        }
    }
}
