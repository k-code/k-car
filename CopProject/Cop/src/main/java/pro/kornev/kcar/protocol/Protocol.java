package pro.kornev.kcar.protocol;

import java.nio.ByteBuffer;

/**
 * User: vkornev
 * Date: 02.10.13
 * Time: 10:34
 */
public class Protocol {

    static
    {
        System.loadLibrary("protocol");
    }

    native public static byte[] toByteArray(Data data);
    native public static Data fromByteArray(byte[] buf, int len);
}
