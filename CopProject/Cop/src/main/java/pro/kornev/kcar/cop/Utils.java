package pro.kornev.kcar.cop;

/**
 *
 */
public class Utils {

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {
        }
    }
}
