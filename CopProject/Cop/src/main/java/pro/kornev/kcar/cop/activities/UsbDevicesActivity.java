package pro.kornev.kcar.cop.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Map;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.cop.services.usb.UsbPermissionReceiver;

public class UsbDevicesActivity extends Activity {
    private UsbManager mUsbManager;
    private UsbPermissionReceiver usbPermissionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usb_devices_activity);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbPermissionReceiver = new UsbPermissionReceiver(this);
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
        final Map<String, UsbDevice> usbMap = mUsbManager.getDeviceList();
        if (usbMap ==null || usbMap.isEmpty()) return;

        ListView devicesList = (ListView)findViewById(R.id.uaDevicesList);
        String[] array = new String[usbMap.keySet().size()];
        array = usbMap.keySet().toArray(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.usb_devices_list_item, array);
        devicesList.setAdapter(adapter);

        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView item = (TextView)view;
                if (item == null || item.getText() == null) return;
                String usbPath = item.getText().toString();
                showMessageBox("Item", usbPath, usbMap.get(usbPath));
            }
        });
    }

    public void showMessageBox(String title, final String deviceName, final UsbDevice device) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(deviceName);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mUsbManager.requestPermission(device, usbPermissionReceiver.getPermissionIntent());
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
}
