package com.ducv.fff.chess;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.WindowManager;

import com.ducv.fff.preference.Preference;

public class ChessPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		addPreferencesFromResource(R.xml.preferences);
		// if (!Preference.getStatusBar(this)) {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// }
		// CheckBoxPreference showTitleBar = (CheckBoxPreference)
		// findPreference("status_bar");
		// showTitleBar
		// .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		//
		// public boolean onPreferenceChange(
		// android.preference.Preference preference,
		// Object newValue) {
		// // TODO Auto-generated method stub
		// if (newValue.equals("false")) {
		// ChessPreferences.this.getWindow().setFlags(
		// WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// return false;
		// } else {
		// ChessPreferences.this.getWindow().clearFlags(
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// return true;
		// }
		//
		// }
		// });
	}
}
