package pro.kornev.kcar.prototype.test;

import org.junit.Test;
import pro.kornev.kcar.prototype.Data;
import pro.kornev.kcar.prototype.Protocol;

/**
 * User: vkornev
 * Date: 02.10.13
 * Time: 17:45
 */
public class ProtocolTest {
    @Test
    public void testProtocol() {
        Data data = new Data();
        data.id = 1;
        data.cmd = 2;
        data.type = 0;
        data.bData = 3;
        byte buf[] = Protocol.toByteArray(data);
        System.out.println("len: " + buf.length);
        for (int i=0; i<buf.length; i++) {
            System.out.print(String.format("%02x ", buf[i]));
        }
    }

    @Test
    public void testFromByteArray() {
        byte buf[] = {(byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0x1, 0x2, 0x0, 0x3, 0x51};

        Data data = Protocol.fromByteArray(buf, buf.length);
        System.out.println("Data.id: " + data.id);
        System.out.println("Data.cmd: " + data.cmd);
        System.out.println("Data.type: " + data.type);
        System.out.println("Data.bData: " + data.bData);
        System.out.println("Data.iData: " + data.iData);
    }
}
