package pro.kornev.kcar.proxy;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 20.11.13
 * Time: 10:51
 */
public final class Utils {

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
