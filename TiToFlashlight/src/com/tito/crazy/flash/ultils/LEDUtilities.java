package com.tito.crazy.flash.ultils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class LEDUtilities {
	public static int LOW_BRIGHTNESS = 125;// 25
	public static int MEDIUM_BRIGHTNESS = 50;// 75
	public static int HIGH_BRIGHTNESS = 128;// 125

	private static final String PATH_HTC = "/sys/devices/platform/flashlight.0/leds/flashlight/brightness";
	private static final String PATH_SONY_ERICSSON_X10 = "/sys/class/leds/lv5219lg:fled/brightness";

	private String path = null;
	private static LEDUtilities instance;
	public Context context;
	private Boolean isSupported = false;

	public Boolean isSupported() {
		return isSupported;
	}

	public void setIsSupported(Boolean isSupported) {
		this.isSupported = isSupported;
	}

	private LEDUtilities(Context context) {
		this.context = context;
		checkSupported();
	}

	public static LEDUtilities getInstance(Context context) {
		if (instance == null) {
			instance = new LEDUtilities(context);
		}
		return instance;
	}

	public void turnOn() {
		setBrightness(HIGH_BRIGHTNESS);
	}

	public void turnOff() {
		setBrightness(0);
	}

	public void checkSupported() {
		path = PATH_HTC;
		File brightness = new File(path);
		isSupported = brightness.exists() && brightness.canWrite() && !android.os.Build.DEVICE.equals("passion");
		LOW_BRIGHTNESS = 125;// 25
		MEDIUM_BRIGHTNESS = 50;// 75
		HIGH_BRIGHTNESS = 128;// 125
		if (!isSupported) {
			LOW_BRIGHTNESS = 7;
			MEDIUM_BRIGHTNESS = 14;
			HIGH_BRIGHTNESS = 20;
			path = PATH_SONY_ERICSSON_X10;
			brightness = new File(path);
			isSupported = brightness.exists() && brightness.canWrite() && !android.os.Build.DEVICE.equals("passion");
		}

	}

	// /@Ducv
	public void setBrightness(int brightness) {
		if (!isSupported) {
			return;
		} else {
			write(path, brightness);
		}
	}

	private boolean write(String filepath, int brightness) {

		if (brightness < 0 || brightness > 128)
			return false;

		String content = Integer.toString(brightness) + "\n";
		File f = new File(filepath);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(content);
			out.close();
		} catch (IOException ex) {
			Log.w("Flash light", "write ", ex);
			return false;
		}
		return true;
	}
}
