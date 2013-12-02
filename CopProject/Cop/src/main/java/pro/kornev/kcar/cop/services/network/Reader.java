package pro.kornev.kcar.cop.services.network;

import android.content.Context;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.List;

import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

final class Reader implements Runnable {
    private final Socket socket;
    private final LogsDB log;
    private final List<NetworkListener> listeners;

    public Reader(Context context, Socket socket, List<NetworkListener> listeners) {
        this.socket = socket;
        this.listeners = listeners;
        log = new LogsDB(context);
        log.putLog("NR Created");
    }

    @Override
    public void run() {
        log.putLog("NR Start network reader");
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            while (!socket.isClosed()) {
                Data data = Protocol.fromInputStream(input);

                log.putLog(String.format("NR got data id: %d; cmd: %d", data.id, data.cmd));
                for (NetworkListener l : listeners) {
                    l.onDataReceived(data);
                }
            }
        } catch (Exception e) {
            log.putLog("NR error: " + e.getMessage());
        }
        log.putLog("NR Stop network reader");
        NetworkService.closeSocket(socket);
    }
}