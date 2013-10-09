package pro.kornev.kcar.cop;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void protocolButtonClick(View view) {
        Button button = (Button)view;
        Data data = new Data();
        data.id = 1;
        data.cmd = 2;
        data.type = 0;
        data.bData = 3;
        byte[] buf = Protocol.toByteArray(data);

        TextView text = (TextView)findViewById(R.id.TestProtocolOutput);
        text.setText("");
        for (byte b: buf) {
            text.append(String.format("%02x ", b));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
