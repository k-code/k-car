package pro.kornev.kcar.cop.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.IBinder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import pro.kornev.kcar.cop.State;
import pro.kornev.kcar.cop.providers.LogsDB;
import pro.kornev.kcar.protocol.Data;
import pro.kornev.kcar.protocol.Protocol;

/**
 *
 */
public class VideoService extends Service implements NetworkListener, Camera.PreviewCallback, Camera.ErrorCallback {
    private Camera mCamera;
    private int fps = 1;
    private long lastFrameTime = 0;
    private LogsDB db;
    private boolean isStartPreview = false;
    private Camera.Size size;
    private int previewFormat = ImageFormat.NV21;
    private int quality = 50;
    private CameraPreview cameraPreview = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        stopPreview();
        releaseCamera();
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        db = new LogsDB(getApplicationContext());
        NetworkService.addListener(this);

        initCamera();

        return START_STICKY;
    }

    // Network listener
    @Override
    public void onDataReceived(Data data) {
        if (data.cmd < Protocol.Cmd.copFirst() || data.cmd > Protocol.Cmd.copLast()) {
            return;
        }
        if (data.cmd == Protocol.Cmd.camPreviewState()) {
            if (data.bData == 0) {
                db.putLog("Stop preview");
                stopPreview();
            } else {
                db.putLog("Start preview");
                startPreview();
            }
        } else  if (data.cmd == Protocol.Cmd.camFps()) {
            db.putLog("Set FPS to " + data.bData);
            setFps(data.bData);
        } else  if (data.cmd == Protocol.Cmd.camReset()) {
            db.putLog("Reset camera");
            initCamera();
        } else if (data.cmd == Protocol.Cmd.camQuality()) {
            db.putLog("Set quality to " + data.bData);
            setQuality(data.bData);
        }
    }

    // Camera listener
    @Override
    public void onPreviewFrame(byte[] buf, Camera camera) {
        if (System.currentTimeMillis() - lastFrameTime < 1000 / getFps()) {
            return;
        }
        YuvImage image = new YuvImage(buf, previewFormat, size.width, size.height, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), getQuality(), baos);

        byte[] aData = baos.toByteArray();

        Data data = new Data();
        data.cmd = Protocol.Cmd.camPreviewImg();
        data.type = Protocol.arrayType();
        data.aSize = aData.length;
        data.aData = aData;
        State.getToControlQueue().add(data);
        lastFrameTime = System.currentTimeMillis();
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    private synchronized void startPreview() {
        try {
            if (isStartPreview) return;
            mCamera.startPreview();
            isStartPreview = true;
        } catch (Exception e) {
            db.putLog("Failed start camera preview: " + e.getMessage());
        }
    }

    private synchronized void stopPreview() {
        try {
            if (!isStartPreview) return;
            mCamera.stopPreview();
            isStartPreview = false;
        } catch (Exception e) {
            db.putLog("Failed stop camera preview: " + e.getMessage());
        }
    }

    private void setupCamera(Camera mCamera) {
        Camera.Parameters parameters = mCamera.getParameters();
        /*List<int[]> supportedFps = parameters.getSupportedPreviewFpsRange();
        if (supportedFps != null) {
            int minFps = supportedFps.get(0)[0];
            parameters.setPreviewFpsRange(minFps, minFps);
        }*/
        previewFormat = parameters.getPreviewFormat();
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
    }

    private void initCamera() {
        try {
            if (mCamera != null) {
                stopPreview();
                releaseCamera();
            }
            mCamera = Camera.open();
            setupCamera(mCamera);
            if (cameraPreview == null) {
                cameraPreview = new CameraPreview(getApplicationContext(), mCamera);
            } else {
                cameraPreview.setCamera(mCamera);
            }
            SurfaceTexture surfaceTexture = new SurfaceTexture(0);
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    surfaceTexture.getTransformMatrix(new float[16]);
                }
            });
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreviewCallback(this);
            mCamera.setErrorCallback(this);
        } catch (Exception e) {
            db.putLog("Failed init camera: " +e.getMessage());
            e.printStackTrace();
        }
    }



    private synchronized int getFps() {
        return fps;
    }

    private synchronized void setFps(int fps) {
        this.fps = fps > 0 ? fps : 1;
    }

    private synchronized int getQuality() {
        return quality;
    }

    private synchronized void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public void onError(int error, Camera camera) {
        initCamera();
        startPreview();
        Data data = new Data();
        data.cmd = Protocol.Cmd.error();
        data.bData = Protocol.Cmd.camPreviewImg();
        State.getToControlQueue().add(data);
    }
}
