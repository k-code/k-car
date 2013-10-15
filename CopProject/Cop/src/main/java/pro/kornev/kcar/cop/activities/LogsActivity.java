package pro.kornev.kcar.cop.activities;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Looper;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.cop.providers.LogData;
import pro.kornev.kcar.cop.providers.LogsDB;

public class LogsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs_activity);
        this.runOnUiThread(new LogsViewUpdater((TextView) findViewById(R.id.raText), getApplicationContext()));
    }

    public void onClearButtonClick(View v) {
        TextView text = (TextView)findViewById(R.id.raText);
        text.clearComposingText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.run, menu);
        return true;
    }

    class LogsViewUpdater implements Runnable {
        private TextView textView;
        private int lastRecordId = 0;
        private LogsDB db;

        LogsViewUpdater(TextView v, Context c) {
            textView = v;
            db = new LogsDB(c);
        }

        @Override
        public void run() {
            while (true) {
            SparseArray<LogData> logs = db.getLogs(lastRecordId);
            for (int i=0; i < logs.size(); i++) {
                LogData ld = logs.get(i);
                lastRecordId = ld.getId();
                textView.append(ld.getLog() + "\n");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
        }
    }
}
