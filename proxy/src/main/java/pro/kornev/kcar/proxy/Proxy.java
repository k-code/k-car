package pro.kornev.kcar.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.kornev.kcar.protocol.Data;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: kvv
 * Date: 17.10.13
 * Time: 20:29
 */
public final class Proxy implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Proxy.class);
    private static NetworkService copService;
    private static NetworkService controlService;

    public static void main(String[] args) {
        log.info("Start Proxy...");
        Runtime.getRuntime().addShutdownHook(new Thread(new Proxy()));

        Queue<Data> fromCopToControlQueue = new LinkedBlockingQueue<Data>();
        Queue<Data> fromControlToCopQueue = new LinkedBlockingQueue<Data>();

        while(true) {
            log.info("Start network services");
            copService = new NetworkService(6780, fromCopToControlQueue, fromControlToCopQueue);
            controlService = new NetworkService(6781, fromControlToCopQueue, fromCopToControlQueue);

            Thread control = new Thread(controlService);
            Thread cop = new Thread(copService);

            cop.start();
            control.start();

            try {
                cop.join();
                control.join();
            } catch (InterruptedException ignored) {
            }
            log.error("Network service unexpectedly stopped");
            Utils.sleep(1000);
        }
    }

    @Override
    public void run() {
        log.info("Shutdown Proxy...");
        copService.shutdown();
        controlService.shutdown();
    }
}
