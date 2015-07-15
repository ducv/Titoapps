package com.tito.crazy.flash.ultils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraUltils {
	private static CameraUltils instance;
	public Context context;
	private boolean isFlashOn;
	private boolean hasFlash;
	private Parameters params;
	private Camera camera;
    Camera.Parameters parameters;
    SurfaceHolder mHolder;

	private CameraUltils(Context context,SurfaceView surfaceView ) {
		this.context = context;
        mHolder = surfaceView.getHolder();
		checkCamera();
	}

	public static CameraUltils getInstance(Context context,SurfaceView surfaceView) {
		if (instance == null) {
			instance = new CameraUltils(context,surfaceView);
		}
		return instance;
	}

	private void checkCamera() {
		hasFlash = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}

	public boolean isFlashOn() {
		return isFlashOn;
	}

	public void setFlashOn(boolean isFlashOn) {
		this.isFlashOn = isFlashOn;
	}

	public boolean isHasFlash() {
		return hasFlash;
	}

	public void setHasFlash(boolean hasFlash) {
		this.hasFlash = hasFlash;
	}


	/*
	 * Turning On flash
	 */
	public void turnOnFlash() {
//		if (!isFlashOn) {
            // Switch on the cam for app's life
            if (camera == null) {
                // Turn on Cam
                camera = Camera.open();
                try {
                    camera.setPreviewDisplay(mHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }
            // Turn on LED
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
//            camera.startPreview();
			isFlashOn = true;
			// changing button/switch image
//		}

	}

	/*
	 * Turning Off flash
	 */
	public void turnOffFlash() {
//		if (isFlashOn) {
            // Turn off flashlight
            if (camera == null) {
                camera = Camera.open();
                try {
                    camera.setPreviewDisplay(mHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }
            parameters = camera.getParameters();
            if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
//                camera.stopPreview();
            }
            isFlashOn = false;
//		}
	}

	public void release() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}
}
