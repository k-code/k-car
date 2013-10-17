package pro.kornev.kcar.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: kvv
 * Date: 17.10.13
 * Time: 20:50
 */
public class ControlService implements Runnable {
    @Override
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(6781);
            while (!listener.isClosed()) {
                Socket client = listener.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
