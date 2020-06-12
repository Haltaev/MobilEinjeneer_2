package ru.mobilengineer;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.cardview.widget.CardView;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
SurfaceHolder holder;
    public CameraView(Context context, Camera camera){
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Camera.Parameters parameters = camera.getParameters();

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}