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
import pro.kornev.kcar.cop.State;

public class UsbDevicesActivity extends Activity {
    private Map<String, UsbDevice> usbList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usb_devices_activity);
        refresh();
    }

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
        usbList = manager.getDeviceList();
        if (usbList==null || usbList.isEmpty()) return;

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
            }
        });
    }

    public void showMessageBox(String title, final String deviceName) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(deviceName);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                State.setUsbDevice(usbList.get(deviceName));
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
