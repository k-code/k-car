package pro.kornev.kcontrol.service.network;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.kornev.kcar.protocol.Data;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: kvv
 * Date: 17.10.13
 * Time: 20:32
 */
public final class NetworkService {
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);
    private static final int MAX_ERRORS = 10;

    private Queue<Data> inputQueue;
    private Queue<Data> outputQueue;
    private Gson gson;
    private Socket client;
    private Set<NetworkServiceListener> listeners;

    public NetworkService(String host, int port) {
        this.inputQueue = new LinkedBlockingQueue<>();
        this.outputQueue = new LinkedBlockingQueue<>();
        this.listeners = new HashSet<>();

        gson = new Gson();
        client = null;

        log.debug("Connect to host {} on port {}", host, port);
        try {
            client = new Socket(host, port);
        } catch (IOException e) {
            throw new IllegalStateException("Failed connect to host " + host + " on port " + port);
        }
        if (!client.isBound()) {
            throw new IllegalStateException("Failed connect to host " + host + " on port " + port + " Cannot bound socket");
        }

        new Thread(new Reader()).start();
        new Thread(new Writer()).start();
        new Thread(new ListenersProcessor()).start();
    }

    public void shutdown() {
        try {
            client.shutdownInput();
            client.shutdownOutput();
            client.close();
        } catch (IOException e) {
            log.warn("Close network socket is throw exception", e);
        }
    }

    public void addListener(NetworkServiceListener listener) {
        this.listeners.add(listener);
    }

    public void send(Data data) {
        outputQueue.add(data);
    }

    private class Reader implements Runnable {

        @Override
        public void run() {
            int errors = 0;
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while (!client.isClosed()) {
                    if (errors > MAX_ERRORS) {
                        client.close();
                        break;
                    }
                    String s = input.readLine();
                    if (s == null) {
                        errors++;
                        continue;
                    }
                    log.debug("Read line: " + s);

                    Data data = gson.fromJson(s, Data.class);
                    if (data == null) {
                        errors++;
                        continue;
                    }

                    inputQueue.add(data);
                }
                log.debug("Read socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Writer implements Runnable {

        @Override
        public void run() {
            try {
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                while (!client.isClosed()) {
                    Data data = outputQueue.poll();
                    if (data == null) continue;
                    String s = gson.toJson(data);
                    log.debug("Write line: " + s);
                    output.write(gson.toJson(data));
                    output.newLine();
                    output.flush();
                }
                log.debug("Write socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ListenersProcessor implements Runnable {

        @Override
        public void run() {
            while (!client.isClosed()) {
                if (inputQueue.isEmpty()) {
                    continue;
                }
                Data data = inputQueue.poll();
                for (NetworkServiceListener listener: listeners) {
                    listener.onPackageReceive(data);
                }
            }
        }
    }
}
