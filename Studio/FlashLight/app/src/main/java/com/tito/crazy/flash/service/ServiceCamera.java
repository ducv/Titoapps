package com.tito.crazy.flash.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.tito.crazy.flash.R;
import com.tito.crazy.flash.ultils.FlashUtils;
import com.tito.crazy.flash.ultils.ManagePreferences;
import com.tito.crazy.flash.widget.TitoFlashLightWidgetProvider;

import java.io.IOException;

public class ServiceCamera extends Service {

    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private FlashUtils flashUtils;
    private boolean isWidget;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            isWidget = bundle.getBoolean("WIDGET");
        }else{
            isWidget = false;
        }
        Log.w("Start",""+isWidget);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("onCreate",""+isWidget);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1,
                1,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        windowManager.addView(surfaceView, params);

        flashUtils = new FlashUtils(this);
        flashUtils.init(surfaceView);

        if (ManagePreferences.getLightStatus(this)) {
            ManagePreferences.setLightStatus(this, false);
            flashUtils.turnOff();
            updateWidget();
        } else {
            ManagePreferences.setLightStatus(this, true);
            flashUtils.turnOn();
            updateWidget();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (surfaceView != null) windowManager.removeView(surfaceView);
        flashUtils.release();
    }


    private void updateWidget() {
        Intent intent = new Intent(this, TitoFlashLightWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), TitoFlashLightWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);

    }

}