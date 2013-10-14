package pro.kornev.kcar.cop;

import android.hardware.usb.UsbDevice;

/**
 *
 * @author vkornev
 * @since 14.10.13
 */
public class State {
    private static UsbDevice usbDevice;

    public static UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public static void setUsbDevice(UsbDevice usbDevice) {
        State.usbDevice = usbDevice;
    }
}
