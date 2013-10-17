package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.widget.Toast;

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
        private byte[] bytes = new byte[64];
        private int TIMEOUT = 10;
        private boolean forceClaim = true;

        Process(UsbService mainService) {
            this.mainService = mainService;
        }

        @Override
        public void run() {
            while (State.isServiceRunning()) {
                UsbDevice device = State.getUsbDevice();
                if (device != null) {
                    UsbInterface intf = device.getInterface(0);
                    UsbEndpoint endpoint = intf.getEndpoint(0);
                    UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    UsbDeviceConnection connection = mUsbManager.openDevice(device);
                    if (connection == null) {
                        break;
                    }
                    connection.claimInterface(intf, forceClaim);
                    int len = connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
                    Data data = Protocol.fromByteArray(bytes, len);
                    db.putLog(String.format("Data id: %d; cmd: %d; type: %d; bData: %d; iData: %d", data.id, data.cmd, data.type, data.bData, data.iData));
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mainService.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        State.setServiceRunning(false);
    }
}
