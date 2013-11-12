package pro.kornev.kcontrol;

import javax.swing.SwingUtilities;

import pro.kornev.kcontrol.view.MainWindow;

public class Runner {

    public static void main(String[] args) throws Exception {
        MainWindow mainWindow;

        mainWindow = new MainWindow();
        SwingUtilities.invokeAndWait(mainWindow);

        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }

}