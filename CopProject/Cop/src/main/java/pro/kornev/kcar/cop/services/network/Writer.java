package pro.kornev.kcar.cop.services.network;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

final class Writer {
    private final Socket socket;
    private final LogsDB log;
    private final DataOutputStream output;

    private int id = 0;

    public Writer(Context context, Socket socket) throws IOException {
        this.socket = socket;
        log = new LogsDB(context);
        log.putLog("NW Create network writer");
        output = new DataOutputStream(socket.getOutputStream());
    }

    public synchronized void write(Data data) {
        try {
            if (socket != null && !socket.isClosed()) {
                data.id = id++;
                log.putLog(String.format("NW write data id: %d; cmd: %d", data.id, data.cmd));
                Protocol.toOutputStream(data, output);
            } else {
                log.putLog(String.format("NW skip data cmd: %d", data.cmd));
            }
        } catch (Throwable e) {
            log.putLog("NW error: " + e.getMessage());
        }
    }
}
