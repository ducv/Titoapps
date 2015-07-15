package com.tito.crazy.flash.ultils;

import android.content.Context;
import android.content.SharedPreferences;

public final class ManagePreferences {

	public static final String PREFERENT_NAME = "tito_flashlight";
	public static final String SOUND = "sound";
	public static final String LIGHT_STATUS = "LIGHT_STATUS";

	// /////////////////////////
	public static void setSound(Context context, boolean value) {
		putBooleanValue(SOUND, value, context);
	}

	public static boolean getSound(Context context) {
		return getBooleanValue(SOUND, context);
	}

	// /////////////////////////
	public static void setLightStatus(Context context, boolean value) {
		putBooleanValue(LIGHT_STATUS, value, context);
	}

	public static boolean getLightStatus(Context context) {
		return getBooleanValue(LIGHT_STATUS, context);
	}

	/**
	 * base function to get/set data from database
	 * 
	 * @param key
	 * @param s
	 * @param context
	 */
	public static void putStringValue(String key, String s, Context context) {
		// SmartLog.log(TAG, "Set string value");
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, s);
		editor.commit();
	}

	public static void putFloatValue(String key, Float s, Context context) {
		// SmartLog.log(TAG, "Set string value");
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putFloat(key, s);
		editor.commit();
	}

	public static void putLongValue(String key, long l, Context context) {
		// SmartLog.log(TAG, "Set string value");
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(key, l);
		editor.commit();
	}

	public static Long getLongValue(String key, Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		return pref.getLong(key, 0);
	}

	public static void putIntValue(String key, int value, Context context) {
		// SmartLog.log(TAG, "Set string value");
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void putBooleanValue(String key, Boolean s, Context context) {
		// SmartLog.log(TAG, "Set string value");
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(key, s);
		editor.commit();
	}

	/**
	 * Read an string to SharedPreferences
	 * 
	 * @param key
	 * @return
	 */
	public static String getStringValue(String key, Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		return pref.getString(key, "");
	}

	public static Float getFloatValue(String key, Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		return pref.getFloat(key, (float) 0);
	}

	public static int getIntValue(String key, Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, -1);
		return pref.getInt(key, -1);
	}

	public static boolean getBooleanValue(String key, Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENT_NAME, 0);
		return pref.getBoolean(key, false);
	}
}
