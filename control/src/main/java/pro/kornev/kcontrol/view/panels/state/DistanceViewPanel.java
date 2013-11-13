package pro.kornev.kcontrol.view.panels.state;

import pro.kornev.kcontrol.view.panels.CustomPanel;

import javax.swing.*;

public class DistanceViewPanel extends CustomPanel {
	private static final long serialVersionUID = -3113982496558550127L;

	private JLabel distanceLabel;
	private JLabel distanceValue;

	public DistanceViewPanel() {
        super("Distance");
		initLabels();
	}

	private void initLabels() {
        distanceLabel = new JLabel("Distance to barrier:");
		this.add(distanceLabel, gbl.setGrid(0, 0));
		distanceValue = new JLabel("0");
		this.add(distanceValue, gbl.setGrid(1, 0));
	}

    public JLabel getDistanceLabel() {
        return distanceLabel;
    }

    public JLabel getDistanceValue() {
        return distanceValue;
    }
}
