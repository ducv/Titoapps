package com.tito.crazy.flash.ultils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class CameraUltils {
	private static CameraUltils instance;
	public Context context;
	private boolean isFlashOn;
	private boolean hasFlash;
	private Parameters params;
	private Camera camera;

	private CameraUltils(Context context) {
		this.context = context;
		checkCamera();
		getCamera();
	}

	public static CameraUltils getInstance(Context context) {
		if (instance == null) {
			instance = new CameraUltils(context);
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
	 * Get the camera
	 */
	public void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (Exception e) {
			}
		}
	}

	/*
	 * Turning On flash
	 */
	public void turnOnFlash() {
		if (!isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			// play sound
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isFlashOn = true;

			// changing button/switch image
		}

	}

	/*
	 * Turning Off flash
	 */
	public void turnOffFlash() {
		if (isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;
		}
	}

	public void release() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}
}
