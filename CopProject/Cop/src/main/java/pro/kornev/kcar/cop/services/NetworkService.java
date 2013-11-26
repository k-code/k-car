package pro.kornev.kcar.cop.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.providers.ConfigDB;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 * @author vkornev
 * @since 17.10.13
 */
public class NetworkService implements Runnable {
    private static final int PROXY_PORT = 6780;
    private static final int PROXY_RECONNECT_TIMEOUT = 10000;
    private LogsDB log;
    private ConfigDB config;
    private List<NetworkListener> listeners = new ArrayList<NetworkListener>();
    private boolean writerRunning = false;
    private CopService copService;
    private volatile Socket socket;

    public NetworkService(CopService cs) {
        this.copService = cs;
        log = new LogsDB(copService);
        log.putLog("NS Created");
        config = new ConfigDB(copService);
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        log.putLog("NS Running");
        Cleaner cleaner = null;
        while (copService.isRunning()) {
            log.putLog("NS Starting cleaner");
            if (cleaner == null) {
                cleaner = new Cleaner();
                new Thread(cleaner).start();
            }
            log.putLog("NS Connect to: " + config.getProxy() + ":" + PROXY_PORT);
            try {
                socket = new Socket(config.getProxy(), PROXY_PORT);
            } catch (Exception e) {
                log.putLog("NS Failed connect to server: " + e.getMessage());
                /** wait {@link NetworkService#PROXY_RECONNECT_TIMEOUT} seconds and if isServiceRunning then try reconnect */
                Utils.sleep(PROXY_RECONNECT_TIMEOUT);
                continue;
            }

            try {
                log.putLog("NS Stopping cleaner");
                cleaner.stop();
                cleaner = null;

                log.putLog("NS Starting reader and writer");
                setWriterRunning(true);
                Writer writer = new Writer(socket);
                Thread writerThread = new Thread(writer);
                writerThread.start();
                Reader reader = new Reader(socket);
                Thread readerThread = new Thread(reader);
                readerThread.start();

                readerThread.join(); // Work wile reader is working
                closeSocket(socket); // Close socket and white while writer is closed
                writerThread.join(); // Wait while writer was stopped
                log.putLog("NS reader and writer was closed");
            } catch (Exception e) {
                log.putLog("NS run reader and writer was filed: " + e.getMessage());
            }
        }
    }

    public void addListener(NetworkListener listener) {
        listeners.add(listener);
    }

    public void stop() {
        closeSocket(socket);
    }

    class Reader implements Runnable {
        private volatile Socket client;

        Reader(Socket s) {
            client = s;
        }

        @Override
        public void run() {
            log.putLog("NR Start network reader");
            try {
                DataInputStream input = new DataInputStream(client.getInputStream());
                while (copService.isRunning()) {
                    Data data = Protocol.fromInputStream(input);

                    log.putLog(String.format("NR got data id: %d; cmd: %d", data.id, data.cmd));
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
                log.putLog("NR error: " + e.getMessage());
                e.printStackTrace();
            }
            setWriterRunning(false);
            log.putLog("NR Stop network reader");
            closeSocket(client);
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
            log.putLog("NW Start network writer");
            try {
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                while (isWriterRunning()) {
                    if (queue.isEmpty()) {
                        Utils.sleep(1);
                        continue;
                    }
                    Data data = queue.poll();
                    data.id = id++;
                    log.putLog(String.format("NW wrote date id: %d; cmd: %d", data.id, data.cmd));

                    Protocol.toOutputStream(data, output);
                }
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (Exception e) {
                log.putLog("NW error: " + e.getMessage());
            }
            log.putLog("NW Stop network writer");
            closeSocket(client);
        }
    }

    class Cleaner implements Runnable {
        private boolean isWork = true;

        @Override
        public void run() {
            Queue<Data> queue = State.getToControlQueue();
            while (copService.isRunning() && isWork()) {
                log.putLog("NC Clear queue");
                queue.clear();
                Utils.sleep(1000);
            }
            log.putLog("NC Was closed");
        }

        private synchronized boolean isWork() {
            return isWork;
        }

        public synchronized void stop() {
            isWork = false;
        }
    }

    private synchronized boolean isWriterRunning() {
        return writerRunning;
    }

    private void setWriterRunning(boolean writerRunning) {
        this.writerRunning = writerRunning;
    }

    private synchronized void closeSocket(Socket socket) {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }
}
