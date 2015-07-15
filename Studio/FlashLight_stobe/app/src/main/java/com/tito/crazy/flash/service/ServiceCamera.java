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

    public static int ID_NOTIFICATION = 2018;

    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private FlashUtils flashUtils;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("ServiceCamera", "Start");
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
            Log.w("ServiceCamera", "turn off");
            ManagePreferences.setLightStatus(this, false);
            flashUtils.turnOff();
            updateWidget();
        } else {
            Log.w("ServiceCamera", "turn on");
            ManagePreferences.setLightStatus(this, true);
            flashUtils.turnOn();
            updateWidget();
        }
        Log.w("ServiceCamera", "Start");
    }


    public void createNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), ServiceCamera.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, notificationIntent, 0);

        Notification notification = new Notification(R.drawable.icon, "Click to start launcher",
                System.currentTimeMillis());
        notification.setLatestEventInfo(getApplicationContext(), "Start launcher", "Click to start launcher", pendingIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICATION, notification);
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