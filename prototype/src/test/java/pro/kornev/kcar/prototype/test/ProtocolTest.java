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
            System.out.print(String.format("%x ", buf[i]));
        }
    }
}
