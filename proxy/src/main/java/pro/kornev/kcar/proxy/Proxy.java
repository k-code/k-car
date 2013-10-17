package pro.kornev.kcar.proxy;

/**
 * User: kvv
 * Date: 17.10.13
 * Time: 20:29
 */
public class Proxy {
    public static void main(String[] args) {
        CopService copService = new CopService();
        Thread t = new Thread(copService);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
