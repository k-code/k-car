package pro.kornev.kcar.cop.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.services.NetworkService;
import pro.kornev.kcar.cop.services.UsbService;

public class MainActivity extends Activity {
    private static final String ACTION_USB_PERMISSION =
        "com.android.example.USB_PERMISSION";
    private Button runButton;
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runButton = (Button)findViewById(R.id.maRunButton);
        setRunButtonText();
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        registerReceiver(mUsbReceiver, filter);
    }

    public void testProtocolClick(View v) {
        Intent intent = new Intent(this, TestProtocolActivity.class);
        startActivity(intent);
    }

    public void chooseUsbDeviceClick(View v) {
        Intent intent = new Intent(this, UsbDevicesActivity.class);
        startActivity(intent);
    }

    public void onShowLogsClick(View v) {
        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                        }
                    }
                    else {
                    }
                }
            }
        }
    };

    public void onServiceRunClick(View v) {
        if (State.isServiceRunning()) {
            State.setServiceRunning(false);
        }
        else {
            EditText proxy = (EditText)findViewById(R.id.maProxyIp);
            if (proxy.getText() == null || proxy.getText().toString().length() == 0) {
                // TODO : show error message
                return;
            }
            State.setProxyServer(proxy.getText().toString());
            State.setServiceRunning(true);

            mUsbManager.requestPermission(State.getUsbDevice(), mPermissionIntent);
            Intent usbServiceIntent = new Intent(this, UsbService.class);
            startService(usbServiceIntent);
            Intent networkServiceIntent = new Intent(this, NetworkService.class);
            startService(networkServiceIntent);
        }
        setRunButtonText();
    }

    public void onEnableLogsClick(View v) {
        CheckBox cb = (CheckBox)v;
        State.setLogsEnabled(cb.isChecked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setRunButtonText() {
        if (State.isServiceRunning()) {
            runButton.setText("Stop services");
        }
        else {
            runButton.setText("Run services");
        }
    }
}
