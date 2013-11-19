package pro.kornev.kcar.protocol;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * User: vkornev
 * Date: 02.10.13
 * Time: 10:34
 */
public class Protocol {
    public static final byte[] STREAM_HEADER = {0x12, 0x34, 0x56, 0x78};

    static {
        System.loadLibrary("protocol");
    }

    native public static int toByteArray(Data data, byte[] buf);
    native public static Data fromByteArray(byte[] buf, int len);
    native public static byte getVersion();
    native public static int getMaxLength();

    public static void toOutputStream(Data data, DataOutputStream outputStream) throws IOException {
        byte[] dataBuf = new byte[getMaxLength()];
        int dataBufLen = toByteArray(data, dataBuf);
        outputStream.write(STREAM_HEADER, 0, 4);
        outputStream.writeInt(dataBufLen);
        outputStream.write(dataBuf, 0, dataBufLen);
        outputStream.flush();
    }

    public static Data fromInputStream(DataInputStream inputStream) throws IOException {
        byte[] buf = new byte[getMaxLength()];
        int bufLen = 0;
        while (bufLen == 0) {
            if (inputStream.readByte() == STREAM_HEADER[0] &&
                    inputStream.readByte() == STREAM_HEADER[1] &&
                    inputStream.readByte() == STREAM_HEADER[2] &&
                    inputStream.readByte() == STREAM_HEADER[3]) {
                bufLen = inputStream.readInt();
                break;
            }
        }
        int readBytes = 0;
        while (readBytes < bufLen) {
            readBytes += inputStream.read(buf, readBytes, bufLen-readBytes);
        }
        return fromByteArray(buf, bufLen);
    }
}
