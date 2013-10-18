package pro.kornev.kcar.cop;

import android.hardware.usb.UsbDevice;

/**
 *
 * @author vkornev
 * @since 14.10.13
 */
public class State {
    private static UsbDevice usbDevice;
    private static boolean isServiceRunning;
    private static boolean isLogsEnabled;
    private static String proxyServer;

    public static UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public static void setUsbDevice(UsbDevice usbDevice) {
        State.usbDevice = usbDevice;
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
}
