package pro.kornev.kcontrol.view.panels;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcontrol.service.RelationsController;
import pro.kornev.kcontrol.view.GBLHelper;

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

    public PingPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder("Proxy settings"));
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

        pingButton.addActionListener(pingButtonListener);
    }
    
    private ActionListener pingButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Data ping = new Data();
            ping.id = 1;
            ping.cmd = 1;
            ping.type = 0;
            ping.bData = 0;
            RelationsController.getOutputQueue().add(ping);
            long endTime = System.currentTimeMillis() + PING_TIMEOUT;
            while (endTime > System.currentTimeMillis()) {
                if (RelationsController.getInputQueue().isEmpty()) {
                    continue;
                }
                Data response = RelationsController.getInputQueue().peek();
                if (response.cmd != 1) {
                    continue;
                }
                RelationsController.getInputQueue().remove(response);
                if (response.bData == 1) {
                    proxyStatus.setText(STATUS_OK);
                }
                else if (response.bData == 2) {
                    androidStatus.setText(STATUS_OK);
                }
                else if (response.bData == 3) {
                    stmStatus.setText(STATUS_OK);
                }
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
    };
}
