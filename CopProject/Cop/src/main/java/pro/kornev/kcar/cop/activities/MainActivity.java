package pro.kornev.kcar.cop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.providers.LogData;
import pro.kornev.kcar.cop.providers.LogsDB;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO : remove this
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(new Runnable() {

            private LogsDB db = new LogsDB(getApplicationContext());
            private int i = 0;

            @Override
            public void run() {
                if (State.isLogsEnabled())
                    db.putLog("this is test log message #" + i++);
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
