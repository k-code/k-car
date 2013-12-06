package pro.kornev.kcar.cop.services.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import pro.kornev.kcar.cop.providers.LogsDB;

/**
 *
 */
public final class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;
    private final LogsDB log;

    public CameraPreview(Context context) {
        super(context);
        log = new LogsDB(context);
        holder = getHolder();
        if (holder != null) {
            holder.addCallback(this);
        }
        log.putLog("VP Created");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log.putLog("VP Surface created");
        try {
            setPreview(camera, holder);
            camera.startPreview();
        } catch (Exception ignored) {}
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        log.putLog("VP Surface destroyed");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        log.putLog("VP Surface changed");

        if (holder.getSurface() == null){
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception ignored) {}

        try {
            setPreview(camera, holder);
            camera.startPreview();
        } catch (Exception e){
            log.putLog("VP Error starting camera preview: " + e.getMessage());
        }
    }

    public void setCamera(Camera camera) throws IOException {
        this.camera = camera;
        setPreview(this.camera, holder);
    }

    private void setPreview(Camera camera, SurfaceHolder holder) {
        log.putLog("VP Reset preview");
        try {
            if (holder != null) {
                this.camera.setPreviewDisplay(holder);
            }
            camera.setPreviewTexture(new SurfaceTexture(0));
        } catch (Exception e) {
            log.putLog("VP Reset preview failed: " + e.getMessage());
        }
    }
}