package pro.kornev.kcar.cop.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

public class TestProtocolActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_protocol);
    }

    public void protocolButtonClick(View view) {
        TextView text = (TextView)findViewById(R.id.tpTextView);
        text.append(String.format("Version: %02x\n", Protocol.getVersion()));


        Data data = new Data();
        data.id = 1;
        data.cmd = 2;
        data.type = 0;
        data.bData = 3;
        byte[] buf = Protocol.toByteArray(data);

        text.append("Convert data to byte buffer:\n");
        text.append(String.format("Data:\n\tid: %d\n\tcmd: %d\n\ttype: %d\n\tbData: %d\n\tiData: %d\n",
                data.id, data.cmd, data.type, data.bData, data.iData));
        text.append("Byte buffer:\n");
        for (byte b: buf) {
            text.append(String.format("%02x ", b));
        }
        text.append("\n");

        text.append("Convert byte buffer to data:\n");
        Data newData = Protocol.fromByteArray(buf, buf.length);
        text.append("Byte buffer:\n");
        for (byte b: buf) {
            text.append(String.format("%02x ", b));
        }
        text.append("\n");
        text.append(String.format("Data:\n\tid: %d\n\tcmd: %d\n\ttype: %d\n\tbData: %d\n\tiData: %d\n",
                newData.id, newData.cmd, newData.type, newData.bData, newData.iData));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_protocol, menu);
        return true;
    }
    
}
