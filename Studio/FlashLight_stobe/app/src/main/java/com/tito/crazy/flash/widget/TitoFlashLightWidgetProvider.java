package com.tito.crazy.flash.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.tito.crazy.flash.R;
import com.tito.crazy.flash.ultils.ManagePreferences;

public class TitoFlashLightWidgetProvider extends AppWidgetProvider {
	public static final String ACTION_BROADCAST = "com.tito.crazy.flash.widget.RunWidgetBroadcast";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIDs) {
		for (int i = 0; i < appWidgetIDs.length; i++) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.titoflashlightwidget);
			int appWidgetId = appWidgetIDs[i];
            Intent intent = new Intent(ACTION_BROADCAST);
            Bundle bundle = new Bundle();
            bundle.putBoolean("WIDGET", true);
//            bundle.putBoolean("STATUS", true);
            intent.putExtras(bundle);
            PendingIntent pendingIntentBroadcast = PendingIntent.getBroadcast(context, 0, intent, 0);
            if (ManagePreferences.getLightStatus(context)){
                remoteViews.setTextViewText(R.id.myTextViewStatus,"ON");
            }else{
                remoteViews.setTextViewText(R.id.myTextViewStatus,"OFF");
            }
			remoteViews.setOnClickPendingIntent(R.id.ImageViewWidget, pendingIntentBroadcast);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            Log.w("Update","Update");
        }

	}

}