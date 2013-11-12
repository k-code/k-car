package pro.kornev.kcontrol.view.panels;

import pro.kornev.kcontrol.view.GBLHelper;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class DistanceViewPanel extends JPanel {
	private static final long serialVersionUID = -3113982496558550127L;

	private JLabel distanceLabel;
	private JLabel distanceValue;

	public DistanceViewPanel() {
		setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		initLabels();
	}

	private void initLabels() {
	    GBLHelper c = GBLHelper.create().margin(10, 20);
        distanceLabel = new JLabel("Distance to barrier:");
		this.add(distanceLabel, c.setGrid(0, 0));
		distanceValue = new JLabel("0");
		this.add(distanceValue, c.setGrid(1, 0));
	}

    public JLabel getDistanceLabel() {
        return distanceLabel;
    }

    public JLabel getDistanceValue() {
        return distanceValue;
    }
}
