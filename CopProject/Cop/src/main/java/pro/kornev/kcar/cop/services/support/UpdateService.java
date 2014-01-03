package pro.kornev.kcar.cop.services.support;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.cop.services.CopService;
import pro.kornev.kcar.cop.services.CustomService;
import pro.kornev.kcar.protocol.Data;

/**
 *
 */
public class UpdateService implements CustomService, Runnable {
    private static final int DELAY = 3600;
    private static final int VERSION = 0;
    private static final String UPDATE_URL = "http://kornev.pro/cop.apk";
    private static final String VERSION_URL = "http://kornev.pro/cop_version.txt";
    private static final String INSTALL_CMD = "pm install -r";
    private static final String LAUNCH_CMD = "am start -n 'pro.kornev.kcar.cop/pro.kornev.kcar.cop.activities.MainActivity'" +
            " -a android.intent.action.MAIN -c android.intent.category.LAUNCHER";

    private final LogsDB log;
    private volatile ScheduledExecutorService executorService;
    private volatile boolean running = false;

    public UpdateService(CopService copService) {
        log = new LogsDB(copService);
    }

    @Override
    public boolean start() {
        if (running) return false;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this, 2, DELAY, TimeUnit.SECONDS);
        running = true;
        log.putLog("UP Run");
        return true;
    }

    @Override
    public boolean stop() {
        if (!running) return false;
        executorService.shutdown();
        running = false;
        log.putLog("UP Stopped");
        return false;
    }

    @Override
    public void onDataReceived(Data data) {

    }

    @Override
    public void run() {
        log.putLog("UP Check version");
        if (VERSION < getActualVersion()) {
            update();
        }
    }

    private int getActualVersion() {
        try {
            URL url = new URL(VERSION_URL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            InputStream is = c.getInputStream();
            int version = is.read() - '0';
            log.putLog(String.format("UP Versions cur: %d; act: %d", VERSION, version));
            return version;
        } catch (Exception e) {
            log.putLog("UP Failed get version: " + e.getMessage());
            return Integer.MAX_VALUE;
        }
    }

    private void update() {
        try {
            log.putLog("UP Update starting...");
            log.putLog("UP Get package: " + UPDATE_URL);
            URL url = new URL(UPDATE_URL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            String PATH = Environment.getExternalStorageDirectory() + "/Download";
            File file = new File(PATH);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    log.putLog("UP Failed make upload dir: " + file.getAbsolutePath());
                    return;
                }
            }
            File outputFile = new File(file, "/cop.apk");
            if(outputFile.exists()){
                if (!outputFile.delete()) {
                    log.putLog("UP Failed remove file: " + outputFile.getAbsolutePath());
                }
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            log.putLog("UP Upload is successful");

            String path = outputFile.getAbsolutePath();

            if (ShellInterface.isSuAvailable()) {
                log.putLog("UP Run update and restart commands");
                ShellInterface.runCommand(String.format("%s %s && %s", INSTALL_CMD, path, LAUNCH_CMD));
            } else {
                log.putLog("UP Failed get root privileges");
            }
        } catch (Exception e) {
            log.putLog("Update error! " + e.getMessage());
        }
    }
}
