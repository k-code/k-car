package pro.kornev.kcar.cop.services.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.providers.ConfigDB;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.cop.services.CustomService;
import pro.kornev.kcar.cop.services.support.ProcessKillerException;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 * @author vkornev
 * @since 17.10.13
 */
public final class NetworkService extends Service implements Runnable, CustomService {
    private static final int PROXY_PORT = 6780;
    private static final int PROXY_RECONNECT_TIMEOUT = 10000;
    private static final int PROXY_PING_DELAY = 60;

    private List<NetworkListener> listeners;
    private IBinder binder;
    private LogsDB log;
    private ConfigDB config;
    private ScheduledExecutorService executorService;


    private volatile Socket socket;
    private volatile Writer writer;

    private boolean isRunning = false;

    @Override
    public void onCreate() {
        log = new LogsDB(this);
        config = new ConfigDB(this);
        binder = new NetworkBinder(this);
        listeners = new ArrayList<NetworkListener>();
        listeners.add(this);
        log.putLog("NS Created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        log.putLog("NS onBind");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.putLog("NS onStartCommand");
        start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void run() {
        log.putLog("NS Running");
        while (isRunning()) {
            log.putLog("NS Connect to: " + config.getProxy() + ":" + PROXY_PORT);
            try {
                setSocket(new Socket(config.getProxy(), PROXY_PORT));
            } catch (Exception e) {
                log.putLog("NS Failed connect to server: " + e.getMessage());
                Utils.sleep(PROXY_RECONNECT_TIMEOUT);
                continue;
            }

            try {
                log.putLog("NS Starting reader and writer");
                setWriter(new Writer(this, getSocket()));
                Reader reader = new Reader(this, getSocket(), listeners);
                Thread readerThread = new Thread(reader);
                readerThread.start();
                readerThread.join(); // Work wile reader is working
                closeSocket(getSocket()); // Try to close socket
                log.putLog("NS reader and writer was closed");
            } catch (Exception e) {
                log.putLog("NS run reader and writer was filed: " + e.getMessage());
                Utils.sleep(PROXY_RECONNECT_TIMEOUT);
            }
        }
        log.putLog("NS Stopped");
    }

    @Override
    public void onDataReceived(Data data) {
        if (data.cmd == Protocol.Cmd.ping() && data.bData == 0) {
            log.putLog("NS Send ping response");
            Data response = new Data();
            response.id = data.id;
            response.cmd = data.cmd;
            response.type = data.type;
            response.bData = 2;
            write(response);
        }
    }

    public synchronized boolean stop() {
        try {
            log.putLog("NS Stopping...");
            isRunning = false;
            executorService.shutdown();
            closeSocket(getSocket());
            stopSelf();
        } catch (Exception ignored) {
        }
        return true;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public void addListener(NetworkListener listener) {
        if (listeners.contains(listener)) return;
        log.putLog("NS Add listener " + listener.getClass().getSimpleName());
        listeners.add(listener);
    }

    public void removeListener(NetworkListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
            log.putLog("NS remove listener " + listener.getClass().getSimpleName());
        }
    }

    public void write(Data data) {
        getWriter().write(data);
    }

    public static void closeSocket(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {
        }
    }

    public synchronized boolean start() {
        try {
            log.putLog("NS Starting...");
            Thread thread = new Thread(this);
            thread.setUncaughtExceptionHandler(new ProcessKillerException());
            thread.start();
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleWithFixedDelay(new PingTask(this), PROXY_PING_DELAY, PROXY_PING_DELAY, TimeUnit.SECONDS);
            isRunning = true;
        } catch (Exception e) {
            log.putLog("NS Filed start: " + e.getMessage());
            return false;
        }
        return true;
    }

    private synchronized Socket getSocket() {
        return socket;
    }

    private synchronized void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.socket.setKeepAlive(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized Writer getWriter() {
        return writer;
    }

    private synchronized void setWriter(Writer writer) {
        this.writer = writer;
    }
}
