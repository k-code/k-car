package pro.kornev.kcar.cop.services.network;

import android.os.Binder;

public class NetworkBinder extends Binder {
    private final NetworkService networkService;

    NetworkBinder(NetworkService networkService) {
        this.networkService = networkService;
    }

    public NetworkService getService() {
            return networkService;
        }
    }
