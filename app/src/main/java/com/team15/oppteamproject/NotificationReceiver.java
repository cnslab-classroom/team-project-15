package com.team15.oppteamproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("ACTION_CONSCIOUS_CONFIRMED")) {
            IncidentHandler incidentHandler = MainActivity.getIncidentHandler();
            if (incidentHandler != null) {
                incidentHandler.resetCrashState();
                incidentHandler.stopMonitoring();
                incidentHandler.stopSound(); // 소리 중지
                Log.d("NotificationReceiver", "User confirmed consciousness. Crash state reset.");
            } else {
                Log.e("NotificationReceiver", "IncidentHandler instance is null.");
            }
        }
    }
}

