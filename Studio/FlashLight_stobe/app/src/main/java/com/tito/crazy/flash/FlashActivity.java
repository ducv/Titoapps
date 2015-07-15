package com.tito.crazy.flash;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
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
import com.tito.crazy.flash.service.ServiceCamera;
import com.tito.crazy.flash.ultils.FlashUtils;
import com.tito.crazy.flash.ultils.ManagePreferences;
import com.tito.crazy.flash.widget.TitoFlashLightWidgetProvider;

public class FlashActivity extends Activity implements StrokeIndexListener {

    //	private FlashUtils flashUtils;
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

    private SurfaceView surfaceView;
    private boolean isStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        initControl();
        AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(bateryBroadcastReceiver, batteryLevelFilter);
    }

    private BroadcastReceiver bateryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int __rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int __scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int __level = -1;
            if (__rawlevel >= 0 && __scale > 0) {
                __level = (__rawlevel * 100) / __scale;
            }
            battery.updateLevel(__level);
        }
    };

    private void initControl() {
        isStatus = ManagePreferences.getLightStatus(this);
        powerBt = (Button) findViewById(R.id.myButtonPower);
        powerBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                if (isStatus) {
                    brightnessLevel.setLevel(0);
                    isStatus = false;
                } else {
                    brightnessLevel.setLevel(FlashUtils.LEVEL_3);
                    isStatus = true;
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

    private void sendBroadCastToCameraService(boolean status) {
        Intent intent = new Intent(TitoFlashLightWidgetProvider.ACTION_BROADCAST);
        Bundle bundle = new Bundle();
        bundle.putBoolean("WIDGET", false);
        bundle.putBoolean("STATUS", status);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateTime.removeCallbacks(timeRunable);
        updateTime = null;
        unregisterReceiver(bateryBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendBroadCastToCameraService(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
//        updateTime.postDelayed(timeRunable, TIME_DELAY);
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
            if (strobe == 0) {
                if (isStatus) {
                    sendBroadCastToCameraService(true);
                    brightnessLevel.setLevel(FlashUtils.LEVEL_3);
                } else {
                    sendBroadCastToCameraService(false);
                    brightnessLevel.setLevel(0);
                }
            } else {
                if (count >= (10 - strobe)) {
                    if (ManagePreferences.getLightStatus(FlashActivity.this)) {
                        sendBroadCastToCameraService(false);
                        brightnessLevel.setLevel(0);
                    } else {
                        sendBroadCastToCameraService(true);
                        brightnessLevel.setLevel(FlashUtils.LEVEL_3);
                    }
                    count = 0;
                }

            }

        }
    };

    private void openHomePage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.home_page)));
        startActivity(intent);
    }

//    private void updateWidget() {
//        Intent intent = new Intent(this, TitoFlashLightWidgetProvider.class);
//        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), TitoFlashLightWidgetProvider.class));
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
//        sendBroadcast(intent);
//
//    }

}
