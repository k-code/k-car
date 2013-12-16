package pro.kornev.kcontrol.view.panels.settings;

import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import pro.kornev.kcontrol.view.GBLHelper;
import pro.kornev.kcontrol.view.panels.CustomPanel;
import pro.kornev.kcontrol.view.panels.state.PreviewPanel;

public class SettingsPanel extends CustomPanel {

    private static final long serialVersionUID = -5935642426362810839L;

    public SettingsPanel(String title) {
        super(title);

        add(new ProxySettingsPanel(), getGbl().setGrid(0, 0));
        add(new JoystickSettingsPanel(), getGbl().setGrid(1, 0));
        add(new PreviewSettings(), getGbl().setGrid(2, 0));
        add(new GpsSettings(), getGbl().setGrid(2, 1));
    }
}
