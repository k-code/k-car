package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Queue;

import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.protocol.Data;

/**
 * Created by kvv on 21.10.13.
 *
 */
public class DistributionService extends Service {
    private Queue<Data> fromControl;
    private Queue<Data> toControl;
    private Queue<Data> fromUsb;
    private Queue<Data> toUsb;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fromControl = State.getFromControlQueue();
        toControl = State.getToControlQueue();
        fromUsb = State.getFromUsbQueue();
        toUsb = State.getToUsbQueue();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        new Thread(new Worker()).start();
        return result;
    }

    class Worker implements Runnable {
        @Override
        public void run() {
            while (State.isServiceRunning()) {
                if (!fromControl.isEmpty()) {
                    toUsb.add(fromControl.poll());
                }
                if (!fromUsb.isEmpty()) {
                    toControl.add(fromUsb.poll());
                }
            }
        }
    }
}
