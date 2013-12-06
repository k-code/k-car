package pro.kornev.kcar.cop.services.support;

import android.os.Process;

/**
 *
 */
public final class ProcessKillerException implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        Process.killProcess(Process.myPid());
        System.exit(10);
    }
}
