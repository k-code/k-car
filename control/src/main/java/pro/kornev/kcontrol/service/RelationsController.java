package pro.kornev.kcontrol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcontrol.service.joystick.DriverJoystickListener;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.view.JoystickViewListener;
import pro.kornev.kcontrol.view.graphics.Copter3dView;
import pro.kornev.kcontrol.view.panels.DistanceViewPanel;
import pro.kornev.kcontrol.view.panels.JoystickViewPanel;

import com.centralnexus.input.Joystick;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RelationsController {
    private static Logger log = LoggerFactory.getLogger(RelationsController.class);
    private static KJoystick joystick;
    private static JoystickViewPanel joystickView;
    private static DistanceViewPanel distanceViewPanel;
    private static JoystickViewListener jvl;
    private static DriverJoystickListener djl;
    private static Copter3dView copter3dView;
    private static Queue<Data> inputQueue;
    private static Queue<Data> outputQueue;

    public static void setJoystick(Joystick j) {
        log.debug("Set joystick: "+j);
        if (joystick != null) {
            joystick.removeAllListeners();
        }
        
        if (j == null) {
            joystick = null;
            return;
        }
        joystick = new KJoystick(j);
        addJoystickViewListener();
    }
    
    public static KJoystick getJoystick() {
        return joystick;
    }

    public static JoystickViewPanel getJoystickView() {
        return joystickView;
    }

    public static DistanceViewPanel getDistanceViewPanel() {
        return distanceViewPanel;
    }

    public static void setDistanceViewPanel(DistanceViewPanel distanceViewPanel) {
        RelationsController.distanceViewPanel = distanceViewPanel;
    }

    public static void setJoystickView(JoystickViewPanel jv) {
        log.debug("Set joystick view");
        joystickView = jv;
        addJoystickViewListener();
    }
    
    private static void addJoystickViewListener() {
        log.debug("Add joystick view listener");
        if (joystick == null || joystickView == null) {
            return;
        }
        if (jvl == null) {
            jvl = new JoystickViewListener(joystickView);
        }
        joystick.addListener(jvl);
    }

    public static Queue<Data> getInputQueue() {
        if (inputQueue == null) {
            inputQueue = new LinkedBlockingQueue<Data>();
        }
        return inputQueue;
    }

    public static Queue<Data> getOutputQueue() {
        if (outputQueue == null) {
            outputQueue = new LinkedBlockingQueue<Data>();
        }
        return outputQueue;
    }
}
