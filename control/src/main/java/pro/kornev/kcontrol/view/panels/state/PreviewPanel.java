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
        setMinimumSize(new Dimension(20, 20));
        label = new JLabel("qwew");
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

    static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {

        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }
}
