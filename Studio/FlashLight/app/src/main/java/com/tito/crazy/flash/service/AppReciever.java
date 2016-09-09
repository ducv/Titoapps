package com.tito.crazy.flash.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AppReciever extends BroadcastReceiver {
    private Intent xmppIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((intent.getAction() != null)
                && (intent.getAction()
                .equals("android.intent.action.BOOT_COMPLETED"))) {
            startXMPPService(context);
        }
    }

    /**
     * for xmpp service
     */
    private void startXMPPService(Context context) {
        xmppIntent = new Intent(context, ServiceCamera.class);
        context.startService(xmppIntent);
    }

}