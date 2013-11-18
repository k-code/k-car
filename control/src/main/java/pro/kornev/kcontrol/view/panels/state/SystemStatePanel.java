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

        JoystickViewPanel joystickViewPanel = new JoystickViewPanel();
        DistanceViewPanel distanceViewPanel = new DistanceViewPanel();

        add(new PingPanel(), getGbl().setGrid(0,0));
        add(joystickViewPanel, getGbl().setGrid(1,0));
        add(distanceViewPanel, getGbl().setGrid(2,0));
        add(new PreviewPanel("Preview"), getGbl().setGrid(0,1).fillH().weightV(1));
    }
}
