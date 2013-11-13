package pro.kornev.kcontrol.view.panels.settings;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.centralnexus.input.Joystick;

import pro.kornev.kcontrol.service.RelationsController;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.view.GBLHelper;

public class JoystickSettingsPanel extends JPanel {
    private static final long serialVersionUID = -1854448935344200361L;

    private JComboBox<Joystick> joysticksBox;
    private JButton updateDevicesButton;
    private Set<SettingsListener> settingsListeners;
    private KJoystick joystick;

    public JoystickSettingsPanel(Set<SettingsListener> listeners) {
        super();
        this.settingsListeners = listeners;
        setBorder(BorderFactory.createTitledBorder("Joystick settings"));
        setLayout(new GridBagLayout());
        init();
    }

    private void init() {
        JoystickPanelListener listener = new JoystickPanelListener();
        joysticksBox = new JComboBox<>();
        joysticksBox.addActionListener(listener);
        joysticksBox.setMinimumSize(new Dimension(100, 20));
        add(joysticksBox, GBLHelper.create().weightH(1).fillH().margin(2, 3).setGrid(0, 0));

        updateDevicesButton = new JButton("Update");
        updateDevicesButton.addActionListener(listener);
        add(updateDevicesButton, GBLHelper.create().weightH(0.1).fillH().margin(2, 3).setGrid(1, 0));

        createJoysticksList();
    }

    private void createJoysticksList() {
        joysticksBox.removeAllItems();
        int jCount = Joystick.getNumDevices();
        for (int i = 0; i < jCount; i++) {
            try {
                joysticksBox.addItem(Joystick.createInstance(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDevicesButtonHandler() {
        createJoysticksList();
    }
    
    private void fireChangeJoystickSettings() {
        Joystick j = (Joystick)joysticksBox.getSelectedItem();
        if (joystick != null) {
            joystick.removeAllListeners();
        }
        if (j != null) {
            for (SettingsListener listener: settingsListeners) {
                listener.changeJoystick(new KJoystick(j));
            }
        }
    }

    private class JoystickPanelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source.equals(updateDevicesButton)) {
                updateDevicesButtonHandler();
            } else if (source.equals(joysticksBox)) {
                fireChangeJoystickSettings();
            }

        }
    }
}
