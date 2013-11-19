package pro.kornev.kcar.proxy;

import pro.kornev.kcar.protocol.Data;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: kvv
 * Date: 17.10.13
 * Time: 20:29
 */
public final class Proxy implements Runnable {
    private static NetworkService copService;
    private static NetworkService controlService;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Proxy()));

        Queue<Data> fromCopToControlQueue = new LinkedBlockingQueue<Data>();
        Queue<Data> fromControlToCopQueue = new LinkedBlockingQueue<Data>();

        copService = new NetworkService(6780, fromCopToControlQueue, fromControlToCopQueue);
        controlService = new NetworkService(6781, fromControlToCopQueue, fromCopToControlQueue);

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

    @Override
    public void run() {
        copService.shutdown();
        controlService.shutdown();
    }
}
