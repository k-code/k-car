package pro.kornev.kcontrol.view.panels.state;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;
import pro.kornev.kcontrol.service.SettingService;
import pro.kornev.kcontrol.service.joystick.KJoystick;
import pro.kornev.kcontrol.service.network.ProxyService;
import pro.kornev.kcontrol.service.network.ProxyServiceListener;
import pro.kornev.kcontrol.view.panels.CustomPanel;
import pro.kornev.kcontrol.service.SettingsListener;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;

public class DistanceViewPanel extends CustomPanel implements ProxyServiceListener, SettingsListener {
	private static final long serialVersionUID = -3113982496558550127L;

    private JLabel distanceValue;
    private ProxyService proxyService;

	public DistanceViewPanel() {
        super("Distance");
		initLabels();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new DistanceRequest(), 0, 10, TimeUnit.SECONDS);
	}

	private void initLabels() {
        JLabel distanceLabel = new JLabel("Distance to barrier:");
		this.add(distanceLabel, getGbl().setGrid(0, 0));
		distanceValue = new JLabel("0");
        distanceValue.setPreferredSize(new Dimension(30, 15));
		this.add(distanceValue, getGbl().setGrid(1, 0));
        SettingService.i.addListener(this);
	}

    @Override
    public void changeJoystick(KJoystick joystick) {
    }

    @Override
    public void changeProxy(ProxyService ns) {
        proxyService = ns;
        proxyService.addListener(this);
    }

    @Override
    public void onPackageReceive(Data data) {
        if (data.cmd != Protocol.Cmd.autoUsRes()) {
            return;
        }
        distanceValue.setText(String.valueOf(data.iData));
    }

    private class DistanceRequest implements Runnable {

        @Override
        public void run() {
            if (proxyService == null) {
                return;
            }
            Data data = new Data();
            data.cmd = Protocol.Cmd.autoUsReq();
            proxyService.send(data);
        }
    }
}
