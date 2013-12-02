package pro.kornev.kcar.cop.services.support;

import android.os.Process;

/**
 *
 */
public class UncaughtException implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        Process.killProcess(Process.myPid());
        System.exit(10);
    }
}
