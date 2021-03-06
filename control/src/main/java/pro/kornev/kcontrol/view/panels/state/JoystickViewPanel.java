package pro.kornev.kcontrol.view.panels.state;

import javax.swing.JLabel;

import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.view.panels.CustomPanel;
import pro.kornev.kcontrol.service.SettingsListener;

public class JoystickViewPanel extends CustomPanel {
	private static final long serialVersionUID = -3113982496558550127L;

    private JLabel axisValueX;
	private JLabel axisValueY;
	private JLabel axisValueZ;
	private JLabel axisValueR;
	
	public JoystickViewPanel() {
        super("Joystick state");
		initAxisLabels();
		initAxisValues();
        addChangeSettingsListener();
	}

	public JLabel getAxisValueX() {
		return axisValueX;
	}

	public JLabel getAxisValueY() {
		return axisValueY;
	}

	public JLabel getAxisValueZ() {
		return axisValueZ;
	}

	public JLabel getAxisValueR() {
		return axisValueR;
	}

	private void initAxisLabels() {
        JLabel axisLabelY = new JLabel("Y:");
		this.add(axisLabelY, getGbl().setGrid(0, 0));
        JLabel axisLabelX = new JLabel("X:");
		this.add(axisLabelX, getGbl().setGrid(0, 1));
        JLabel axisLabelZ = new JLabel("Z:");
		this.add(axisLabelZ, getGbl().setGrid(2, 0));
        JLabel axisLabelR = new JLabel("R:");
		this.add(axisLabelR, getGbl().setGrid(2, 1));
	}
	
	private void initAxisValues() {
        axisValueY = new JLabel("0");
        this.add(axisValueY, getGbl().setGrid(1, 0));
		axisValueX = new JLabel("0");
		this.add(axisValueX, getGbl().setGrid(1, 1));
		axisValueZ = new JLabel("0");
		this.add(axisValueZ, getGbl().setGrid(3, 0));
		axisValueR = new JLabel("0");
		this.add(axisValueR, getGbl().setGrid(3, 1));
	}

    private void addChangeSettingsListener() {
        SettingsListener settingsListener = new SettingsListener() {
            @Override
            public void changeJoystick(KJoystick joystick) {
                joystick.addListener(joystickListener);
            }

            @Override
            public void changeProxy(ProxyService networkService) {
            }
        };
        SettingService.i.addListener(settingsListener);
    }

    private JoystickListener joystickListener = new JoystickListener() {
        @Override
        public void joystickAxisChanged(Joystick joystick) {
            KJoystick kj = new KJoystick(joystick);
            axisValueX.setText(floatToString(kj.getX()));
            axisValueY.setText(floatToString(kj.getY()));
            axisValueZ.setText(floatToString(kj.getZ()));
            axisValueR.setText(floatToString(kj.getR()));
        }

        @Override
        public void joystickButtonChanged(Joystick joystick) {
        }
    };

    private String floatToString(float f) {
        return String.format("%.3f", Float.valueOf(f));
    }
}
