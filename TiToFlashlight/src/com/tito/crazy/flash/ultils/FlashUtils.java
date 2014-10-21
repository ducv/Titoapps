package com.tito.crazy.flash.ultils;

import android.content.Context;

public class FlashUtils {
	public static final int LEVEL_0 = 0;
	public static final int LEVEL_1 = 1;
	public static final int LEVEL_2 = 2;
	public static final int LEVEL_3 = 3;
	private LEDUtilities ledUtilities;
	private CameraUltils cameraUltils;
	public Context context;
	private boolean active = false;
	private int level = LEVEL_0;
	private SoundUltils soundUltils;
	private boolean status = false;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public FlashUtils(Context context) {
		this.context = context;
		soundUltils = SoundUltils.getInstance(context);
		active = false;
		status = true;
	}


	public void init() {
		ledUtilities = LEDUtilities.getInstance(context);
		if (!ledUtilities.isSupported()) {
			cameraUltils = CameraUltils.getInstance(context);
		}
	}

	public void adjustBrightness() {
		if (ledUtilities.isSupported()) {
			if (level < LEVEL_3) {
				level++;
				soundUltils.playSound(true);
				turnOn();
				active = true;
				status = true;
			} else {
				level = LEVEL_0;
				turnOff();
				soundUltils.playSound(false);
				active = false;
				status = false;
			}
		} else {
			if (active) {
				active = false;
				cameraUltils.turnOffFlash();
				soundUltils.playSound(false);
				level = LEVEL_0;
				status = false;
			} else {
				active = true;
				soundUltils.playSound(true);
				cameraUltils.turnOnFlash();
				level = LEVEL_3;
				status = true;
			}
		}
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}


	public void turnOn() {
		if (ledUtilities.isSupported()) {
			switch (level) {
			case LEVEL_1:
				ledUtilities.setBrightness(LEDUtilities.LOW_BRIGHTNESS);
				break;
			case LEVEL_2:
				ledUtilities.setBrightness(LEDUtilities.MEDIUM_BRIGHTNESS);
				break;
			case LEVEL_3:
				ledUtilities.setBrightness(LEDUtilities.HIGH_BRIGHTNESS);
				break;
			default:
				break;
			}

		} else {
			cameraUltils.turnOnFlash();
		}
		status = true;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void turnOff() {
		if (ledUtilities.isSupported()) {
			ledUtilities.turnOff();
		} else {
			cameraUltils.turnOffFlash();
		}
		status = false;
	}

	public void release() {
		if (ledUtilities.isSupported()) {
			return;
		} else {
			cameraUltils.release();
		}
	}

	public void onStart() {
		if (!ledUtilities.isSupported()) {
			cameraUltils.getCamera();
		}
	}

	public boolean getLedSupported() {
		return ledUtilities.isSupported();
	}
}
