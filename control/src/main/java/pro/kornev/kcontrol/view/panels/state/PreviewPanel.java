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
    private static final Dimension maxSize = new Dimension(640, 480);
    private boolean isStartPreview = false;
    private JButton startPreviewButton;
    private ProxyService proxyService;
    private JPanel preview;
    private JLabel bitRate;
    private JLabel fpsLabel;
    private Canvas canvas;
    private long lastSec = 0;
    private int fps = 0;

    public PreviewPanel() {
        super("Android camera preview");
        SettingService.i.addListener(this);
        preview = new JPanel(new FlowLayout(FlowLayout.LEFT));
        canvas = new Canvas();

        preview.add(canvas);
        add(preview, getGbl().setGrid(0, 0).fillB().rowSpan(3).weightH(1));
        startPreviewButton = new JButton(isStartPreview ? STOP_PREVIEW : START_PREVIEW);
        startPreviewButton.addActionListener(this);

        bitRate = new JLabel("0");
        bitRate.setPreferredSize(new Dimension(30, 15));
        bitRate.setMinimumSize(bitRate.getPreferredSize());
        JLabel bitRateLabel = new JLabel("Bit rate (bytes):");

        fpsLabel = new JLabel("0");
        add(new JLabel("FPS:"), getGbl().setGrid(1, 1));
        add(fpsLabel, getGbl().setGrid(2,1));

        add(startPreviewButton, getGbl().setGrid(1, 2).anchorB());
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
        if (data.cmd != Protocol.Cmd.camImg() || !preview.isShowing()) return;

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

        Dimension size = new Dimension(bufImage.getWidth(), bufImage.getHeight());
        if (size.height > maxSize.height) {
            size = maxSize;
        }
        if (preview.getSize() != size) {
            preview.setSize(size);
        }
        if (canvas.getSize() != size) {
            canvas.setSize(size);
        }
        size = canvas.getSize();
        canvas.getGraphics().drawImage(bufImage, 0, 0, size.width, size.height, null);

        if (lastSec < System.currentTimeMillis() - 1000) {
            lastSec = System.currentTimeMillis();
            fpsLabel.setText(String.valueOf(fps));
            fps = 0;
        }
        fps++;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (proxyService == null) return;
        Data data = new Data();
        data.cmd = Protocol.Cmd.camState();
        data.bData = isStartPreview ? (byte)0 : (byte)1;
        proxyService.send(data);
        isStartPreview = !isStartPreview;
        startPreviewButton.setText(isStartPreview ? STOP_PREVIEW : START_PREVIEW);
    }


}
