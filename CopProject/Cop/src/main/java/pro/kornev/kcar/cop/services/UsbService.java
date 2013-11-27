package pro.kornev.kcar.cop.services;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.providers.ConfigDB;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 * @author vkornev
 * @since 14.10.13
 */
public class UsbService implements NetworkListener, SerialInputOutputManager.Listener {
    private static final int DRIVER_SCAN_TIMEOUT = 1000;
    private final LogsDB log;
    private final ConfigDB config;
    private final Queue<Data> outputQueue;
    private final Queue<Data> inputQueue;
    private volatile SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private CopService copService;
    private UsbPermissionReceiver usbPermissionReceiver;

    public UsbService(CopService copService) {
        this.copService = copService;
        log = new LogsDB(this.copService);
        config = new ConfigDB(this.copService);
        usbPermissionReceiver = new UsbPermissionReceiver(this.copService);
        outputQueue = copService.getToUsbQueue();
        inputQueue = copService.getToControlQueue();
    }

    public void start() {
        new Thread(new Controller()).start();
        log.putLog("US Is running");
    }

    @Override
    public void onRunError(Exception e) {
        log.putLog("US Runner stopped.");
    }

    @Override
    public void onNewData(final byte[] data) {
        log.putLog("US Read data len: " + data.length);
        log.putLog("US Read data: " + HexDump.dumpHexString(data));
        inputQueue.add(Protocol.fromByteArray(data, data.length));
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            log.putLog("US Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager(UsbSerialDriver driver) {
        if (driver != null) {
            log.putLog("US Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(driver, this);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange(UsbSerialDriver driver) {
        stopIoManager();
        startIoManager(driver);
    }

    @Override
    public void onDataReceived(Data data) {
        if ((data.cmd == Protocol.Cmd.ping() && data.bData == 0)
                || (data.cmd >= Protocol.Cmd.autoFirst() && data.cmd <= Protocol.Cmd.autoLast())) {
            outputQueue.add(data);
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

    class Controller implements Runnable {
        private UsbSerialDriver driver = null;
        private Writer writer = null;

        @Override
        public void run() {
            log.putLog("US Running Controller");
            while (copService.isRunning()) {
                try {
                    driver = getDriver();
                    if (driver == null) {
                        log.putLog("US No serial device.");
                        Utils.sleep(DRIVER_SCAN_TIMEOUT);
                        continue;
                    }
                } catch (Throwable e) {
                    log.putLog("US Get device error: " + e.getMessage());
                }
                try {
                    log.putLog("US Usb device found");
                    driver.open();
                    log.putLog("US Serial device: " + driver.getClass().getSimpleName());

                    if (writer != null) {
                        log.putLog("US Stopping Writer");
                        writer.stop();
                    }

                    onDeviceStateChange(driver);

                    log.putLog("US Starting Writer");
                    writer = new Writer(driver, outputQueue);
                    Thread writerThread = new Thread(writer);
                    writerThread.start();
                    writerThread.join();
                } catch (Exception e) {
                    log.putLog("US Error: " + e.getMessage());
                    try {
                        driver.close();
                    } catch (IOException ignored) {
                    }
                    Utils.sleep(DRIVER_SCAN_TIMEOUT);
                }
            }
            if (writer != null) {
                writer.stop();
            }
            log.putLog("US Controller stopped");
        }
    }

    class Writer implements Runnable {
        private Queue<Data> outputQueue;
        private byte[] bytes = new byte[Protocol.getMaxLength()];
        private int TIMEOUT = 10;
        private UsbSerialDriver driver;
        private boolean working;

        Writer(UsbSerialDriver driver, Queue<Data> q) {
            outputQueue = q;
            this.driver = driver;
            this.working = true;
        }

        @Override
        public void run() {
            log.putLog("UW Start USB writer");

            while (isWorking()) {
                try {
                    if (outputQueue.size() == 0) {
                        Utils.sleep(1);
                        continue;
                    }
                    Data data = outputQueue.poll();
                    log.putLog("UW Write data cmd: " + data.cmd);
                    int bLen = Protocol.toByteArray(data, bytes);
                    driver.write(Arrays.copyOf(bytes, bLen), TIMEOUT);
                } catch (Exception e) {
                    log.putLog("Error: Failed send data to USB: " + e.getMessage());
                }
            }
            log.putLog("UW Was stopped");
        }

        public synchronized void stop() {
            this.working = false;
        }

        private synchronized boolean isWorking() {
            return working;
        }
    }
}
