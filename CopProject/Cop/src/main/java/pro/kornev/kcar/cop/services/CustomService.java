package pro.kornev.kcar.cop.services;

import pro.kornev.kcar.cop.services.network.NetworkListener;

/**
 *
 */
public interface CustomService extends NetworkListener {
    public boolean start();
    public boolean stop();
}
