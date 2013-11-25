package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import pro.kornev.kcar.cop.providers.LogsDB;

/**
 *
 */
public class CopService extends Service {
    private LogsDB log;
    private final IBinder mBinder = new CopBinder();
    private boolean running = false;
    private VideoService videoService;

    @Override
    public void onCreate() {
        super.onCreate();
        log = new LogsDB(this);
        log.putLog("CS Created");
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

        NetworkService networkService = new NetworkService(this);
        videoService = new VideoService(this);
        UsbService usbService = new UsbService(this);

        networkService.addListener(videoService);
        networkService.addListener(usbService);

        usbService.start();
        videoService.start();
        networkService.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setRunning(false);
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
        setRunning(false);
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
        videoService.stop();
        stopSelf();
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
                    try {
                        log.putLog("i = " + i++);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
