package pro.kornev.kcar.cop.services;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.Utils;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 * @author vkornev
 * @since 14.10.13
 */
public class UsbService implements NetworkListener, SerialInputOutputManager.Listener {
    private static final int DRIVER_SCAN_TIMEOUT = 1000;
    private final LogsDB db;
    private volatile SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private CopService copService;

    public UsbService(CopService copService) {
        this.copService = copService;
        db = new LogsDB(this.copService);
    }

    public void start() {
        new Thread(new Controller()).start();
        db.putLog("US Is running");
    }

    @Override
    public void onRunError(Exception e) {
        db.putLog("US Runner stopped.");
    }

    @Override
    public void onNewData(final byte[] data) {
        db.putLog("US Read data len: " + data.length);
        db.putLog("US Read data: " + HexDump.dumpHexString(data));
        State.getToControlQueue().add(Protocol.fromByteArray(data, data.length));
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            db.putLog("US Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager(UsbSerialDriver driver) {
        if (driver != null) {
            db.putLog("US Starting io manager ..");
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
            State.getToUsbQueue().add(data);
        }
    }

    private UsbSerialDriver getDriver() {
        UsbManager mUsbManager = (UsbManager) copService.getSystemService(Context.USB_SERVICE);
        return UsbSerialProber.findFirstDevice(mUsbManager);
    }

    class Controller implements Runnable {
        private UsbSerialDriver driver = null;
        private Writer writer = null;

        @Override
        public void run() {
            db.putLog("US Running Controller");
            while (copService.isRunning()) {
                UsbSerialDriver newDriver = getDriver();
                if (newDriver == null || newDriver.equals(driver)) {
                    db.putLog("US No serial device.");
                    Utils.sleep(DRIVER_SCAN_TIMEOUT);
                    continue;
                }
                driver = newDriver;
                try {
                    db.putLog("US Usb device found");
                    driver.open();
                    db.putLog("US Serial device: " + driver.getClass().getSimpleName());

                    if (writer != null) {
                        db.putLog("US Stopping Writer");
                        writer.stop();
                    }

                    onDeviceStateChange(driver);

                    db.putLog("US Starting Writer");
                    writer = new Writer(driver);
                    Thread writerThread = new Thread(writer);
                    writerThread.start();
                } catch (Exception e) {
                    db.putLog("US Error: " + e.getMessage());
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
            db.putLog("US Controller stopped");
        }
    }

    class Writer implements Runnable {
        private Queue<Data> queue;
        private byte[] bytes = new byte[Protocol.getMaxLength()];
        private int TIMEOUT = 10;
        private UsbSerialDriver driver;
        private boolean working;

        Writer(UsbSerialDriver driver) {
            queue = State.getToUsbQueue();
            this.driver = driver;
            this.working = true;
        }

        @Override
        public void run() {
            db.putLog("UW Start USB writer");

            while (isWorking()) {
                if (queue.size() == 0) {
                    Utils.sleep(1);
                    continue;
                }
                Data data = queue.poll();
                db.putLog("UW Write data cmd: " + data.cmd);
                int bLen = Protocol.toByteArray(data, bytes);
                try {
                    driver.write(Arrays.copyOf(bytes, bLen), TIMEOUT);
                } catch (IOException e) {
                    db.putLog("Error: Failed send data to USB: " + e.getMessage());
                }
            }
            db.putLog("UW Was stopped");
        }

        public synchronized void stop() {
            this.working = false;
        }

        private synchronized boolean isWorking() {
            return working;
        }
    }
}
