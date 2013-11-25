package pro.kornev.kcar.cop;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import pro.kornev.kcar.protocol.Data;

/**
 *
 * @author vkornev
 * @since 14.10.13
 */
public class State {
    private static volatile Queue<Data> toControlQueue = new LinkedBlockingQueue<Data>();
    private static volatile Queue<Data> toUsbQueue = new LinkedBlockingQueue<Data>();

    public static synchronized Queue<Data> getToControlQueue() {
        return toControlQueue;
    }

    public static synchronized Queue<Data> getToUsbQueue() {
        return toUsbQueue;
    }
}
