package pro.kornev.kcontrol.service.network;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcontrol.service.RelationsController;

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

    private String host;
    private int port;
    private Queue<Data> inputQueue;
    private Queue<Data> outputQueue;
    private Gson gson;

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
        this.inputQueue = RelationsController.getInputQueue();
        this.outputQueue = RelationsController.getOutputQueue();
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(host, port);
            log.debug("Connect to host {} on port {}", host, port);
            if (!socket.isBound()) {
                throw new IllegalStateException("Failed connect to host " + host + " on port " + port);
            }
            Reader r = new Reader(socket);
            Writer w = new Writer(socket);
            Thread reader = new Thread(r);
            Thread writer = new Thread(w);
            reader.start();
            writer.start();
            reader.join();
            writer.join();
            // TODO : added ability to stop service
        } catch (Exception e) {
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
