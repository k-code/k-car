package pro.kornev.kcar.cop.activities;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.providers.LogData;
import pro.kornev.kcar.cop.providers.LogsDB;

public class LogsActivity extends Activity {
    private Handler handler = new Handler();
    private volatile LogsViewUpdater logsViewUpdater;
    private LogsDB db;
    private ScheduledThreadPoolExecutor executor;
    private TextView text;
    private CheckBox autoScroll;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs_activity);
        db = new LogsDB(getApplicationContext());
        text = (TextView) findViewById(R.id.laText);
        autoScroll = (CheckBox)findViewById(R.id.laAutoScrollCheckBox);
        scrollView = (ScrollView) findViewById(R.id.laScroll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUG", "la start");
        runDbReader();
        clear(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DEBUG", "la stop");
        executor.shutdown();
    }

    public void onClearButtonClick(View v) {
        clear(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.run, menu);
        return true;
    }

    class LogsViewUpdater implements Runnable {
        private TextView textView;
        private Handler handler;
        private int lastRecordId = 0;
        private LogsDB logsDB;

        LogsViewUpdater(TextView v, Handler h, LogsDB db) {
            textView = v;
            handler = h;
            logsDB = db;
        }

        public synchronized void setLastRecordId(int lastRecordId) {
            this.lastRecordId = lastRecordId;
        }

        public synchronized int getLastRecordId() {
            return lastRecordId;
        }

        @Override
        public void run() {
            SparseArray<LogData> logs = logsDB.getLogs(getLastRecordId());
            if (logs.size() == 0) return;
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < logs.size(); i++) {
                LogData ld = logs.get(i);
                setLastRecordId(ld.getId());
                sb.append(ld.getLog());
                sb.append("\n");
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textView.append(sb.toString());
                    if (autoScroll.isChecked()) {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }
            });
        }
    }

    private void runDbReader() {
        logsViewUpdater = new LogsViewUpdater(text, handler, db);
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(logsViewUpdater, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void clear(boolean clearDb) {
        text.setText("");
        if (clearDb) {
            db.clearLogs();
        }
        if (logsViewUpdater != null) {
            logsViewUpdater.setLastRecordId(0);
        }
    }
}
