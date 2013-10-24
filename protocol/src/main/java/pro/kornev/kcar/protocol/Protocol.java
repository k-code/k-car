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

    native public static int toByteArray(Data data, byte[] buf);
    native public static Data fromByteArray(byte[] buf, int len);
    native public static byte getVersion();
    native public static int getMaxLength();
}
