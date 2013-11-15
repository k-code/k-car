package pro.kornev.kcontrol.view.panels.settings;

import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import pro.kornev.kcontrol.view.GBLHelper;

public class SettingsPanel extends JPanel {

    private static final long serialVersionUID = -5935642426362810839L;

    public SettingsPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        GBLHelper c = GBLHelper.create().fillH().margin(2, 3).anchorT();
        add(new ProxySettingsPanel(), c.weightH(0.3).setGrid(0, 0));
        add(new JoystickSettingsPanel(), c.weightH(0.7).setGrid(1, 0));
    }
}