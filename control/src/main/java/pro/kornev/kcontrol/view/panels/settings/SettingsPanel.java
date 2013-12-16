package pro.kornev.kcontrol.view.panels.settings;

import pro.kornev.kcontrol.view.panels.CustomPanel;

public class SettingsPanel extends CustomPanel {

    private static final long serialVersionUID = -5935642426362810839L;

    public SettingsPanel(String title) {
        super(title);

        add(new ProxySettingsPanel(), getGbl().setGrid(0, 0));
        add(new JoystickSettingsPanel(), getGbl().setGrid(1, 0));
        add(new PreviewSettings(), getGbl().setGrid(2, 0));
        add(new SensorsSettings(), getGbl().setGrid(2, 1));
    }
}
