package pro.kornev.kcar.cop.services.support;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.services.CopService;

/**
 *
 */
public class WakeUpService extends Service implements Runnable {
    private static final String TAG = "WakeUpService";
    private boolean running = false;
    private Intent copServiceIntent;
    private IWakeUpBinder copService;

    private final IWakeUpBinder.Stub binder = new IWakeUpBinder.Stub() {
        @Override
        public boolean isRunning() throws RemoteException {
            return WakeUpService.this.isRunning();
        }

        @Override
        public void stop() throws RemoteException {
            setRunning(false);
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAG, "Bound to COP service");
            copService = IWakeUpBinder.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "Unbound from COP service");
            copService = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        copServiceIntent = new Intent("pro.kornev.kcar.cop.services.support.WakeUpService");
        copServiceIntent.putExtra("type", IWakeUpBinder.class.getName());
        copServiceIntent.setAction("a");
        Log.w(TAG, "Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "Starting");
        if (isRunning()) {
            Log.w(TAG, "Already started");
            stopSelf();
            return START_NOT_STICKY;
        }
        setRunning(true);
        new Thread(this).start();
        Log.w(TAG, "Started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "On bind: " + intent.getComponent());
        return binder;
    }

    @Override
    public void run() {
        try {
            while (isRunning()) {
                if (copService != null) {
                    if (!copService.isRunning()) {
                        Log.w(TAG, "Starting COP service...");
                        startService(copServiceIntent);
                    }
                }
                else {
                    Log.w(TAG, "Binding to COP service...");
                    bindService(copServiceIntent, connection, BIND_AUTO_CREATE);
                }
                Utils.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean isRunning() {
        return running;
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }
}
