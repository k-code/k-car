package pro.kornev.kcar.cop.services.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.atomic.AtomicInteger;

import pro.kornev.kcar.cop.services.CopService;
import pro.kornev.kcar.cop.services.CustomService;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 *
 */
public final class LightService implements SensorEventListener, CustomService {
    private final CopService copService;
    private final SensorManager sensorManager;
    private final Sensor lightSensor;
    private final AtomicInteger light = new AtomicInteger(0);
    private volatile boolean running = false;

    public LightService(CopService copService) {
        this.copService = copService;
        sensorManager = (SensorManager) copService.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onDataReceived(Data data) {
        if (data.cmd == Protocol.Cmd.sensLight() && data.bData == Protocol.Req.get()) {
            data.iData = light.get();
            write(data);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        light.set(Math.round(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean start() {
        if (running) return false;
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        running = true;
        return true;
    }

    public boolean stop() {
        if (!running) return false;
        sensorManager.unregisterListener(this);
        running = false;
        return true;
    }

    private void write(Data data) {
        if (copService.getNetworkService() == null) {
            return;
        }
        copService.getNetworkService().write(data);
    }
}
