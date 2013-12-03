package pro.kornev.kcar.cop.services.network;

import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 *
 */
public final class PingTask implements Runnable {
    private NetworkService networkService;
    private LogsDB log;

    public PingTask(NetworkService networkService) {
        this.networkService = networkService;
        log = new LogsDB(networkService);
    }

    @Override
    public void run() {
        log.putLog("NP Ping");
        Data data = new Data();
        data.cmd = Protocol.Cmd.ping();
        networkService.write(data);
    }
}
