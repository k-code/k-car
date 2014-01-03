package pro.kornev.kcar.cop.services.support;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;

import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.cop.services.CopService;

/**
 *
 */
public final class WakeUpService extends Service implements Runnable {
    private static final int CHECK_SERVICE_STATE_INTERVAL = 60000;
    private boolean running = false;
    private Intent copServiceIntent;
    private IWakeUpBinder copService;
    private LogsDB log;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            log.putLog("WS Bound to COP service");
            try {
                copService = IWakeUpBinder.Stub.asInterface(service);
                if (!copService.isRunning()) {
                    startService(copServiceIntent);
                }
                copService.setCallback(wakeUpCallback);
            } catch (Exception e) {
                log.putLog("WS Filed connect to COP service: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log.putLog("WS Unbound from COP service");
            copService = null;
        }
    };

    private final IWakeUpCallback.Stub wakeUpCallback = new IWakeUpCallback.Stub() {
        @Override
        public void stop() throws RemoteException {
            log.putLog("WS Stopping..");
            setRunning(false);
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new ProcessKillerException());
        log = new LogsDB(this);
        copServiceIntent = new Intent(CopService.class.getName());
        copServiceIntent.putExtra(CopService.EXTRA_BINDER, IWakeUpBinder.class.getName());
        log.putLog("WS Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.putLog("WS Starting");
        if (isRunning()) {
            log.putLog("WS Already started");
            stopSelf();
            return START_NOT_STICKY;
        }
        setRunning(true);
        new Thread(this).start();
        log.putLog("WS Started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.putLog("WS Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        log.putLog("WS Task removed");
    }

    @Override
    public void run() {
            while (isRunning()) {
                try {
                    log.putLog("WS Check COP service state");
                    if (copService != null) {
                        if (!copService.isRunning()) {
                            log.putLog("WS Starting COP service...");
                            startService(copServiceIntent);
                        }
                    }
                    else {
                        log.putLog("WS Binding to COP service...");
                        bindService(copServiceIntent, connection, BIND_AUTO_CREATE);
                    }
                } catch (Exception e) {
                    log.putLog("WS Binding error: " + e.getMessage());
                    copService = null;
                }
                Utils.sleep(CHECK_SERVICE_STATE_INTERVAL);
            }
    }

    private synchronized boolean isRunning() {
        return running;
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }
}
