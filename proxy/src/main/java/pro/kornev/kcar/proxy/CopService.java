package pro.kornev.kcar.proxy;

import com.google.gson.Gson;
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
    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            ServerSocket listener = new ServerSocket(6780);
            while (!listener.isClosed()) {
                Socket client = listener.accept();
                while (!client.isClosed()) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String s = input.readLine();
                    Data data = gson.fromJson(s, Data.class);
                    data.id++;
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    output.write(gson.toJson(data));
                    output.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
