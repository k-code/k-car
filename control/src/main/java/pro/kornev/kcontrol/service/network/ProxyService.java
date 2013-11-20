package pro.kornev.kcontrol.service.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

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
public final class ProxyService {
    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);
    private static final int MAX_ERRORS = 10;

    private Queue<Data> inputQueue;
    private Queue<Data> outputQueue;
    private Socket client;
    private Set<ProxyServiceListener> listeners;

    public ProxyService(String host, int port) {
        this.inputQueue = new LinkedBlockingQueue<>();
        this.outputQueue = new LinkedBlockingQueue<>();
        this.listeners = new HashSet<>();

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

    public void addListener(ProxyServiceListener listener) {
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
                DataInputStream input = new DataInputStream(client.getInputStream());

                while (!client.isClosed()) {
                    if (errors > MAX_ERRORS) {
                        client.close();
                        break;
                    }
                    if (input.available() == 0) {
                        sleep();
                        continue;
                    }

                    Data data = Protocol.fromInputStream(input);
                    log.debug("Read command: " + data.cmd);

                    inputQueue.add(data);
                }
                log.debug("Read socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Writer implements Runnable {
        private int id = 0;

        @Override
        public void run() {
            try {
                DataOutputStream output = new DataOutputStream(client.getOutputStream());

                while (!client.isClosed()) {
                    Data data = outputQueue.poll();
                    if (data == null) {
                        sleep();
                        continue;
                    }
                    log.debug("Write command: " + data.cmd);
                    data.id = id++;
                    Protocol.toOutputStream(data, output);
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
                    sleep();
                    continue;
                }
                Data data = inputQueue.poll();
                for (ProxyServiceListener listener: listeners) {
                    listener.onPackageReceive(data);
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
