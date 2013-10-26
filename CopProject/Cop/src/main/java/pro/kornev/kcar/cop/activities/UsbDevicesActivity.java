package pro.kornev.kcar.cop.activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.cop.State;

public class UsbDevicesActivity extends Activity {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usb_devices_activity);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        registerReceiver(mUsbReceiver, filter);
        refresh();
    }

    @SuppressWarnings("unused")
    public void refreshButtonClick(View v) {
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.usb_devices, menu);
        return true;
    }

    private void refresh() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Map<String, UsbDevice> usbList = manager.getDeviceList();
        if (usbList ==null || usbList.isEmpty()) return;

        ListView devicesList = (ListView)findViewById(R.id.uaDevicesList);
        String[] array = new String[usbList.keySet().size()];
        array = usbList.keySet().toArray(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.usb_devices_list_item, array);
        devicesList.setAdapter(adapter);

        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView item = (TextView)view;
                if (item.getText() == null) return;
                showMessageBox("Item", item.getText().toString());
                mUsbManager.requestPermission(State.getUsbDevice(), mPermissionIntent);
            }
        });
    }

    public void showMessageBox(String title, final String deviceName) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(deviceName);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            State.setUsbDevice(device);
                        }
                    }
                    else {
                        Toast.makeText(context, "Permission restricted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
}
