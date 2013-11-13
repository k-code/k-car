package pro.kornev.kcontrol.view.panels.state;

import pro.kornev.kcontrol.service.RelationsController;
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

        add(new PingPanel(), gbl.setGrid(0,0));
        add(joystickViewPanel, gbl.setGrid(1,0));
        add(distanceViewPanel, gbl.setGrid(2,0));

        RelationsController.setJoystickView(joystickViewPanel);
        RelationsController.setDistanceViewPanel(distanceViewPanel);
    }
}
