package pro.kornev.kcar.cop;

import android.hardware.usb.UsbDevice;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import pro.kornev.kcar.protocol.Data;

/**
 *
 * @author vkornev
 * @since 14.10.13
 */
public class State {
    private static UsbDeviceEntry usbDeviceEntry;
    private static boolean isServiceRunning;
    private static boolean isLogsEnabled;
    private static String proxyServer;
    private static Queue<Data> toControlQueue = new LinkedBlockingQueue<Data>();
    private static Queue<Data> fromControlQueue = new LinkedBlockingQueue<Data>();
    private static Queue<Data> toUsbQueue = new LinkedBlockingQueue<Data>();
    private static Queue<Data> fromUsbQueue = new LinkedBlockingQueue<Data>();

    public static UsbDeviceEntry getUsbDeviceEntry() {
        return usbDeviceEntry;
    }

    public static void setUsbDeviceEntry(UsbDeviceEntry usbDeviceEntry) {
        State.usbDeviceEntry = usbDeviceEntry;
    }

    public static boolean isServiceRunning() {
        return isServiceRunning;
    }

    public static void setServiceRunning(boolean serviceRunning) {
        isServiceRunning = serviceRunning;
    }

    public static boolean isLogsEnabled() {
        return isLogsEnabled;
    }

    public static void setLogsEnabled(boolean logsEnabled) {
        isLogsEnabled = logsEnabled;
    }

    public static String getProxyServer() {
        return proxyServer;
    }

    public static void setProxyServer(String proxyServer) {
        State.proxyServer = proxyServer;
    }

    public static Queue<Data> getToControlQueue() {
        return toControlQueue;
    }

    public static Queue<Data> getFromControlQueue() {
        return fromControlQueue;
    }

    public static Queue<Data> getToUsbQueue() {
        return toUsbQueue;
    }

    public static Queue<Data> getFromUsbQueue() {
        return fromUsbQueue;
    }
}
