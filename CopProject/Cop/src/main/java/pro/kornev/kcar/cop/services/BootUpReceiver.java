package pro.kornev.kcar.cop.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pro.kornev.kcar.cop.providers.LogsDB;

/**
 *
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new LogsDB(context).putLog("BU Start COP service");
        Intent serviceIntent = new Intent(context, CopService.class);
        context.startService(serviceIntent);
    }

}
