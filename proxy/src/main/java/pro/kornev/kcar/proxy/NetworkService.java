package pro.kornev.kcar.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

/**
 * User: kvv
 * Date: 17.10.13
 * Time: 20:32
 */
public final class NetworkService implements Runnable {
    private final Logger log;
    private int port;
    private volatile Queue<Data> inputQueue;
    private volatile Queue<Data> outputQueue;
    private volatile Reader reader = null;
    private volatile Writer writer = null;
    private volatile Cleaner cleaner = null;
    private boolean clientAccepted = false;

    public NetworkService(int port, Queue<Data> inputQueue, Queue<Data> outputQueue) {
        this.port = port;
        this.log = LoggerFactory.getLogger("Network service: " + port);
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
        try {
            log.info("Run listener on port {}", port);
            ServerSocket listener = new ServerSocket(port);
            log.debug("Start cleaner", port);
            cleaner = new Cleaner(listener);
            new Thread(cleaner).start();
            while (!listener.isClosed()) {
                Socket client;
                try {
                    client = listener.accept();
                } catch (IOException e) {
                    log.error("Failed accept client.", e);
                    continue;
                }
                log.info("Client accepted");

                if (reader != null) {
                    log.debug("Shutdown reader");
                    reader.shutdown();
                }
                if (writer != null) {
                    log.debug("Shutdown writer");
                    writer.shutdown();
                }

                log.debug("Start new reader and writer");
                reader = new Reader(client);
                writer = new Writer(client);
                new Thread(reader).start();
                new Thread(writer).start();
                log.debug("Paused cleaner");
                setClientAccepted(true);
            }
            log.info("Listener on port {} is closed", port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        log.info("Shutdown server on port" + port);
        if (reader != null) reader.shutdown();
        if (writer != null) writer.shutdown();
        if (cleaner != null) cleaner.shutdown();
    }

    class Reader implements Runnable {
        private final Logger log;
        private Socket client;

        Reader(Socket client) {
            this.log = LoggerFactory.getLogger("Reader: " + port);
            this.client = client;
        }

        @Override
        public void run() {
            try {
                DataInputStream input = new DataInputStream(client.getInputStream());

                while (!client.isClosed()) {
                    Data data = Protocol.fromInputStream(input);
                    log.debug("Read command: " + data.cmd);
                    inputQueue.add(data);

                    if (data.cmd == Protocol.Cmd.ping() && data.bData == 0) {
                        Data response = new Data();
                        response.id = data.id;
                        response.cmd = data.cmd;
                        response.type = data.type;
                        response.bData = 1;
                        outputQueue.add(response);
                    }
                }
            } catch (IOException e) {
                log.error("Reader exception {}", e.getMessage());
            } finally {
                shutdown();
                log.info("Read socket was closed", port);
            }
        }

        public void shutdown() {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                setClientAccepted(false);
            }
        }
    }

    class Writer implements Runnable {
        private final Logger log;
        private Socket client;

        Writer(Socket client) {
            this.log = LoggerFactory.getLogger("Writer: " + port);
            this.client = client;
        }

        @Override
        public void run() {
            try {
                DataOutputStream output = new DataOutputStream(client.getOutputStream());

                while (!client.isClosed()) {
                    Data data = outputQueue.poll();
                    if (data == null) {
                        Utils.sleep(1);
                        continue;
                    }

                    log.debug("Write command: " + data.cmd);
                    Protocol.toOutputStream(data, output);
                }
            } catch (IOException e) {
                log.error("Writer exception", e);
            } finally {
                shutdown();
                log.info("Write socket was closed", port);
            }
        }

        public void shutdown() {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                setClientAccepted(false);
            }
        }
    }

    class Cleaner implements Runnable {
        private final Logger log;
        private ServerSocket listener;

        Cleaner(ServerSocket listener) {
            this.log = LoggerFactory.getLogger("Cleaner: " + port);
            this.listener = listener;
        }

        @Override
        public void run() {
            while (!listener.isClosed()) {
                if (!isClientAccepted()) {
                    Data data = outputQueue.poll();
                    if (data == null) {
                        Utils.sleep(1);
                    }
                    else {
                        log.debug("Drop data with id {}", data.id);
                    }
                }
                else {
                    Utils.sleep(100);
                }
            }
            log.info("Cleaner was closed", port);
        }

        public void shutdown() {
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized boolean isClientAccepted() {
        return clientAccepted;
    }

    synchronized void setClientAccepted(boolean clientAccepted) {
        this.clientAccepted = clientAccepted;
    }
}
