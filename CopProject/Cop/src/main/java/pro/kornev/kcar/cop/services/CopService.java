package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.cop.services.network.NetworkBinder;
import pro.kornev.kcar.cop.services.network.NetworkService;
import pro.kornev.kcar.cop.services.usb.UsbService;
import pro.kornev.kcar.cop.services.video.VideoService;
import pro.kornev.kcar.protocol.Data;

/**
 *
 */
public class CopService extends Service {
    private final Queue<Data> toUsbQueue = new LinkedBlockingQueue<Data>();
    private final IBinder mBinder = new CopBinder();
    private LogsDB log;
    private boolean running = false;
    private VideoService videoService;
    private UsbService usbService;
    private Intent networkServiceIntent;
    private NetworkService networkService;

    @Override
    public void onCreate() {
        super.onCreate();
        log = new LogsDB(this);
        log.putLog("CS Created");
        networkServiceIntent = new Intent(this, NetworkService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        log.putLog("CS Binging...");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.putLog("CS Starting...");
        setRunning(true);
        //startDebugThread();

        videoService = new VideoService(this);
        usbService = new UsbService(this);
        bindService(networkServiceIntent, networkServiceConnection, Context.BIND_AUTO_CREATE);

        usbService.start();
        videoService.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
        log.putLog("CS Destroyed");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        log.putLog("CS Unbound");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stop();
        log.putLog("CS Task removed");
    }

    public class CopBinder extends Binder {
        public CopService getService() {
            return CopService.this;
        }
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void stop() {
        setRunning(false);
        try {
            unbindService(networkServiceConnection);
        } catch (Exception ignored) {}
        videoService.stop();
        stopSelf();
    }
    public Queue<Data> getToUsbQueue() {
        return toUsbQueue;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    @SuppressWarnings("unused")
    private void startDebugThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while(isRunning()) {
                    log.putLog("i = " + i++);
                    Utils.sleep(1000);
                }
            }
        }).start();
    }

    private ServiceConnection networkServiceConnection  = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            networkService = ((NetworkBinder)service).getService();
            if (!networkService.isRunning()) {
                networkService.addListener(videoService);
                networkService.addListener(usbService);
                startService(networkServiceIntent);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
