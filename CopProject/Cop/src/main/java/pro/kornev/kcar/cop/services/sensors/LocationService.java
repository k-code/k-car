package pro.kornev.kcar.cop.services.sensors;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.nio.ByteBuffer;

import pro.kornev.kcar.cop.services.CopService;
import pro.kornev.kcar.cop.services.network.NetworkListener;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 *
 */
public final class LocationService implements NetworkListener, LocationListener {
    private final LocationManager locationManager;
    private final CopService copService;
    private volatile double latitude = 0;
    private volatile double longitude = 0;
    private volatile String locationProvider = LocationManager.NETWORK_PROVIDER;

    public LocationService(CopService copService) {
        this.copService = copService;
        locationManager= (LocationManager) copService.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onDataReceived(Data data) {
        if (data.cmd == Protocol.Cmd.sensGps()) {
            Message msg = new Message();
            msg.arg1 = data.bData;
            handler.sendMessage(msg);
        } else if (data.cmd == Protocol.Cmd.sensLocation()) {
            ByteBuffer bb = ByteBuffer.allocate(Double.SIZE / 8 * 2);
            synchronized (this) {
                bb.putDouble(latitude);
                bb.putDouble(longitude);
            }
            byte[] buf = bb.array();
            data.type = Protocol.arrayType();
            data.aSize = buf.length;
            data.aData = buf;
            write(data);
        }
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void start() {
        locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
    }

    public void stop() {
        locationManager.removeUpdates(this);
    }

    private void write (Data data) {
        if (copService.getNetworkService() == null) {
            return;
        }
        copService.getNetworkService().write(data);
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            stop();
            if (msg.arg1==0) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                locationProvider = LocationManager.GPS_PROVIDER;
            }
            start();
            return false;
        }
    });
}
