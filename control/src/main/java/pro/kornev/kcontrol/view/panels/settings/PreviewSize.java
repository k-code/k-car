package pro.kornev.kcontrol.view.panels.settings;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 21.11.13
 * Time: 17:01
 */
public class PreviewSize {
    public int width;
    public int height;

    @Override
    public String toString() {
        return String.format("%d x %d", width, height);
    }
}
