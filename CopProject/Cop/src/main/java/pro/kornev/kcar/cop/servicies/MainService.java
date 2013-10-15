package pro.kornev.kcar.cop.servicies;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author vkornev
 * @since 14.10.13
 */
public class MainService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
