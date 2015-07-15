package com.tito.crazy.flash.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tito.crazy.flash.service.ServiceCamera;
import com.tito.crazy.flash.ultils.ManagePreferences;

public class WidgetBroadcast extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.w("ServiceCamera", "onReceive");
        if (bundle!=null){
            boolean isWidget = bundle.getBoolean("WIDGET");
            if (isWidget){
                Log.w("ServiceCamera", "Widget");
                if (!ManagePreferences.getLightStatus(context)){
                    startService(context);
                }else{
                    stopService(context);
                    ManagePreferences.setLightStatus(context, false);
                    updateWidget(context);
                }
            }else{
                Log.w("ServiceCamera", "Activity");
                boolean status = bundle.getBoolean("STATUS");
                if (status != ManagePreferences.getLightStatus(context)){
                    if (!ManagePreferences.getLightStatus(context)){
                        startService(context);
                    }else{
                        stopService(context);
                        ManagePreferences.setLightStatus(context, false);
                        updateWidget(context);
                    }
                }
            }

        }else{
            Log.w("ServiceCamera", "Widget");
            if (!ManagePreferences.getLightStatus(context)){
                startService(context);
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


    private void startService(Context context){
        Intent cameraIntent = new Intent(context,ServiceCamera.class);
        context.startService(cameraIntent);
    }
    private void stopService(Context context){
        Intent cameraIntent = new Intent(context,ServiceCamera.class);
        context.stopService(cameraIntent);
    }

}
