package pro.kornev.kcar.proxy;

import com.google.gson.Gson;
import org.junit.Test;
import pro.kornev.kcar.protocol.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * User: kvv
 * Date: 24.10.13
 * Time: 21:44
 */
public class ProxyTest {
    private Gson gson = new Gson();
    @Test
    public void testPingStm() throws Exception {
        Data data = new Data();
        data.id = 1;
        data.cmd = 1;
        data.type = 1;
        data.iData = 0;

        Socket socket = new Socket("localhost", 6781);
        assertTrue(socket.isConnected());

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        String s = gson.toJson(data);
        output.write(gson.toJson(data));
        output.newLine();
        output.flush();

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        s = input.readLine();
        Data rData = gson.fromJson(s, Data.class);
    }
}
