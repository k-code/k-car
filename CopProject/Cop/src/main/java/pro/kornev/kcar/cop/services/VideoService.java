package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import pro.kornev.kcar.cop.R;
import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.protocol.Data;

/**
 *
 */
public class VideoService extends Service {
    private Camera mCamera;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mCamera = getCameraInstance();

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFpsRange(1, 2);
        parameters.getSupportedPreviewFpsRange();
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        if (sizes == null || sizes.isEmpty()) {
            return START_NOT_STICKY;
        }
        Camera.Size minSize = sizes.get(0);
        for (Camera.Size size: sizes) {
            if (minSize.width > size.width) {
                minSize = size;
            }
        }
        parameters.setPreviewSize(minSize.width, minSize.height);
        mCamera.setParameters(parameters);

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] buf, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();
                if (size == null) return;

                YuvImage image = new YuvImage(buf, parameters.getPreviewFormat(),
                        size.width, size.height, null);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, baos);

                byte[] aData = baos.toByteArray();

                Data data = new Data();
                data.id = 3;
                data.cmd = 5;
                data.type = 2;
                data.aSize = aData.length;
                data.aData = aData;
                State.getToControlQueue().add(data);
            }
        });

        try {
            mCamera.setPreviewDisplay(new SurfaceView(getApplicationContext()).getHolder());
            mCamera.setPreviewTexture(new SurfaceTexture(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
        return START_STICKY;
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        releaseCamera();
        mCamera.stopPreview();
        return true;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
