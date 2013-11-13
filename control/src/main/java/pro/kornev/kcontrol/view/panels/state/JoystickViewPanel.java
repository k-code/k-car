package pro.kornev.kcontrol.view.panels.state;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import pro.kornev.kcontrol.view.GBLHelper;
import pro.kornev.kcontrol.view.panels.CustomPanel;

public class JoystickViewPanel extends CustomPanel {
	private static final long serialVersionUID = -3113982496558550127L;

	private JLabel axisLabelX;
	private JLabel axisLabelY;
	private JLabel axisLabelZ;
	private JLabel axisLabelR;

	private JLabel axisValueX;
	private JLabel axisValueY;
	private JLabel axisValueZ;
	private JLabel axisValueR;
	
	public JoystickViewPanel() {
        super("Joystick state");
		initAxisLabels();
		initAxisValues();
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
        axisLabelY = new JLabel("Y:");
		this.add(axisLabelY, gbl.setGrid(0, 0));
		axisLabelX = new JLabel("X:");
		this.add(axisLabelX, gbl.setGrid(0, 1));
		axisLabelZ = new JLabel("Z:");
		this.add(axisLabelZ, gbl.setGrid(2, 0));
		axisLabelR = new JLabel("R:");
		this.add(axisLabelR, gbl.setGrid(2, 1));
	}
	
	private void initAxisValues() {
        axisValueY = new JLabel("0");
        this.add(axisValueY, gbl.setGrid(1, 0));
		axisValueX = new JLabel("0");
		this.add(axisValueX, gbl.setGrid(1, 1));
		axisValueZ = new JLabel("0");
		this.add(axisValueZ, gbl.setGrid(3, 0));
		axisValueR = new JLabel("0");
		this.add(axisValueR, gbl.setGrid(3, 1));
	}
}
