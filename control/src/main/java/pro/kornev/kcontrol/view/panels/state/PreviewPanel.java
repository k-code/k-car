package pro.kornev.kcontrol.view.panels.state;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.SettingsListener;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.service.network.ProxyServiceListener;
import pro.kornev.kcontrol.view.panels.CustomPanel;
import sun.awt.image.ByteArrayImageSource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 15.11.13
 * Time: 16:19
 */
public class PreviewPanel extends CustomPanel implements SettingsListener, ProxyServiceListener{
    private JLabel label;

    public PreviewPanel(String title) {
        super(title);
        SettingService.i.addListener(this);
        label = new JLabel("qwew");
        label.setMaximumSize(new Dimension(200, 200));
        add(label);
    }

    @Override
    public void changeJoystick(KJoystick joystick) {
    }

    @Override
    public void changeProxy(ProxyService proxyService) {
        proxyService.addListener(this);
    }

    @Override
    public void onPackageReceive(Data data) {
        if (data.cmd != 5) return;

        InputStream in = new ByteArrayInputStream(data.aData);
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        label.getGraphics().drawImage(bufImage, 0, 0, null);
        label.repaint();
        label.setIcon(new ImageIcon(bufImage));
        label.setText(String.valueOf(data.aSize));
        repaint();
    }
}
