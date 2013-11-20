package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 * @author vkornev
 * @since 17.10.13
 */
public class NetworkService extends Service {
    private static final int PROXY_PORT = 6780;
    private static final int PROXY_RECONNECT_TIMEOUT = 10000;
    private LogsDB db;
    private static List<NetworkListener> listeners = new ArrayList<NetworkListener>();
    private Thread controllerThread;
    private Controller controller;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new LogsDB(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Network service starting", Toast.LENGTH_SHORT).show();
        try {
            if (controller != null) {
                Socket s = controller.getSocket();
                while (s != null && !s.isClosed()) {
                    s.close();
                    sleep(1); // wait while socket will close and threads will stop
                }
                while (controllerThread != null && controllerThread.isAlive()) {
                    controllerThread.interrupt();
                }
            }
            controller = new Controller();
            controllerThread = new Thread(controller);
            controllerThread.start();
        } catch (Exception e) {
            db.putLog("NS start error: " + e.getMessage());
            e.printStackTrace();
        }
        return START_STICKY;
    }

    class Controller implements Runnable {
        private volatile Socket socket;
        @Override
        public void run() {
            Thread readerThread = null;
            Thread writerThread = null;
            while (State.isServiceRunning()) {
                db.putLog("Connect to: " + State.getProxyServer() + ":" + PROXY_PORT);
                try {
                    setSocket(new Socket(State.getProxyServer(), PROXY_PORT));
                } catch (Exception e) {
                    db.putLog("Failed connect to server: " + e.getMessage());
                    /** wait {@link NetworkService#PROXY_RECONNECT_TIMEOUT} seconds and if isServiceRunning then try reconnect */
                    sleep(PROXY_RECONNECT_TIMEOUT);
                    continue;
                }

                try {
                    Writer writer = new Writer(getSocket());
                    writerThread = new Thread(writer);
                    writerThread.start();

                    Reader reader = new Reader(getSocket());
                    readerThread = new Thread(reader);
                    readerThread.start();
                    db.putLog("Connect to " +getSocket().getInetAddress().toString() + " is successful");
                    readerThread.join(); // Work wile reader is working
                    getSocket().close(); // Close socket and try reconnect
                } catch (InterruptedException e) {
                    readerThread.interrupt();
                    writerThread.interrupt();
                    return;
                } catch (Exception e) {
                    db.putLog("NS start error: " + e.getMessage());
                }
                sleep(PROXY_RECONNECT_TIMEOUT);
            }
        }

        public synchronized Socket getSocket() {
            return socket;
        }

        public synchronized void setSocket(Socket socket) {
            this.socket = socket;
        }
    }

    class Reader implements Runnable {
        private volatile Socket client;

        Reader(Socket s) {
            client = s;
        }

        @Override
        public void run() {
            db.putLog("Start network reader");
            try {
                DataInputStream input = new DataInputStream(client.getInputStream());
                while (State.isServiceRunning() && !client.isClosed()) {
                    Data data = Protocol.fromInputStream(input);

                    db.putLog(String.format("NR: id: %d; cmd: %d", data.id, data.cmd));
                    if (data.cmd == Protocol.Cmd.ping() && data.bData == 0) {
                        Data response = new Data();
                        response.id = data.id;
                        response.cmd = data.cmd;
                        response.type = data.type;
                        response.bData = 2;
                        State.getToControlQueue().add(response);
                    }
                    for (NetworkListener l: listeners) {
                        l.onDataReceived(data);
                    }
                }
            } catch (Exception e) {
                db.putLog("NR error: " + e.getMessage());
                e.printStackTrace();
            }
            db.putLog("Stop network reader");
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    class Writer implements Runnable {
        private volatile Socket client;
        private Queue<Data> queue;
        private int id = 0;

        Writer(Socket s) {
            client = s;
            queue = State.getToControlQueue();
        }

        @Override
        public void run() {
            db.putLog("Start network writer");
            try {
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                while (State.isServiceRunning() && !client.isClosed()) {
                    if (queue.isEmpty()) {
                        sleep(1);
                        continue;
                    }
                    Data data = queue.poll();
                    data.id = id++;
                    db.putLog(String.format("NW: id: %d; cmd: %d", data.id, data.cmd));

                    Protocol.toOutputStream(data, output);
                }
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (Exception e) {
                db.putLog("NW error: " + e.getMessage());
            }
            db.putLog("Stop network writer");
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Network service done", Toast.LENGTH_SHORT).show();
        State.setServiceRunning(false);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    public static void addListener(NetworkListener listener) {
        listeners.add(listener);
    }

    public static void removeAllListeners() {
        listeners.clear();
    }
}
