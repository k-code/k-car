package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.cop.services.network.NetworkBinder;
import pro.kornev.kcar.cop.services.network.NetworkListener;
import pro.kornev.kcar.cop.services.network.NetworkService;
import pro.kornev.kcar.cop.services.sensors.LightService;
import pro.kornev.kcar.cop.services.sensors.LocationService;
import pro.kornev.kcar.cop.services.sensors.OrientationService;
import pro.kornev.kcar.cop.services.support.IWakeUpBinder;
import pro.kornev.kcar.cop.services.support.IWakeUpCallback;
import pro.kornev.kcar.cop.services.support.ProcessKillerException;
import pro.kornev.kcar.cop.services.support.UpdateService;
import pro.kornev.kcar.cop.services.usb.UsbService;
import pro.kornev.kcar.cop.services.video.VideoService;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 *
 */
public final class CopService extends Service implements NetworkListener {
    public static final String EXTRA_BINDER = "BINDER";

    private final IBinder mBinder = new CopBinder();

    private LogsDB log;
    private boolean running = false;
    private VideoService videoService;
    private UsbService usbService;
    private Intent networkServiceIntent;
    private NetworkService networkService;
    private IWakeUpCallback wakeUpCallback;
    private LightService lightService;
    private OrientationService orientationService;
    private LocationService locationService;
    private UpdateService updateService;

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new ProcessKillerException());
        log = new LogsDB(this);
        log.putLog("CS Created");
        networkServiceIntent = new Intent(this, NetworkService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        log.putLog("CS Binding...");
        if (IWakeUpBinder.class.getName().equals(intent.getStringExtra(EXTRA_BINDER))) {
            return wakeUpBinder;
        }
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.putLog("CS Starting...");
        if (isRunning()) {
            log.putLog("Already running");
            stopSelf();
            return START_NOT_STICKY;
        }
        setRunning(true);
        //startDebugThread();

        videoService = new VideoService(this);
        usbService = new UsbService(this);
        lightService = new LightService(this);
        orientationService = new OrientationService(this);
        locationService = new LocationService(this);
        updateService = new UpdateService();
        bindService(networkServiceIntent, networkServiceConnection, Context.BIND_AUTO_CREATE);

        usbService.start();
        updateService.start();

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

    @Override
    public void onDataReceived(Data data) {
        if (data.type != Protocol.byteType() ||
                (data.bData != Protocol.Req.off() && data.bData != Protocol.Req.on()))
            return;

        CustomService service = null;
        if (data.cmd == Protocol.Cmd.sensOrient()) {
            service = orientationService;
        } else if (data.cmd == Protocol.Cmd.sensLocation()) {
            service = locationService;
        } else if (data.cmd == Protocol.Cmd.sensLight()) {
            service = lightService;
        } else if (data.cmd == Protocol.Cmd.camState()) {
            service = videoService;
        }
        if (service == null) return;
        Message mes = new Message();
        mes.obj = service;
        mes.arg1 = data.bData;
        customServiceHandler.sendMessage(mes);
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
            wakeUpCallback.stop();
        } catch (Exception ignored) {}
        networkService.stop();
        videoService.stop();
        usbService.stop();
        lightService.stop();
        orientationService.stop();
        locationService.stop();
        updateService.stop();
        stopSelf();
    }

    public void restartUsbService() {
        if (usbService != null) {
            usbService.stop();
            usbService.start();
        }
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
                    log.putLog("CS i = " + i++);
                    if (i==10) {
                        i = i/0;
                    }
                    Utils.sleep(1000);
                }
            }
        }).start();
    }

    private IWakeUpBinder.Stub wakeUpBinder = new IWakeUpBinder.Stub() {

        @Override
        public boolean isRunning() throws RemoteException {
            return CopService.this.isRunning();
        }

        @Override
        public void setCallback(IWakeUpCallback callback) throws RemoteException {
            wakeUpCallback = callback;
        }
    };

    private ServiceConnection networkServiceConnection  = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            networkService = ((NetworkBinder)service).getService();
            networkService.addListener(CopService.this);
            networkService.addListener(usbService);
            if (!networkService.isRunning()) {
                startService(networkServiceIntent);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private Handler customServiceHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj == null) return false;
            CustomService service = (CustomService) msg.obj;
            if (msg.arg1 == Protocol.Req.off()) {
                service.stop();
                if (networkService != null) {
                    networkService.removeListener(service);
                }
            } else if (msg.arg1 == Protocol.Req.on()) {
                service.start();
                networkService.addListener(service);
            }
            return false;
        }
    });
}
