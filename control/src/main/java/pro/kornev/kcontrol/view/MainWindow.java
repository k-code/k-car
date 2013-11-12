package pro.kornev.kcontrol.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.centralnexus.input.Joystick;

import pro.kornev.kcontrol.service.RelationsController;
import pro.kornev.kcontrol.view.panels.DistanceViewPanel;
import pro.kornev.kcontrol.view.panels.JoystickViewPanel;
import pro.kornev.kcontrol.view.panels.PingPanel;
import pro.kornev.kcontrol.view.panels.settings.ChangeSettingsListener;
import pro.kornev.kcontrol.view.panels.settings.SettingsPanel;

public final class MainWindow extends JFrame implements Runnable {
    private static final long serialVersionUID = 6690894233205194578L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    @Override
    public void run() {
        setDefaultLookAndFeelDecorated(true);
        setTitle("K-Control");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        setLayout(new BorderLayout());
        Dimension size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);
        setMinimumSize(size);

        JPanel mainPanel = new JPanel(new GridBagLayout());

        JoystickViewPanel joystickViewPanel = new JoystickViewPanel();
        DistanceViewPanel distanceViewPanel = new DistanceViewPanel();

        RelationsController.setJoystickView(joystickViewPanel);
        RelationsController.setDistanceViewPanel(distanceViewPanel);

        JPanel statusViewPanel = new JPanel(new GridBagLayout());

        statusViewPanel.add(joystickViewPanel, GBLHelper.create().setGrid(0, 0).fillH().anchorT().margin(0, 3));
        statusViewPanel.add(distanceViewPanel, GBLHelper.create().setGrid(0, 1).fillH().anchorT().margin(0, 3));
        statusViewPanel.add(new PingPanel(), GBLHelper.create().setGrid(0, 2).fillH().anchorT().margin(0, 3));

        JTabbedPane mainTabbedPane = new JTabbedPane();
        mainTabbedPane.add("Status view", statusViewPanel);

        mainTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
            }
        });

        addSettingsPanel(mainTabbedPane);

        mainPanel.add(mainTabbedPane, GBLHelper.create().setGrid(0, 0).fillB().anchorT().colSpan().fillB());

        add(mainPanel, BorderLayout.CENTER);
        
        pack();
    }

    private class ChangeSettingsListenerImpl implements ChangeSettingsListener {

        @Override
        public void changeJoystick(Joystick newJoystick) {
            RelationsController.setJoystick(newJoystick);
        }
    }

    private void addSettingsPanel(JTabbedPane tabbedPane) {
        SettingsPanel settingsPanel = new SettingsPanel();
        tabbedPane.add("Settings panel", settingsPanel);
        settingsPanel.addListener(new ChangeSettingsListenerImpl());
    }
}
