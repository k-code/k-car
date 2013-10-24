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
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);
    private static final int MAX_ERRORS = 10;

    private int port;
    private Queue<Data> inputQueue;
    private Queue<Data> outputQueue;
    private Gson gson;

    public NetworkService(int port, Queue<Data> inputQueue, Queue<Data> outputQueue) {
        this.port = port;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(port);
            log.debug("Run listener on port {}", port);
            while (!listener.isClosed()) {
                Socket client = listener.accept();
                log.info("Accept client");
                Reader r = new Reader(client);
                Writer w = new Writer(client);
                new Thread(r).start();
                new Thread(w).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Reader implements Runnable {
        private Socket client;

        Reader(Socket client) {
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
                }
                log.debug("Read socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Writer implements Runnable {
        private Socket client;

        Writer(Socket client) {
            this.client = client;
        }

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
}
