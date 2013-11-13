package pro.kornev.kcontrol.view.panels;

import pro.kornev.kcontrol.view.GBLHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 13.11.13
 * Time: 10:05
 */
public class CustomPanel extends JPanel {
    protected GBLHelper gbl;

    public CustomPanel(String title) {
        super();
        setBorder(BorderFactory.createTitledBorder(title));
        setLayout(new GridBagLayout());
        gbl =  GBLHelper.create().weightH(1).fillH().margin(2, 3).anchorT();
    }
}
