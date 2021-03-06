package pro.kornev.kcar.protocol.test;

import org.junit.Assert;
import org.junit.Test;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

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
        System.out.println();

        Data newData = Protocol.fromByteArray(buf, len);

        assertNotNull("Array is null", newData.aData);
        assertTrue("Array length less or equals 0", newData.aData.length > 0);

        for (int i=0; i < data.aSize; i++) {
            assertEquals(data.aData[i], newData.aData[i]);
        }
    }

    @Test
    public void testStreams() throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
        for (int i=0; i<10; i++) {
            Data data = new Data();
            data.id = i;
            data.cmd = (byte)i;
            data.type = 0;
            data.bData = 0;

            Protocol.toOutputStream(data, outputStream);

            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            Data newData = Protocol.fromInputStream(inputStream);
            byteArrayOutputStream.reset();

            assertEquals(data.id, newData.id);
            assertEquals(data.cmd, newData.cmd);
        }

    }

    @Test
    public void testNativeMethods () {
        assertTrue(Protocol.byteType() == 0);
        assertTrue(Protocol.intType() > 0);
        assertTrue(Protocol.arrayType() > 0);
        assertTrue(Protocol.Cmd.reservedFirst() == 0);
        assertTrue(Protocol.Cmd.reservedLast() > 0);
        assertTrue(Protocol.Cmd.copFirst() > 0);
        assertTrue(Protocol.Cmd.copLast() > 0);
        assertTrue(Protocol.Cmd.autoFirst() > 0);
        assertTrue(Protocol.Cmd.autoLast() > 0);
        assertTrue(Protocol.Cmd.error() == 0);
        assertTrue(Protocol.Cmd.ping() > 0);
        assertTrue(Protocol.Cmd.camState() > 0);
        assertTrue(Protocol.Cmd.camImg() > 0);
        assertTrue(Protocol.Cmd.camFlash() > 0);
        assertTrue(Protocol.Cmd.camQuality() > 0);
        assertTrue(Protocol.Cmd.camFps() > 0);
        assertTrue(Protocol.Cmd.camSizeList() > 0);
        assertTrue(Protocol.Cmd.camSizeSet() > 0);
        assertTrue(Protocol.Cmd.autoUsReq() > 0);
        assertTrue(Protocol.Cmd.autoUsRes() > 0);
        assertTrue(Protocol.Cmd.autoTriggerLed() > 0);
        assertTrue(Protocol.Cmd.autoLMS() > 0);
        assertTrue(Protocol.Cmd.autoRMS() > 0);
    }
}
