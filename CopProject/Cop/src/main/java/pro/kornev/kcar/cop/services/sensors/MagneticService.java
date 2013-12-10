package pro.kornev.kcar.cop.services.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import pro.kornev.kcar.cop.services.CopService;
import pro.kornev.kcar.cop.services.network.NetworkListener;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 *
 */
public final class MagneticService implements NetworkListener, SensorEventListener {
    private final CopService copService;
    private final SensorManager sensorManager;
    private final Sensor magneticSensor;
    private final float[] magnetics = {0 ,0 ,0};

    public MagneticService(CopService copService) {
        this.copService = copService;
        sensorManager = (SensorManager)copService.getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onDataReceived(Data data) {
        if (data.cmd == Protocol.Cmd.sensMagnetic()) {
            byte[] axis = {(byte) magnetics[0], (byte) magnetics[1], (byte) magnetics[2]};
            data.aSize = axis.length;
            data.aData = axis;
            write(data);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        magnetics[0] = event.values[0];
        magnetics[1] = event.values[1];
        magnetics[2] = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    private void write (Data data) {
        if (copService.getNetworkService() == null) {
            return;
        }
        copService.getNetworkService().write(data);
    }
}
