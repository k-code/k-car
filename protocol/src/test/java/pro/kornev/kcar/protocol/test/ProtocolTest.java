package pro.kornev.kcar.protocol.test;

import org.junit.Assert;
import org.junit.Test;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
            System.out.print(String.format("%02X ", buf[i]));
        }
    }

    @Test
    public void testArrayData() {
        Data data = new Data();
        data.id = 1;
        data.cmd = 2;
        data.type = 2;
        data.aSize = 8;
        data.aData = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};

        byte[] buf = new byte[Protocol.getMaxLength()];
        int len = Protocol.toByteArray(data, buf);
        for (int i=0; i<len; i++) {
            System.out.print(String.format("%02X ", buf[i]));
        }
        Data newData = Protocol.fromByteArray(buf, len);

        assertNotNull("Array is null", newData.aData);
        assertTrue("Array length less or equals 0", newData.aData.length > 0);

        for (int i=0; i < data.aSize; i++) {
            assertEquals(data.aData[i], newData.aData[i]);
        }
    }
}
