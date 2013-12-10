package pro.kornev.kcar.cop.services.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import pro.kornev.kcar.cop.services.CopService;
import pro.kornev.kcar.cop.services.network.NetworkListener;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 *
 */
public final class AccelerationService implements NetworkListener, SensorEventListener {
    private final CopService copService;
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final float[] gravity = {0 ,0 ,0};

    public AccelerationService(CopService copService) {
        this.copService = copService;
        sensorManager = (SensorManager)copService.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onDataReceived(Data data) {
        if (data.cmd == Protocol.Cmd.sensAxis()) {
            byte[] axis = {(byte)gravity[0], (byte)gravity[1], (byte)gravity[2]};
            data.aSize = 3;
            data.aData = axis;
            write(data);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.75f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        /*linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
