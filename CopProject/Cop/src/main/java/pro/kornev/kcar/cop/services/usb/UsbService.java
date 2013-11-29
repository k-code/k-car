package pro.kornev.kcar.cop.services.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.providers.ConfigDB;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.cop.services.CopService;
import pro.kornev.kcar.cop.services.network.NetworkListener;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 * @author vkornev
 * @since 14.10.13
 */
public class UsbService implements NetworkListener, SerialInputOutputManager.Listener {
    private static final int DRIVER_SCAN_TIMEOUT = 1000;
    private static final int WRITE_TIMEOUT = 1000;
    private final LogsDB log;
    private final ConfigDB config;
    private volatile SerialInputOutputManager mSerialIoManager;
    private volatile UsbSerialDriver driver;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private CopService copService;
    private UsbPermissionReceiver usbPermissionReceiver;

    public UsbService(CopService copService) {
        this.copService = copService;
        log = new LogsDB(this.copService);
        config = new ConfigDB(this.copService);
        usbPermissionReceiver = new UsbPermissionReceiver(this.copService);
    }

    public void start() {
        driver = getDriver();
        if (driver == null) {
            log.putLog("US Failed get USB driver");
            return;
        }
        startIoManager(driver);
        log.putLog("US Is running");
    }

    public void stop() {
        stopIoManager();
    }

    @Override
    public void onRunError(Exception e) {
        log.putLog("US Run error: " + e.getMessage());
        Utils.sleep(DRIVER_SCAN_TIMEOUT);
        start();
    }

    @Override
    public void onNewData(final byte[] buf) {
        Data data = Protocol.fromByteArray(buf, buf.length);
        log.putLog("US Receive data: cmd=" + data.cmd);
        sendToNetwork(data);
    }

    private void stopIoManager() {
        try {
            if (mSerialIoManager != null) {
                log.putLog("US Stopping io manager ..");
                mSerialIoManager.stop();
                mSerialIoManager = null;
            }
            if (driver != null) {
                driver.close();
            }
        } catch (Exception ignored) {}
    }

    private void startIoManager(UsbSerialDriver driver) {
        try {
            if (driver != null) {
                driver.open();
                log.putLog("US Starting io manager ..");
                mSerialIoManager = new SerialInputOutputManager(driver, this);
                mExecutor.submit(mSerialIoManager);
            }
        } catch (Exception e) {
            log.putLog("US Start IO manager is failed: " + e.getMessage());
        }
    }

    @Override
    public void onDataReceived(Data data) {
        if ((data.cmd == Protocol.Cmd.ping() && data.bData == 0)
                || (data.cmd >= Protocol.Cmd.autoFirst() && data.cmd <= Protocol.Cmd.autoLast())) {
            write(data);
        }
    }

    private UsbSerialDriver getDriver() {
        String usbDeviceName = config.getUsbDevice();
        if (usbDeviceName == null) return null;
        UsbManager mUsbManager = (UsbManager) copService.getSystemService(Context.USB_SERVICE);
        Map<String, UsbDevice> usbDeviceList = mUsbManager.getDeviceList();
        if (usbDeviceList == null || usbDeviceList.isEmpty()) return null;
        UsbDevice usbDevice = usbDeviceList.get(usbDeviceName);
        if (usbDevice == null) return null;
        if (!mUsbManager.hasPermission(usbDevice)) {
            mUsbManager.requestPermission(usbDevice, usbPermissionReceiver.getPermissionIntent());
            if (!mUsbManager.hasPermission(usbDevice)) {
                return null;
            }
        }
        return UsbSerialProber.probeSingleDevice(mUsbManager, usbDevice).get(0);
    }

    private void sendToNetwork(Data data) {
        copService.getNetworkService().write(data);
    }

    private void write(Data data) {
        try {
            if (driver == null) return;
            byte[] buf = new byte[Protocol.getMaxLength()];
            log.putLog("UW Write data cmd: " + data.cmd);
            int bLen = Protocol.toByteArray(data, buf);
            driver.write(Arrays.copyOf(buf, bLen), WRITE_TIMEOUT);
        } catch (Exception e) {
            log.putLog("UW Failed write data: " + e.getMessage());
        }

    }
}
