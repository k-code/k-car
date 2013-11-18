package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.IBinder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;

/**
 *
 */
public class VideoService extends Service implements NetworkListener, Camera.PreviewCallback {
    private Camera mCamera;
    private int fps = 1;
    private long lastFrameTime = 0;
    private LogsDB db;
    private boolean isStartPreview = false;
    private CameraPreview surfaceView;
    private Camera.Size size;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        db = new LogsDB(getApplicationContext());
        NetworkService.addListener(this);
        surfaceView = new CameraPreview(getApplicationContext());

        initCamera();

        return START_STICKY;
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        stopPreview();
        releaseCamera();
        return true;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception ignored){
        }
        return c;
    }

    private synchronized void startPreview() {
        if (isStartPreview) return;
        mCamera.startPreview();
        isStartPreview = true;
    }

    private synchronized void stopPreview() {
        if (!isStartPreview) return;
        mCamera.stopPreview();
        isStartPreview = false;
    }

    @Override
    public void onDataReceived(Data data) {
        if (data.cmd == 6) {
            if (data.bData == 0) {
                db.putLog("Stop preview");
                stopPreview();
            }
            else {
                db.putLog("Start preview");
                startPreview();
            }
        } else  if (data.cmd == 7) {
            db.putLog("Set FPS to " + data.bData);
            setFps(data.bData);
        } else  if (data.cmd == 8) {
            db.putLog("Reset camera");
            initCamera();
        }
    }

    @Override
    public void onPreviewFrame(byte[] buf, Camera camera) {
        if (System.currentTimeMillis() - lastFrameTime < 1000 / getFps()) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        if (size == null) return;

        YuvImage image = new YuvImage(buf, parameters.getPreviewFormat(), size.width, size.height, null);

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
        lastFrameTime = System.currentTimeMillis();
    }

    private void setupCamera(Camera mCamera) {
        Camera.Parameters parameters = mCamera.getParameters();
        /*List<int[]> supportedFps = parameters.getSupportedPreviewFpsRange();
        if (supportedFps != null) {
            int minFps = supportedFps.get(0)[0];
            parameters.setPreviewFpsRange(minFps, minFps);
        }*/
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        if (sizes != null && !sizes.isEmpty()) {
            size = sizes.get(0);
            for (Camera.Size s: sizes) {
                if (size.width > s.width) {
                    size = s;
                }
            }
            parameters.setPreviewSize(size.width, size.height);
        }
        mCamera.setParameters(parameters);

        try {
            surfaceView.setCamera(mCamera);
            SurfaceTexture surfaceTexture = new SurfaceTexture(10);
            mCamera.setPreviewTexture(surfaceTexture);
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    surfaceTexture.getTransformMatrix(new float[16]);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized int getFps() {
        return fps;
    }

    private synchronized void setFps(int fps) {
        this.fps = fps;
    }

    private void initCamera() {
        if (mCamera != null) {
            stopPreview();
            releaseCamera();
        }
        mCamera = getCameraInstance();
        setupCamera(mCamera);
        mCamera.setPreviewCallback(this);
    }
}
