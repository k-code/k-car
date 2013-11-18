package pro.kornev.kcontrol.view.panels.settings;

import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.view.GBLHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 12.11.13
 * Time: 11:00
 */

public class ProxySettingsPanel extends JPanel {
    private static final String DEFAULT_PROXY = "kornev.pro";
    private static final String DEFAULT_PORT = "6781";
    private JTextField proxyHost;
    private JTextField proxyPort;
    private ProxyService proxyService;

    public ProxySettingsPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder("Proxy settings"));
        setLayout(new GridBagLayout());

        JLabel proxyHostLabel = new JLabel("Proxy host");
        JLabel proxyPartLabel = new JLabel("Proxy port");
        proxyHost = new JTextField(DEFAULT_PROXY);
        proxyPort = new JTextField(DEFAULT_PORT);
        JButton connectButton = new JButton("Connect");
        ActionListener connectButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (proxyService != null) {
                    proxyService.shutdown();
                }
                String host = proxyHost.getText();
                int port = Integer.valueOf(proxyPort.getText());
                proxyService = new ProxyService(host, port);

                SettingService.i.fireChangeProxy(proxyService);
            }
        };
        connectButton.addActionListener(connectButtonListener);

        GBLHelper gbl = GBLHelper.create().weightH(1).fillH().margin(2, 3);

        add(proxyHostLabel, gbl.setGrid(0, 0));
        add(proxyPartLabel, gbl.setGrid(1, 0));
        add(proxyHost, gbl.setGrid(0, 1));
        add(proxyPort, gbl.setGrid(1, 1));
        add(connectButton, gbl.setGrid(0, 2).colSpan(2));
    }

}
