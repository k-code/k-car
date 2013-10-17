package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.providers.LogsDB;

/**
 * @author vkornev
 * @since 17.10.13
 */
public class NetworkService extends Service {
    private LogsDB db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new LogsDB(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Network service starting", Toast.LENGTH_SHORT).show();
        try {
            Socket s = new Socket("kornev.pro", 7850);
            Writer l = new Writer(s);
            new Thread(l).start();
            Reader r = new Reader(s);
            new Thread(r).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    class Reader implements Runnable {
        Socket client;
        Reader(Socket s) {
            client = s;
        }

        @Override
        public void run() {
            while (State.isServiceRunning()) {
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String answer = input.readLine();
                    db.putLog("NR: " + answer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Writer implements Runnable {
        Socket client;
        Writer(Socket s) {
                client = s;
        }

        @Override
        public void run() {
            while (State.isServiceRunning()) {
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String answer = input.readLine();
                    db.putLog("NR: " + answer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Network service done", Toast.LENGTH_SHORT).show();
        State.setServiceRunning(false);
    }
}
