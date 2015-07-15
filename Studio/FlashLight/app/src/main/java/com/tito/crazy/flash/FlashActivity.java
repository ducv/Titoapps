package com.tito.crazy.flash;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
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
import com.tito.crazy.flash.ultils.FlashUtils;
import com.tito.crazy.flash.ultils.ManagePreferences;
import com.tito.crazy.flash.ultils.SoundUltils;
import com.tito.crazy.flash.widget.TitoFlashLightWidgetProvider;

public class FlashActivity extends Activity  {

    //	private FlashUtils flashUtils;
    private ToggleButton powerBt;
    private SoundUltils soundUltils;
    private BrightnessLevel brightnessLevel;
    private Battery battery;
    private ToggleButton soundTg;
    private Button moreBt;
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
        soundUltils = SoundUltils.getInstance(this);
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
        powerBt = (ToggleButton) findViewById(R.id.myButtonPower);
        powerBt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendBroadCastToCameraService(isChecked);
                brightnessLevel.setLevel(isChecked?FlashUtils
                        .LEVEL_3:FlashUtils.LEVEL_0);
                soundUltils.playSound(isChecked);
            }
        });

        brightnessLevel = (BrightnessLevel) findViewById(R.id.myBrightnessLevel);
        battery = (Battery) findViewById(R.id.myBattery);
        soundTg = (ToggleButton) findViewById(R.id.myToggleButtonSound);
        soundTg.setChecked(ManagePreferences.getSound(this));
        powerBt.setChecked(ManagePreferences.getLightStatus(this));
        brightnessLevel.setLevel(ManagePreferences.getLightStatus(this)?FlashUtils
                .LEVEL_3:FlashUtils.LEVEL_0);
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
        unregisterReceiver(bateryBroadcastReceiver);
        sendBroadCastToCameraService(false);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        powerBt.setChecked(ManagePreferences.getLightStatus(this));
        brightnessLevel.setLevel(ManagePreferences.getLightStatus(this)?FlashUtils
                .LEVEL_3:FlashUtils.LEVEL_0);
        super.onResume();
    }

    private void openHomePage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.home_page)));
        startActivity(intent);
    }


}
