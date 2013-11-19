package pro.kornev.kcar.cop.activities;

import android.content.Intent;
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
import pro.kornev.kcar.cop.services.VideoService;

public class MainActivity extends Activity {
    private Button runButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runButton = (Button)findViewById(R.id.maRunButton);
        setRunButtonText();
        State.setLogsEnabled(true);
    }

    @SuppressWarnings("unused")
    public void testProtocolClick(View v) {
        Intent intent = new Intent(this, TestProtocolActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void chooseUsbDeviceClick(View v) {
        Intent intent = new Intent(this, UsbDevicesActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void onShowLogsClick(View v) {
        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void onServiceRunClick(View v) {
        if (State.isServiceRunning()) {
            State.setServiceRunning(false);
            NetworkService.removeAllListeners();
        }
        else {
            EditText proxy = (EditText)findViewById(R.id.maProxyIp);
            if (proxy.getText() == null || proxy.getText().toString().length() == 0) {
                // TODO : show error message
                return;
            }
            State.setProxyServer(proxy.getText().toString());
            State.setServiceRunning(true);

            if (State.getUsbSerialDriver() != null) {
                Intent usbServiceIntent = new Intent(this, UsbService.class);
                startService(usbServiceIntent);
            }
            Intent videoServiceIntent = new Intent(this, VideoService.class);
            startService(videoServiceIntent);
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
