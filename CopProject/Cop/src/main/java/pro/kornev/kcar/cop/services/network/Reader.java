package pro.kornev.kcar.cop.services.network;

import android.content.Context;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;
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
    }

    @Override
    public void run() {
        log.putLog("NR Start network reader");
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            while (!socket.isClosed()) {
                Data data = Protocol.fromInputStream(input);

                log.putLog(String.format("NR got data id: %d; cmd: %d", data.id, data.cmd));
                for (NetworkListener l : getListeners()) {
                    l.onDataReceived(data);
                }
            }
        } catch (Exception e) {
            log.putLog("NR error: " + e.getMessage());
            e.printStackTrace();
        }
        log.putLog("NR Stop network reader");
        NetworkService.closeSocket(socket);
    }

    public synchronized void addListener(NetworkListener listener) {
        listeners.add(listener);
    }

    private synchronized List<NetworkListener> getListeners() {
        return listeners;
    }
}