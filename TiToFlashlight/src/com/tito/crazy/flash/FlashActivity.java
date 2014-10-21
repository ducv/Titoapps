package com.tito.crazy.flash;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tito.crazy.flash.component.Battery;
import com.tito.crazy.flash.component.BrightnessLevel;
import com.tito.crazy.flash.component.StrokeWheel;
import com.tito.crazy.flash.component.TextViewCustom;
import com.tito.crazy.flash.listener.StrokeIndexListener;
import com.tito.crazy.flash.ultils.FlashUtils;
import com.tito.crazy.flash.ultils.ManagePreferences;

public class FlashActivity extends Activity implements StrokeIndexListener {

	private FlashUtils flashUtils;
	private Button powerBt;
	private StrokeWheel strokeWheel;
	private TextViewCustom strokeTv;
	private final int TIME_DELAY = 200;
	private int count = 0;
	private int strobe = 0;
	private BrightnessLevel brightnessLevel;
	private Battery battery;
	private ToggleButton soundTg;
	private Button moreBt;
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash);
		init();
		initControl();
		batteryLevel();
		if (bundle != null) {
			flashUtils.adjustBrightness();
			if (!flashUtils.isStatus()) {
				brightnessLevel.setLevel(0);
			} else {
				brightnessLevel.setLevel(flashUtils.getLevel());
			}
		}
		AdView adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	/**
	 * Computes the battery level by registering a receiver to the intent
	 * triggered by a battery status/level change.
	 */
	private void batteryLevel() {
		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				int __rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int __scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				int __level = -1;
				if (__rawlevel >= 0 && __scale > 0) {
					__level = (__rawlevel * 100) / __scale;
				}
				battery.updateLevel(__level);
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	private void initControl() {
		powerBt = (Button) findViewById(R.id.myButtonPower);
		powerBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				count = 0;
				flashUtils.adjustBrightness();
				if (!flashUtils.isStatus()) {
					brightnessLevel.setLevel(0);
				} else {
					brightnessLevel.setLevel(flashUtils.getLevel());
				}
			}
		});
		strokeTv = (TextViewCustom) findViewById(R.id.myTextViewStroke);
		strokeWheel = (StrokeWheel) findViewById(R.id.myStrokeWheel);
		strokeWheel.setIndexListener(this);
		brightnessLevel = (BrightnessLevel) findViewById(R.id.myBrightnessLevel);
		battery = (Battery) findViewById(R.id.myBattery);
		soundTg = (ToggleButton) findViewById(R.id.myToggleButtonSound);
		soundTg.setChecked(ManagePreferences.getSound(this));
		soundTg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ManagePreferences.setSound(FlashActivity.this, isChecked);
			}
		});
		moreBt = (Button) findViewById(R.id.myButtonMore);
		moreBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openHomePage();
			}
		});
	}

	private void init() {
		count = 0;
		flashUtils = new FlashUtils(this);
		flashUtils.init();
		bundle = getIntent().getExtras();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		flashUtils.release();
	}

	@Override
	protected void onPause() {
		flashUtils.turnOff();
		updateTime.removeCallbacks(timeRunable);
		super.onPause();
	}

	@Override
	protected void onResume() {
		flashUtils.onStart();
		updateTime.postDelayed(timeRunable, TIME_DELAY);
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onIndex(int index) {
		strokeTv.setText("" + index);
		strobe = index;
	}

	private Handler updateTime = new Handler();
	private Runnable timeRunable = new Runnable() {
		@Override
		public void run() {
			updateTime.postDelayed(timeRunable, TIME_DELAY);
			count++;
			if (flashUtils.isActive()) {
				if (strobe == 0) {
					flashUtils.turnOn();
					brightnessLevel.setLevel(flashUtils.getLevel());
				} else {
					if (count >= (10 - strobe)) {
						if (flashUtils.isStatus()) {
							flashUtils.turnOff();
							brightnessLevel.setLevel(0);
						} else {
							flashUtils.turnOn();
							brightnessLevel.setLevel(flashUtils.getLevel());
						}
						count = 0;
					}

				}

			} else {
				brightnessLevel.setLevel(flashUtils.getLevel());
			}

		}
	};

	private void openHomePage() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(getString(R.string.home_page)));
		startActivity(intent);
	}

}
