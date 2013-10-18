package pro.kornev.kcar.proxy;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.kornev.kcar.proxy.model.Data;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: kvv
 * Date: 17.10.13
 * Time: 20:32
 */
public class CopService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(CopService.class);

    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            ServerSocket listener = new ServerSocket(6780);
            log.debug("Run listener");
            while (!listener.isClosed()) {
                Socket client = listener.accept();
                log.info("Accept client");
                while (!client.isClosed()) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String s = input.readLine();
                    log.debug("Read line: " + s);
                    Data data = gson.fromJson(s, Data.class);
                    if (data == null) continue;
                    data.id++;
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    output.write(gson.toJson(data));
                    output.newLine();
                    output.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
