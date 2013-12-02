package pro.kornev.kcar.cop.services.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

/**
 *
 */
public class UsbPermissionReceiver extends BroadcastReceiver {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private PendingIntent mPermissionIntent;

    public UsbPermissionReceiver(Context context) {
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(this, filter);
    }

    public PendingIntent getPermissionIntent() {
        return mPermissionIntent;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                assert device != null;

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Permission restricted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
