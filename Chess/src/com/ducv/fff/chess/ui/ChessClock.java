/**
 * A TextView present a clock for counting the time of a chess.
 * The Default time for every game is 10 minutes ( = 600000 milli seconds)
 * The time is
 */
package com.ducv.fff.chess.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class ChessClock extends TextView {

	private int totalTime = 600000; // in millis ( = 10 minutes)
	private TimeCounter timeCounterThread = null;
	private boolean isTurnOn = false;
	private Handler mHandler;
	public int maxTime = 0;
	private static final int START = 1000;
	private static final int STOP = 1001;
	private static final int RESTART = 1002;
	private static final int UPDATE = 1003;
	private static final int PAUSE = 1004;
	private static final int RESUME = 1005;
	private static final int TEXT_SIZE = 16;
	private Context context;

	public ChessClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ChessClock(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		init();
	}

	public ChessClock(Context context) {
		super(context, null, 0);
		init();
		this.context = context;
	}

	private void init() {
		totalTime = maxTime;
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setTextSize(TEXT_SIZE);
		this.setGravity(Gravity.CENTER);
		this.setText(getTimeText(totalTime));
		this.setTextColor(Color.BLACK);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE:
					if (isTurnOn) {
						ChessClock.this.setText(getTimeText(totalTime));
						totalTime -= 40;
					}
					break;
				}
				super.handleMessage(msg);
			}

		};
	};

	public void start() {
		isTurnOn = true;
	}

	public void pause() {
		isTurnOn = false;
		if (timeCounterThread != null)
			timeCounterThread.interrupt();
		timeCounterThread = null;
	}

	public void resume() {
		isTurnOn = true;
		if (timeCounterThread == null) {
			timeCounterThread = new TimeCounter();
			timeCounterThread.start();
		}
	}

	public void restart() {
		pause();
		totalTime = maxTime;
		ChessClock.this.setText(getTimeText(totalTime));
	}

	public void restart(int time) {
		pause();
		totalTime = time;
		ChessClock.this.setText(getTimeText(totalTime));
	}

	public void setTime(int totalTime) {
		this.totalTime = totalTime;
		ChessClock.this.setText(getTimeText(totalTime));
	}

	public void setMaxTime(int maxTime) {
		// Log.d(TAG, "setMaxTime");
		this.maxTime = maxTime;
	}

	public int getMaxTime() {
		// Log.d(TAG, "setMaxTime");
		return this.maxTime;
	}

	public int getTime() {
		return this.totalTime;
	}

	// public static int getMaxTime() {
	// return maxTime;
	// }

	public void updateToMaxTime() {
		totalTime = maxTime;
		this.setText(getTimeText(totalTime));
	}

	public String getTimeText(int totalTime) {
		int minutes = 10;
		int seconds = 0;
		minutes = totalTime / 60000;
		seconds = (totalTime - minutes * 60000) / 1000;
		return String.format("%02d:%02d", minutes, seconds);
	}

	public class TimeCounter extends Thread {
		@Override
		public void run() {
			while (isTurnOn && totalTime >= 0) {
				Message msg = new Message();
				msg.what = UPDATE;
				mHandler.sendMessage(msg);
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					this.interrupt();
				}
			}
		}

	}
}