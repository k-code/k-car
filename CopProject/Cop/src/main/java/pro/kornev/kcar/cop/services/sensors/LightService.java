package pro.kornev.kcar.cop.services.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import pro.kornev.kcar.cop.services.CopService;
import pro.kornev.kcar.cop.services.network.NetworkListener;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 *
 */
public final class LightService implements NetworkListener, SensorEventListener {
    private final CopService copService;
    private final SensorManager sensorManager;
    private final Sensor lightSensor;
    private final AtomicInteger light = new AtomicInteger(0);

    public LightService(CopService copService) {
        this.copService = copService;
        sensorManager = (SensorManager) copService.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onDataReceived(Data data) {
        if (data.cmd == Protocol.Cmd.sensLight()) {
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

    public void start() {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    private void write(Data data) {
        if (copService.getNetworkService() == null) {
            return;
        }
        copService.getNetworkService().write(data);
    }
}
