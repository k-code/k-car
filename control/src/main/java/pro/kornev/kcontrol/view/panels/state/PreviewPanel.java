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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 15.11.13
 * Time: 16:19
 */
public class PreviewPanel extends CustomPanel implements SettingsListener, ProxyServiceListener, ActionListener {
    private static final String START_PREVIEW = "Start preview";
    private static final String STOP_PREVIEW = "Stop preview";
    private boolean isStartPreview = false;
    private JButton startPreviewButton;
    private ProxyService proxyService;
    private JPanel preview;

    public PreviewPanel(String title) {
        super(title);
        SettingService.i.addListener(this);
        preview = new JPanel(new FlowLayout());

        preview.setPreferredSize(new Dimension(640, 480));
        preview.setMaximumSize(new Dimension(200, 200));
        preview.setMinimumSize(new Dimension(200, 200));
        add(preview, getGbl().setGrid(0, 0).fillB());
        startPreviewButton = new JButton(isStartPreview ? STOP_PREVIEW : START_PREVIEW);
        startPreviewButton.addActionListener(this);
        add(startPreviewButton, getGbl().setGrid(0, 1));
    }

    @Override
    public void changeJoystick(KJoystick joystick) {
    }

    @Override
    public void changeProxy(ProxyService proxyService) {
        proxyService.addListener(this);
        this.proxyService = proxyService;
    }

    @Override
    public void onPackageReceive(Data data) {
        if (data.cmd != 5 || !preview.isShowing()) return;

        InputStream in = new ByteArrayInputStream(data.aData);
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bufImage == null) {
            return;
        }

        preview.getGraphics().drawImage(bufImage, 0, 0, bufImage.getWidth(), bufImage.getHeight(), null);
        //preview.repaint();
        //label.setIcon(new ImageIcon(bufImage));
        //repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Data data = new Data();
        data.id = 334;
        data.cmd = 6;
        data.type = 0;
        data.bData = isStartPreview ? (byte)0 : (byte)1;
        proxyService.send(data);
        isStartPreview = !isStartPreview;
        startPreviewButton.setText(isStartPreview ? STOP_PREVIEW : START_PREVIEW);
    }


}
