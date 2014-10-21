package com.ducv.fff.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Preference {
	// /////////////////////////


	/*
	 * board type
	 */
	public static void setBoardType(Context paramContext, String paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putString("boardtype", paramBoolean);
		localEditor.commit();
	}

	public static String getBoardType(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getString("boardtype", "1");
	}

	/*
	 * Sound
	 */
	public static void setSound(Context paramContext, boolean paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putBoolean("sound", paramBoolean);
		localEditor.commit();
	}

	public static boolean getSound(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getBoolean("sound", false);
	}

	/*
	 * screen always on
	 */
	public static void setScreenOn(Context paramContext, boolean paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putBoolean("screen_on", paramBoolean);
		localEditor.commit();
	}

	public static boolean getScreenOn(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getBoolean("screen_on", false);
	}

	/*
	 * show CPU thinking
	 */
	public static void setShowCpuThinhing(Context paramContext,
			boolean paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putBoolean("cpu_think", paramBoolean);
		localEditor.commit();
	}

	public static boolean getShowCpuThinhing(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getBoolean("cpu_think", false);
	}

	/*
	 * can undo
	 */
	public static void setCanUndo(Context paramContext,
			boolean paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putBoolean("can_go_back_chooser", paramBoolean);
		localEditor.commit();
	}

	public static boolean getCanUndo(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getBoolean("can_go_back_chooser", false);
	}

	/*
	 * hide Status Bar
	 */
	public static void setStatusBar(Context paramContext, boolean paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putBoolean("status_bar", paramBoolean);
		localEditor.commit();
	}

	public static boolean getStatusBar(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getBoolean("status_bar", false);
	}

	/*
	 * Show legal and last move
	 */
	public static void setShowLegalAndLastMove(Context paramContext,
			boolean paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putBoolean("legal_lastmove", paramBoolean);
		localEditor.commit();
	}

	public static boolean getShowLegalAndLastMove(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getBoolean("legal_lastmove", false);
	}

	/*
	 * Show time
	 */
	public static void setShowTime(Context paramContext, boolean paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putBoolean("time_show", paramBoolean);
		localEditor.commit();
	}

	public static boolean getShowTime(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getBoolean("time_show", false);
	}

	/*
	 * first using
	 */
	public static void setFirstUse(Context paramContext, boolean paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putBoolean("first_use", paramBoolean);
		localEditor.commit();
	}

	public static boolean getFirstUse(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getBoolean("first_use", false);
	}
	/*
	 * Difficult
	 */
	public static void setDifficult(Context paramContext, int paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putInt("game_level_chooser", paramBoolean);
		localEditor.commit();
	}

	public static int getDifficult(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getInt("game_level_chooser", 3);
	}
	
	/*
	 * HandiCrap
	 */
	public static void setHandicrap(Context paramContext, int value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putInt("handicap", value);
		localEditor.commit();
	}

	public static int getHandicrap(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getInt("handicap", 0);
	}
	/*
	 * TotalTime
	 */
	public static void setTotalTime(Context paramContext, int paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putInt("total_time_chooser", paramBoolean);
		localEditor.commit();
	}

	public static int getTotalTime(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getInt("total_time_chooser", 0);
	}
	/*
	 * moveTime
	 */
	public static void setMoveTime(Context paramContext, int paramBoolean) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		SharedPreferences.Editor localEditor = prefs.edit();
		localEditor.putInt("move_time_chooser", paramBoolean);
		localEditor.commit();
	}

	public static int getMoveTime(Context paramContext) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(paramContext);
		return prefs.getInt("move_time_chooser", 0);
	}
}
