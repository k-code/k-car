package pro.kornev.kcontrol.view.panels.settings;

import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import pro.kornev.kcontrol.view.GBLHelper;

public class SettingsPanel extends JPanel {
    private Set<SettingsListener> settingsListeners;

    private static final long serialVersionUID = -5935642426362810839L;

    public SettingsPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        settingsListeners = new HashSet<>();
        
        GBLHelper c = GBLHelper.create().fillH().margin(2, 3).anchorT();
        add(new ProxySettingsPanel(settingsListeners), c.weightH(0.3).setGrid(0, 0));
        add(new JoystickSettingsPanel(settingsListeners), c.weightH(0.7).setGrid(1, 0));
    }

    public void addListener(SettingsListener listener) {
        settingsListeners.add(listener);
    }
}
