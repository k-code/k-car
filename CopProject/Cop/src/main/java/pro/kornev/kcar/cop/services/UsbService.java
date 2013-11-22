package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
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
public class UsbService extends Service implements NetworkListener, SerialInputOutputManager.Listener {
    private static final int DRIVER_SCAN_TIMEOUT = 1000;
    private LogsDB db;
    //private static UsbSerialDriver sDriver = null;
    private volatile SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new LogsDB(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        NetworkService.addListener(this);
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        new Thread(new Controller()).start();
        db.putLog("US Is running");
        return START_STICKY;
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

    class Controller implements Runnable {
        private UsbSerialDriver driver = null;
        private Writer writer = null;

        @Override
        public void run() {
            db.putLog("US Running Controller");
            while (State.isServiceRunning()) {
                UsbSerialDriver newDriver = State.getUsbSerialDriver();
                if (newDriver == null || newDriver.equals(driver)) {
                    //db.putLog("No serial device.");
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

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        State.setServiceRunning(false);
    }
}
