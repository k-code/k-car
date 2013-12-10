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
public final class OrientationService implements NetworkListener, SensorEventListener {
    private static final float ALPHA = 0.75f;
    private final CopService copService;
    private final SensorManager sensorManager;
    private final Sensor magneticSensor;
    private final Sensor accelerometer;
    private final float[] gravity = {0 ,0 ,0};
    private final float[] acceleration = {0 ,0 ,0};
    private final float[] magnetic = {0 ,0 ,0};
    private final float[] R = new float[9];
    private final float[] I = new float[9];

    public OrientationService(CopService copService) {
        this.copService = copService;
        sensorManager = (SensorManager)copService.getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onDataReceived(Data data) {
        if (data.cmd == Protocol.Cmd.sensMagnetic()) {
            data.bData = (byte)SensorManager.getInclination(I);
            write(data);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetic[0] = event.values[0];
                magnetic[1] = event.values[1];
                magnetic[2] = event.values[2];
                break;
            case Sensor.TYPE_ACCELEROMETER:
                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
                gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
                gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

                acceleration[0] = event.values[0];
                acceleration[1] = event.values[1];
                acceleration[2] = event.values[2];
                break;
        }
        SensorManager.getRotationMatrix(R, I, acceleration, magnetic);
        Log.w("INC", String.format("%f", SensorManager.getInclination(I)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this, magneticSensor);
        sensorManager.unregisterListener(this, accelerometer);
    }

    private void write (Data data) {
        if (copService.getNetworkService() == null) {
            return;
        }
        copService.getNetworkService().write(data);
    }
}
