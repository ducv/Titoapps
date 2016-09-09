package com.tito.crazy.flash;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tito.crazy.flash.component.Battery;
import com.tito.crazy.flash.component.BrightnessLevel;
import com.tito.crazy.flash.service.ServiceCamera;
import com.tito.crazy.flash.ultils.FlashUtils;
import com.tito.crazy.flash.ultils.ManagePreferences;
import com.tito.crazy.flash.ultils.SoundUltils;
import com.tito.crazy.flash.widget.TitoFlashLightWidgetProvider;

public class FlashActivity extends Activity  {

    	private FlashUtils flashUtils;
    private ToggleButton powerBt;
    private SoundUltils soundUltils;
    private ToggleButton soundTg;
    private LinearLayout moreBt;
    private LinearLayout brightLl;
    private SurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        soundUltils = SoundUltils.getInstance(this);
        initControl();
        AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    }


    private void initControl() {
        powerBt = (ToggleButton) findViewById(R.id.myButtonPower);
        brightLl =(LinearLayout)findViewById(R.id.myLayout_bright);
        powerBt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendBroadCastToCameraService(isChecked);
                soundUltils.playSound(isChecked);
                updateBackgroundBrightLayout(isChecked);
            }
        });
        surfaceView = (SurfaceView)findViewById(R.id.preview);


        soundTg = (ToggleButton) findViewById(R.id.myToggleButtonSound);
        soundTg.setChecked(ManagePreferences.getSound(this));
        powerBt.setChecked(ManagePreferences.getLightStatus(this));
        updateBackgroundBrightLayout(ManagePreferences.getLightStatus(this));
        soundTg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ManagePreferences.setSound(FlashActivity.this, isChecked);

            }
        });
        moreBt = (LinearLayout) findViewById(R.id.myButtonMore);
        moreBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePage();
            }
        });
    }


    private void updateBackgroundBrightLayout(boolean status){
        if (status){
            brightLl.setBackgroundResource(R.drawable.yellow_border);
        }else{
            brightLl.setBackgroundResource(R.drawable.yellow_stobe_border);
        }
    }
    private void sendBroadCastToCameraService(boolean status) {
        if (status != ManagePreferences.getLightStatus(this)){
            if (!ManagePreferences.getLightStatus(this)){
                startService(this,false);
            }else{
                stopService(this);
                ManagePreferences.setLightStatus(this, false);
                updateWidget(this);
            }
        }
    }

    private void updateWidget(Context context) {
        Intent intent = new Intent(context, TitoFlashLightWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, TitoFlashLightWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);

    }
    private void startService(Context context,boolean isWidget){
        Intent cameraIntent = new Intent(context,ServiceCamera.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("WIDGET",isWidget);
        context.startService(cameraIntent);
    }
    private void stopService(Context context){
        Intent cameraIntent = new Intent(context,ServiceCamera.class);
        context.stopService(cameraIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadCastToCameraService(false);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        powerBt.setChecked(ManagePreferences.getLightStatus(this));
        updateBackgroundBrightLayout(ManagePreferences.getLightStatus(this));
        super.onResume();
    }

    private void openHomePage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.home_page)));
        startActivity(intent);
    }


}
