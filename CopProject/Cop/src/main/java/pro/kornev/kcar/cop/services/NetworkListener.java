package pro.kornev.kcar.cop.services;

import pro.kornev.kcar.protocol.Data;

/**
 *
 */
public interface NetworkListener {
    public void onDataReceived(Data data);
}
