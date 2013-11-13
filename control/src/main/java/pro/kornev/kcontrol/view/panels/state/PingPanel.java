package pro.kornev.kcontrol.view.panels.state;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.service.network.NetworkServiceListener;
import pro.kornev.kcontrol.view.GBLHelper;
import pro.kornev.kcontrol.view.MainWindow;
import pro.kornev.kcontrol.view.panels.settings.SettingsListener;

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
public class PingPanel extends JPanel {
    private static final String STATUS_DEFAULT = "-/-";
    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "ER";
    private static final int PING_TIMEOUT = 1000;

    private JLabel proxyStatus;
    private JLabel androidStatus;
    private JLabel stmStatus;
    private ProxyService proxyService;

    public PingPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder("Ping all systems"));
        setLayout(new GridBagLayout());

        JButton pingButton = new JButton("Ping");
        JLabel proxyLabel = new JLabel("Proxy:");
        JLabel androidLabel = new JLabel("Android:");
        JLabel stmLabel = new JLabel("STM:");

        proxyStatus = new JLabel(STATUS_DEFAULT);
        androidStatus = new JLabel(STATUS_DEFAULT);
        stmStatus = new JLabel(STATUS_DEFAULT);

        GBLHelper gbl = GBLHelper.create().weightH(1).fillH().margin(2, 3);
        add(proxyLabel, gbl.setGrid(0,0));
        add(androidLabel, gbl.setGrid(0,1));
        add(stmLabel, gbl.setGrid(0,2));
        add(proxyStatus, gbl.setGrid(1,0));
        add(androidStatus, gbl.setGrid(1,1));
        add(stmStatus, gbl.setGrid(1,2));
        add(pingButton, gbl.setGrid(0,3).colSpan());

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
                proxyService.addListener(networkServiceListener);
            }
        };
        SettingService.i.addListener(sl);
    }

    private NetworkServiceListener networkServiceListener = new NetworkServiceListener() {
        @Override
        public void onPackageReceive(Data data) {
            if (data.cmd != 1) {
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
            data.id = 1;
            data.cmd = 1;
            data.type = 0;
            data.bData = 0;

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
