package pro.kornev.kcar.proxy;

import pro.kornev.kcar.protocol.Data;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: kvv
 * Date: 17.10.13
 * Time: 20:29
 */
public final class Proxy {
    public static void main(String[] args) {
        Queue<Data> fromCopToControlQueue = new LinkedBlockingQueue<Data>();
        Queue<Data> fromControlToCopQueue = new LinkedBlockingQueue<Data>();

        NetworkService copService = new NetworkService(6780, fromCopToControlQueue, fromControlToCopQueue);
        NetworkService controlService = new NetworkService(6781, fromControlToCopQueue, fromCopToControlQueue);

        Thread control = new Thread(controlService);
        Thread cop = new Thread(copService);

        cop.start();
        control.start();

        try {
            cop.join();
            control.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
