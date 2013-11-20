package pro.kornev.kcar.protocol;

/**
 * User: vkornev
 * Date: 02.10.13
 * Time: 16:08
 */


public class Data {
    public int id;
    public byte cmd;
    public byte type;
    public byte bData;
    public int iData;
    public int aSize;
    public byte[] aData;

    public Data() {
        id=0;
        cmd=0;
        type=0;
        bData=0;
        iData=0;
        aSize=0;
        aData=null;
    }
}
