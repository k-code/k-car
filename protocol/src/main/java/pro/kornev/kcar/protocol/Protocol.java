package pro.kornev.kcar.protocol;

import java.io.*;

/**
 * User: vkornev
 * Date: 02.10.13
 * Time: 10:34
 */
@SuppressWarnings("unused")
public class Protocol {
    static {
        System.loadLibrary("protocol");
    }

    native public static int toByteArray(Data data, byte[] buf);
    native public static Data fromByteArray(byte[] buf, int len);
    native public static byte getVersion();
    native public static int getMaxLength();
    native public static byte byteType();
    native public static byte intType();
    native public static byte arrayType();

    public static class Cmd {
        static {
            System.loadLibrary("protocol");
        }
        // Commands categories
        // System
        native public static byte reservedFirst();
        native public static byte reservedLast();
        // COP
        native public static byte copFirst();
        native public static byte copLast();
        // Autopilot
        native public static byte autoFirst();
        native public static byte autoLast();

        // Commands
        // System
        native public static byte error();
        native public static byte ping();
        //COP
        native public static byte camState();
        native public static byte camImg();
        native public static byte camFps();
        native public static byte camQuality();
        native public static byte camFlash();
        native public static byte camSizeList();
        native public static byte camSizeSet();
        //Autopilot
        native public static byte autoTriggerLed();
        native public static byte autoUsReq();
        native public static byte autoUsRes();
        native public static byte autoLMS();
        native public static byte autoRMS();
    }

    public static final byte[] STREAM_HEADER = {0x12, 0x34, 0x56, 0x78};

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
            }
        }
        int readBytes = 0;
        while (readBytes < bufLen) {
            readBytes += inputStream.read(buf, readBytes, bufLen-readBytes);
        }
        return fromByteArray(buf, bufLen);
    }
}
