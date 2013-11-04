package pro.kornev.kcar.cop;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

/**
 *
 * Created by kvv on 03.11.13.
 */
public class UsbDeviceEntry {
    public UsbDevice device;
    public UsbSerialDriver driver;

    public UsbDeviceEntry(UsbDevice device, UsbSerialDriver driver) {
        this.device = device;
        this.driver = driver;
    }
}
