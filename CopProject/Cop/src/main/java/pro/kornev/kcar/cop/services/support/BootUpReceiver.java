package pro.kornev.kcar.cop.services.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.cop.services.CopService;

/**
 *
 */
public final class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new LogsDB(context).putLog("BU Start WakeUp service");
        context.startService(new Intent(context, WakeUpService.class));
    }
}
