package pro.kornev.kcar.protocol.test;

import org.junit.Test;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 * User: kvv
 * Date: 24.10.13
 * Time: 21:19
 */

public class ProtocolTest {

    @Test
    public void testProtocol() {
        Data data = new Data();
        data.id = 1;
        data.cmd = 2;
        data.type = 0;
        data.bData = 3;
        data.iData = 4;
        byte[] buf = new byte[Protocol.getMaxLength()];
        int len = Protocol.toByteArray(data, buf);
        for (int i=0; i<len; i++) {
            System.out.print(String.format("%02x ", buf[i]));
        }
    }
}
