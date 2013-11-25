package pro.kornev.kcar.cop.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, CopService.class);
        context.startService(serviceIntent);
    }

}
