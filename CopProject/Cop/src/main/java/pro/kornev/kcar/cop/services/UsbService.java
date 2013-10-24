package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Queue;

import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 * @author vkornev
 * @since 14.10.13
 */
public class UsbService extends Service {
    private LogsDB db;

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
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        new Thread(new Process(this)).start();
        return START_STICKY;
    }

    class Process implements Runnable {
        UsbService mainService;
        private boolean forceClaim = true;

        Process(UsbService mainService) {
            this.mainService = mainService;
        }

        @Override
        public void run() {
            UsbDevice device = State.getUsbDevice();
            if (device == null) {
                db.putLog("USB device is null. USB service exit");
                return;
            }
            UsbInterface usbInterface = null;
            if (device.getInterfaceCount() > 1) {
                for (int i=0; i< device.getInterfaceCount(); i++) {
                    usbInterface = device.getInterface(i);
                    if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA) {
                        break;
                    }
                    else {
                        usbInterface = null;
                    }
                }
            }
            if (usbInterface == null) {
                db.putLog("Unknown USB device. CDC USB interfaces not found");
                return;
            }
            if (usbInterface.getEndpointCount() != 2) {
                db.putLog("Unknown USB device. USB endpoints not equals two");
                return;
            }
            UsbEndpoint inputEndpoint = null;
            UsbEndpoint outputEndpoint = null;
            for (int i=0; i<2; i++) {
                UsbEndpoint endpoint = usbInterface.getEndpoint(i);
                if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                    inputEndpoint = endpoint;
                }
                else if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    outputEndpoint = endpoint;
                }
            }
            if (inputEndpoint == null) {
                db.putLog("USB input endpoint not found");
                return;
            }
            if (outputEndpoint == null) {
                db.putLog("USB output endpoint not found");
                return;
            }
            UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection connection = mUsbManager.openDevice(device);


            if (connection == null) {
                db.putLog("Failed to open connection to USB device");
                return;
            }
            if (!connection.claimInterface(usbInterface, forceClaim)) {
                db.putLog("Failed claim exclusive access to a USB interface");
                return;
            }
            connection.controlTransfer(0x40, 0, 0, 0, null, 0, 0);//reset
            connection.controlTransfer(0x40, 0, 1, 0, null, 0, 0);//clear Rx
            connection.controlTransfer(0x40, 0, 2, 0, null, 0, 0);//clear Tx
            connection.controlTransfer(0x40, 0x03, 0x4138, 0, null, 0, 0);//baudrate 9600

            Thread reader = new Thread(new Reader(inputEndpoint, connection));
            Thread writer = new Thread(new Writer(outputEndpoint, connection));

            reader.start();
            writer.start();

            try {
                reader.join();
                writer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mainService.stopSelf();
        }
    }

class Reader implements Runnable {
    UsbEndpoint endpoint;
    UsbDeviceConnection connection;
    Queue<Data> queue;
    private byte[] bytes;
    private int TIMEOUT = 10;

    Reader(UsbEndpoint endpoint, UsbDeviceConnection connection) {
        this.endpoint = endpoint;
        this.connection = connection;
        queue = State.getFromUsbQueue();
        bytes = new byte[Protocol.getMaxLength()];
    }

    @Override
    public void run() {
        db.putLog("Start USB reader");
        while (State.isServiceRunning()) {
            db.putLog("Read bulk data");
            int len = connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
            String s = connection.getSerial();
            db.putLog("Read data len: " + len);
            if (len <= 0) continue;
            Data data = Protocol.fromByteArray(bytes, len);
            db.putLog(String.format("USB read: Data id: %d; cmd: %d; type: %d; bData: %d; iData: %d", data.id, data.cmd, data.type, data.bData, data.iData));
            queue.add(data);
        }
    }
}

class Writer implements Runnable {
    UsbEndpoint endpoint;
    UsbDeviceConnection connection;
    Queue<Data> queue;
    private byte[] bytes = new byte[Protocol.getMaxLength()];
    private int TIMEOUT = 10;

    Writer(UsbEndpoint endpoint, UsbDeviceConnection connection) {
        this.endpoint = endpoint;
        this.connection = connection;
        queue = State.getToUsbQueue();
    }

    @Override
    public void run() {
        db.putLog("Start USB writer");
        while (State.isServiceRunning()) {
            if (queue.size() == 0) continue;
            db.putLog("Write bulk data");
            Data data = queue.poll();
            int bLen = Protocol.toByteArray(data, bytes);
            connection.bulkTransfer(endpoint, bytes, bLen, TIMEOUT);
            db.putLog(String.format("USB write: Data id: %d; cmd: %d; type: %d; bData: %d; iData: %d", data.id, data.cmd, data.type, data.bData, data.iData));
        }
    }

}

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        State.setServiceRunning(false);
    }
}
