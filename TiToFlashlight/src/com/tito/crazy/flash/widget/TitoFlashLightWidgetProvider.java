package com.tito.crazy.flash.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.tito.crazy.flash.R;

public class TitoFlashLightWidgetProvider extends AppWidgetProvider {
	public static final String ACTION_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
	public static final String ACTION_BROADCAST = "RunWidgetBroadcast";

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIDs) {
		for (int i = 0; i < appWidgetIDs.length; i++) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.titoflashlightwidget);
			int appWidgetId = appWidgetIDs[i];
			Intent clickIntent = new Intent(context, WidgetBroadcast.class);
			clickIntent.setAction(ACTION_BROADCAST);
			PendingIntent pendingIntentBroadcast = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.myButtonWidgetLight, pendingIntentBroadcast);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}

	}

}