package com.tito.crazy.flash.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tito.crazy.flash.R;
import com.tito.crazy.flash.service.ServiceCamera;
import com.tito.crazy.flash.ultils.ManagePreferences;

public class WidgetBroadcast extends BroadcastReceiver {
    public static int ID_NOTIFICATION = 2015;
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ID_NOTIFICATION);
        if (bundle!=null){
            boolean isWidget = bundle.getBoolean("WIDGET");
            if (isWidget){
                if (!ManagePreferences.getLightStatus(context)){
                    startService(context,true);
                    createNotification(context);
                }else{
                    stopService(context);
                    ManagePreferences.setLightStatus(context, false);
                    updateWidget(context);
                }
            }else{
                boolean status = bundle.getBoolean("STATUS");
                if (status != ManagePreferences.getLightStatus(context)){
                    if (!ManagePreferences.getLightStatus(context)){
                        startService(context,false);
                    }else{
                        stopService(context);
                        ManagePreferences.setLightStatus(context, false);
                        updateWidget(context);
                    }
                }
            }

        }else{
            if (!ManagePreferences.getLightStatus(context)){
                startService(context,true);
                createNotification(context);
            }else{
                stopService(context);
                ManagePreferences.setLightStatus(context, false);
                updateWidget(context);
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

    public void createNotification(Context context) {
        Intent notificationIntent = new Intent(TitoFlashLightWidgetProvider.ACTION_BROADCAST);

        Bundle bundle = new Bundle();
        bundle.putBoolean("WIDGET", true);
        notificationIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                notificationIntent, 0);

        Notification notification = new Notification(R.drawable.ic_torch_widget,
                context.getString(R.string.turn_on),
                System.currentTimeMillis());
        notification.setLatestEventInfo(context,  context.getString(R.string.turn_on),  context.getString(R.string.click_to_turn_off), pendingIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICATION, notification);
    }

}
