package pro.kornev.kcontrol.service.network;

import pro.kornev.kcar.protocol.Data;

/**
 * Created with IntelliJ IDEA.
 * User: vkornev
 * Date: 13.11.13
 * Time: 12:18
 */
public interface ProxyServiceListener {
    public void onPackageReceive(Data data);
}
