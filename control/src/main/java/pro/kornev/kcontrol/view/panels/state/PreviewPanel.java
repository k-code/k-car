package pro.kornev.kcontrol.view.panels.state;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.SettingsListener;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.service.network.ProxyServiceListener;
import pro.kornev.kcontrol.view.panels.CustomPanel;

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
    private static final Dimension minSize = new Dimension(32, 24);
    private static final Dimension preferredSize = new Dimension(320, 240);
    private static final Dimension maxSize = new Dimension(640, 480);
    private boolean isStartPreview = false;
    private JButton startPreviewButton;
    private ProxyService proxyService;
    private JPanel preview;
    private JLabel bitRate;
    private Canvas canvas;

    public PreviewPanel() {
        super("Android camera preview");
        SettingService.i.addListener(this);
        preview = new JPanel(null);
        canvas = new Canvas();

        preview.setMinimumSize(minSize);
        preview.setPreferredSize(preferredSize);
        preview.setMaximumSize(maxSize);

        canvas.setMinimumSize(minSize);
        canvas.setPreferredSize(preferredSize);
        canvas.setMaximumSize(maxSize);

        preview.add(canvas);
        add(preview, getGbl().setGrid(0, 0).fillB());
        startPreviewButton = new JButton(isStartPreview ? STOP_PREVIEW : START_PREVIEW);
        startPreviewButton.addActionListener(this);

        bitRate = new JLabel("0");
        bitRate.setPreferredSize(new Dimension(30, 15));
        JLabel bitRateLabel = new JLabel("Bit rate (bytes):");

        add(startPreviewButton, getGbl().setGrid(0, 1));
        add(bitRateLabel, getGbl().setGrid(1,0));
        add(bitRate, getGbl().setGrid(2,0));
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
        if (data.cmd != Protocol.Cmd.camPreviewImg() || !preview.isShowing()) return;

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

        bitRate.setText(String.valueOf(data.aSize));

        canvas.setSize(bufImage.getWidth(), bufImage.getHeight());
        canvas.getGraphics().drawImage(bufImage, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Data data = new Data();
        data.cmd = Protocol.Cmd.camPreviewState();
        data.bData = isStartPreview ? (byte)0 : (byte)1;
        proxyService.send(data);
        isStartPreview = !isStartPreview;
        startPreviewButton.setText(isStartPreview ? STOP_PREVIEW : START_PREVIEW);
    }


}
