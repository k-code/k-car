package pro.kornev.kcar.cop;

import android.hardware.usb.UsbDevice;

/**
 *
 * @author vkornev
 * @since 14.10.13
 */
public class State {
    private static UsbDevice usbDevice;
    private static boolean isUsbDeviceConnected;
    private static boolean isLogsEnabled;

    public static UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public static void setUsbDevice(UsbDevice usbDevice) {
        State.usbDevice = usbDevice;
    }

    public static boolean isUsbDeviceConnected() {
        return isUsbDeviceConnected;
    }

    public static void setUsbDeviceConnected(boolean usbDeviceConnected) {
        isUsbDeviceConnected = usbDeviceConnected;
    }

    public static boolean isLogsEnabled() {
        return isLogsEnabled;
    }

    public static void setLogsEnabled(boolean logsEnabled) {
        isLogsEnabled = logsEnabled;
    }
}
