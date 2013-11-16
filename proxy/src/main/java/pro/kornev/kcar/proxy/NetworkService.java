package pro.kornev.kcar.proxy;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.kornev.kcar.protocol.Data;

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
    private static final int MAX_ERRORS = 10;

    private final Logger log;
    private int port;
    private Queue<Data> inputQueue;
    private Queue<Data> outputQueue;
    private Gson gson;
    private Reader reader = null;
    private Writer writer = null;
    private volatile boolean clientAccepted = false;

    public NetworkService(int port, Queue<Data> inputQueue, Queue<Data> outputQueue) {
        this.port = port;
        this.log = LoggerFactory.getLogger("Network service: " + port);
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            log.info("Run listener on port {}", port);
            ServerSocket listener = new ServerSocket(port);
            log.debug("Start cleaner", port);
            new Thread(new Cleaner(listener)).start();
            while (!listener.isClosed()) {
                Socket client = listener.accept();
                log.info("Client accepted");

                log.debug("Shutdown reader and writer");
                if (reader != null) {
                    reader.shutdown();
                }
                if (writer != null) {
                    writer.shutdown();
                }

                log.debug("Start new reader and writer");
                reader = new Reader(client);
                writer = new Writer(client);
                new Thread(reader).start();
                new Thread(writer).start();
                log.debug("Paused cleaner");
                clientAccepted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    if (data.cmd == 1 && data.bData == 0) {
                        Data response = new Data();
                        response.id = data.id;
                        response.cmd = data.cmd;
                        response.type = data.type;
                        response.bData = 1;
                        outputQueue.add(response);
                    }
                }
                log.info("Read socket was closed", port);
                clientAccepted = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void shutdown() {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
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
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                while (!client.isClosed()) {
                    Data data = outputQueue.poll();
                    if (data == null) {
                        sleep();
                        continue;
                    }
                    String s = gson.toJson(data);
                    log.debug("Write line: " + s);
                    output.write(gson.toJson(data));
                    output.newLine();
                    output.flush();
                }
                log.info("Write socket was closed", port);
                clientAccepted = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void shutdown() {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
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
                if (!clientAccepted) {
                    Data data = outputQueue.poll();
                    if (data == null) {
                        sleep();
                    }
                    else {
                        log.debug("Data with id {} removed", data.id);
                    }
                }
                else {
                    sleep();
                }
            }
            log.info("Cleaner was closed", port);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
