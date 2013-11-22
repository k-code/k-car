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
    private static volatile UsbSerialDriver usbSerialDriver;
    private static volatile boolean isServiceRunning;
    private static volatile boolean isLogsEnabled;
    private static volatile String proxyServer;
    private static volatile Queue<Data> toControlQueue = new LinkedBlockingQueue<Data>();
    private static volatile Queue<Data> toUsbQueue = new LinkedBlockingQueue<Data>();

    public static synchronized UsbSerialDriver getUsbSerialDriver() {
        return usbSerialDriver;
    }

    public static synchronized void setUsbSerialDriver(UsbSerialDriver usbSerialDriver) {
        State.usbSerialDriver = usbSerialDriver;
    }

    public static synchronized boolean isServiceRunning() {
        return isServiceRunning;
    }

    public static synchronized void setServiceRunning(boolean serviceRunning) {
        isServiceRunning = serviceRunning;
    }

    public static synchronized boolean isLogsEnabled() {
        return isLogsEnabled;
    }

    public static synchronized void setLogsEnabled(boolean logsEnabled) {
        isLogsEnabled = logsEnabled;
    }

    public static synchronized String getProxyServer() {
        return proxyServer;
    }

    public static synchronized void setProxyServer(String proxyServer) {
        State.proxyServer = proxyServer;
    }

    public static synchronized Queue<Data> getToControlQueue() {
        return toControlQueue;
    }

    public static synchronized Queue<Data> getToUsbQueue() {
        return toUsbQueue;
    }
}
