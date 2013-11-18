package pro.kornev.kcontrol.view.panels.state;

import pro.kornev.kcontrol.view.panels.CustomPanel;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 13.11.13
 * Time: 10:04
 */
public class SystemStatePanel extends CustomPanel {
    public SystemStatePanel() {
        super("System state");

        add(new PingPanel(), getGbl().setGrid(0,0));
        add(new JoystickViewPanel(), getGbl().setGrid(1,0));
        add(new DistanceViewPanel(), getGbl().setGrid(2,0));
        add(new PreviewPanel(), getGbl().setGrid(0,1).colSpan(2));
    }
}
