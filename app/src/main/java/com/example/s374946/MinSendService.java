package com.example.s374946;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class MinSendService {
    private static final String CHANNEL_ID = "sms_channel";

    public static void sendSMS(Context context, String phoneNumber, String userName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MinSendService", "SMS permission not granted");
            return;
        }

        try {
            List<String> sentUserNames = new ArrayList<>();

            SharedPreferences prefs = context.getSharedPreferences("com.example.s374946_preferences", Context.MODE_PRIVATE);
            String defaultMessage = "Gratulerer med dagen!"; // Standard melding
            String customMessage = prefs.getString("sms_message", defaultMessage);
            String message = customMessage + ", " + userName;

            // Logg innholdet av meldingen
            Log.d("MinSendService", "Sending SMS to: " + phoneNumber + " with message: " + message);

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("MinSendService", "SMS sent to: " + phoneNumber);

            sentUserNames.add(userName);

            // Vis notifikasjon når SMS er sendt
            showNotification(context, sentUserNames);
        } catch (Exception e) {
            Log.e("MinSendService", "Failed to send SMS", e);
        }
    }

    private static void showNotification(Context context, List<String> userNames) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SMS Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Formatere navnene som en kommaseparert liste
        String namesString = String.join(", ", userNames);
        String notificationText = "Meldingen har blitt sendt til " + namesString;

        // Bygg notifikasjonen
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SMS sendt")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Vis notifikasjonen
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

