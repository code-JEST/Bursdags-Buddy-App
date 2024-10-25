package com.example.s374946;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.util.Calendar;

public class MinPeriodisk extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Hent SMS-tidspunktet fra SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String smsTime = sharedPreferences.getString("sms_time", "08:00"); // Standard tid
        long triggerTime = getTriggerTime(smsTime);

        // Sett opp AlarmManager for å starte MinSendService
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent sendIntent = new Intent(this, MinSendService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Angi tidspunktet for alarmen
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingIntent);

        return START_STICKY;
    }

    private long getTriggerTime(String smsTime) {
        // Konvertere smsTime
        String[] timeParts = smsTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Få dagens dato
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Returnere tidspunktet i millisekunder
        return calendar.getTimeInMillis();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

