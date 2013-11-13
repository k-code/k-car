package pro.kornev.kcontrol.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.*;

import pro.kornev.kcontrol.view.panels.settings.SettingsPanel;
import pro.kornev.kcontrol.view.panels.state.SystemStatePanel;

public final class MainWindow extends JFrame implements Runnable {
    private static final long serialVersionUID = 6690894233205194578L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    // TODO : remove this porn
    public static SettingsPanel settingsPanel;

    @Override
    public void run() {
        setDefaultLookAndFeelDecorated(true);
        setTitle("K-Control");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        setLayout(new BorderLayout());
        Dimension size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);
        //setMinimumSize(size);

        JPanel mainPanel = new JPanel(new GridBagLayout());

        settingsPanel = new SettingsPanel();

        JTabbedPane mainTabbedPane = new JTabbedPane();
        mainTabbedPane.add("State view", new SystemStatePanel());
        mainTabbedPane.add("Settings panel", settingsPanel);

        mainPanel.add(mainTabbedPane, GBLHelper.create().setGrid(0, 0).fillB().anchorT().colSpan().fillB());

        add(mainPanel, BorderLayout.CENTER);
        
        pack();
    }
}
