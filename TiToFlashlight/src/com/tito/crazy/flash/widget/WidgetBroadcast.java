package com.tito.crazy.flash.widget;

import com.tito.crazy.flash.FlashActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WidgetBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.w("FUCK", "FUCK");
		Intent flashIntent = new Intent(context, FlashActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean("IS_WIDGET", true);
		flashIntent.putExtras(bundle);
		flashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(flashIntent);
	}
}
