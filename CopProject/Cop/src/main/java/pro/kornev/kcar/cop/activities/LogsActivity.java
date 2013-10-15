package pro.kornev.kcar.cop.activities;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.providers.LogData;
import pro.kornev.kcar.cop.providers.LogsDB;

public class LogsActivity extends Activity {
    private Handler mHandler = new Handler();
    private LogsViewUpdater logsViewUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs_activity);
        runDbReader();
    }

    public void onClearButtonClick(View v) {
        TextView text = (TextView) findViewById(R.id.raText);
        text.setText("");
        new LogsDB(getApplicationContext()).clear();
        logsViewUpdater.setLastRecordId(0);
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
        private LogsDB db;

        LogsViewUpdater(TextView v, Handler h, Context c) {
            textView = v;
            handler = h;
            db = new LogsDB(c);
        }

        public synchronized void setLastRecordId(int lastRecordId) {
            this.lastRecordId = lastRecordId;
        }

        public synchronized int getLastRecordId() {
            return lastRecordId;
        }

        @Override
        public void run() {
            if (State.isLogsEnabled()) {
                final StringBuilder sb = new StringBuilder();
                SparseArray<LogData> logs = db.getLogs(getLastRecordId());
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
                    }
                });
            }
        }
    }

    private void runDbReader() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        TextView text = (TextView) findViewById(R.id.raText);
        final Handler uiHandler = new Handler();
        logsViewUpdater = new LogsViewUpdater(text, uiHandler, getApplicationContext());
        executor.scheduleAtFixedRate(logsViewUpdater, 0, 100, TimeUnit.MILLISECONDS);
    }
}
