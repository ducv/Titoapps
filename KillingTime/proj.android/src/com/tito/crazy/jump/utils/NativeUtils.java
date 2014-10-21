package com.tito.crazy.jump.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.cocos2dx.lib.Cocos2dxHelper;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.games.Games;
import com.tito.crazy.jump.JumpFace;
import com.tito.crazy.jump.R;

/**
 * 
 * @author Carlos Eduardo Piñan Indacochea
 * @version 1.0.0
 * @date 2014/02/25
 * @update 2014/02/25
 * @reference http://www.cocos2d-x.org/forums/6/topics/28296?r=28417
 * @description Class to support Google Play Game Services and Ouya cocos2d-x
 *              integration.
 */
public class NativeUtils {

	// Important variables
	private static Context context = null;
	private static JumpFace app = null;
	private static NativeUtils instance = null;

	// ID's
	private static final int REQUEST_ACHIEVEMENTS = 10000;
	private static final int REQUEST_LEADERBOARDS = 10001;
	private static final int REQUEST_LEADERBOARD = 10002;

	// TAG
	private static final String TAG = "ANDROID_TAG";

	/**
	 * Singleton
	 */
	public static NativeUtils sharedInstance() {
		if (instance == null)
			instance = new NativeUtils();
		return instance;
	}

	/**
	 * Configura los datos iniciales para comunicar los eventos de aquí a
	 * cocos2d-x.
	 */
	public static void configure(Context context) {
		NativeUtils.context = context;
		NativeUtils.app = (JumpFace) NativeUtils.context;
	}

	/**
	 * 
	 * @param message
	 */
	public static void displayAlert(final String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setNeutralButton(context.getResources().getString(android.R.string.ok), null);
		builder.create().show();
	}

	/*
	 * Google play games services methods. Requirements:
	 * google-play-services_lib as library.
	 */

	/**
	 * Check if user is Signed In.
	 */
	public static boolean isSignedIn() {
		return app.getSignedIn();
	}

	/**
	 * Google Play Games Services Sign In
	 */
	public static void gameServicesSignIn() {
		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (!isSignedIn())
					app.signInGooglePlay();
			}
		});
	}

	/**
	 * Google Play Games Services Sign Out
	 */
	public static void gameServicesSignOut() {
		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn())
					app.signOutGooglePlay();
			}
		});
	}

	/**
	 * Submit a score in a leaderboard.
	 * 
	 * @param leaderboardID
	 * @param score
	 */
	public static void submitScore(final String leaderboardID, final long score) {
		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn()) {
					Log.d(TAG, "Submit score " + score + " to " + leaderboardID);

					Games.Leaderboards.submitScore(app.getCustomApiClient(), leaderboardID, score);

				} else {
					String message = context.getResources().getString(R.string.fail_submit_score_leaderboard);
					message = message.replace("{score}", score + "");
					message = message.replace("{leaderboardID}", leaderboardID);
					displayAlert(message);
				}
			}
		});

	}

	/**
	 * Unlock an achievement.
	 * 
	 * @param achievementId
	 */
	public static void unlockAchievement(final String achievementID) {
		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn()) {
					Log.d(TAG, "Try to unlock achievement " + achievementID);
					Games.Achievements.unlock(app.getCustomApiClient(), achievementID);
				} else {
					String message = context.getResources().getString(R.string.fail_unlock_achievement);
					message = message.replace("{achievementID}", achievementID);
					displayAlert(message);
				}
			}
		});
	}

	/**
	 * Increment the achievement in numSteps.
	 * 
	 * @param achievementId
	 * @param numSteps
	 */
	public static void incrementAchievement(final String achievementID, final int numSteps) {
		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn())
					Games.Achievements.increment(app.getCustomApiClient(), achievementID, numSteps);
				else {
					String message = context.getResources().getString(R.string.fail_increment_achievement);
					message = message.replace("{achievementID}", achievementID);
					message = message.replace("{numSteps}", numSteps + "");
					displayAlert(message);
				}
			}
		});
	}

	/**
	 * Show all achievements.
	 */
	public static void showAchievements() {
		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn())
					app.startActivityForResult(Games.Achievements.getAchievementsIntent(app.getCustomApiClient()), REQUEST_ACHIEVEMENTS);
				else {
					String message = context.getResources().getString(R.string.fail_show_achievements);
					displayAlert(message);
				}

			}
		});

	}

	/**
	 * Show all leaderboard.
	 */
	public static void showLeaderboards() {

		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn())
					app.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(app.getCustomApiClient()), REQUEST_LEADERBOARDS);
				else {
					String message = context.getResources().getString(R.string.fail_show_leaderboards);
					displayAlert(message);
				}
			}
		});

	}

	/**
	 * Show single leaderboard.
	 */
	public static void showLeaderboard(final String leaderboardID) {

		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn())
					app.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(app.getCustomApiClient(), leaderboardID),
							REQUEST_LEADERBOARD);
				else {
					String message = context.getResources().getString(R.string.fail_show_leaderboard);
					message = message.replace("{leaderboardID}", leaderboardID);
					displayAlert(message);
				}
			}
		});

	}

	/**
	 * 
	 * @param key
	 * @param app_state
	 */
	public static void inCloudSaveOrUpdate(final int key, final byte[] app_state) {

		if (!ConfigUtils.GOOGLE_PLAY_IN_CLOUD_SAVE)
			return;

		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn())
					app.inCloudSaveOrUpdate(key, app_state);
				else {
					String message = context.getResources().getString(R.string.gamehelper_alert);
					displayAlert(message);
				}
			}
		});
	}

	/**
	 * 
	 * @param key
	 */
	public static void inCloudLoad(final int key) {

		if (!ConfigUtils.GOOGLE_PLAY_IN_CLOUD_SAVE)
			return;

		app.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isSignedIn())
					app.inCloudLoad(key);
				else {
					String message = context.getResources().getString(R.string.gamehelper_alert);
					displayAlert(message);
				}
			}
		});
	}

	// AdMob

	/*
	 * Show and Hide AdView
	 */

	public static void showAd() {
		app.showAd();
	}

	public static void hideAd() {
		app.hideAd();
	}

	/*
	 * Ouya support Note: For Ouya support need uncomment the <category
	 * android:name="tv.ouya.intent.category.GAME" /> in the
	 * AndroidManifest.xml
	 */

	/*
	 * Native call
	 */

	public static native void notifyInCloudSavingOrUpdate();

	public static native void notifyInCloudLoad();

	public static void screenCapture() {
		Bitmap imageBitmap = BitmapFactory.decodeFile(Cocos2dxHelper.getCocos2dxWritablePath() + "/" + "screenshot.png");

		String fileHolder = "KillingTime";
		File filepathData = new File("/sdcard/" + fileHolder);

		// ~~~Create Dir
		try {
			if (!filepathData.exists()) {
				filepathData.mkdirs();
				filepathData.createNewFile();

				FileWriter fw = new FileWriter(filepathData + fileHolder);
				BufferedWriter out = new BufferedWriter(fw);
				String toSave = String.valueOf(0);
				out.write(toSave);
				out.close();
			}
		} catch (IOException e1) {

		}

		// ~~~Create Image
		File file = new File("/sdcard/" + "screenshot.png");

		try {
			file.createNewFile();
			FileOutputStream ostream = new FileOutputStream(file);
			imageBitmap.compress(CompressFormat.PNG, 100, ostream);
			ostream.close();
		} catch (Exception e) {
		}
		Uri phototUri = Uri.fromFile(file);
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);
		// ~~~Add Code Below
	}

	public static void exitConfirm() {
		app.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder confirmDlg = new Builder(context);
				confirmDlg.setTitle(R.string.exit);
				confirmDlg.setMessage(R.string.confirm_question);
				confirmDlg.setNegativeButton(R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				confirmDlg.setPositiveButton(R.string.yes, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						app.finish();
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				});
				confirmDlg.show();
			}
		});

	}
}
