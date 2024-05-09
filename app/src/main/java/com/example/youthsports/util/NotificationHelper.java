package com.example.youthsports.util;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.youthsports.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "channel_id";
    private static final String TAG = "NotificationHelper";

    private final Context context;
    private final NotificationManager notificationManager;
    private final ActivityResultLauncher<String> requestPermissionLauncher;

    public NotificationHelper(Context context, ActivityResultLauncher<String> requestPermissionLauncher) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.requestPermissionLauncher = requestPermissionLauncher;
        createNotificationChannels();
    }

    private void createNotificationChannel(String channelId, String channelName, String channelDescription) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(channelDescription);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created: " + channelId);
            } else {
                Log.d(TAG, "Notification channel already exists: " + channelId);
            }
        }
    }

    private void createNotificationChannels() {
        createNotificationChannel("1","Authorization","For sign-in, sign-up, Profile updates");
        createNotificationChannel("2","Events","To view Events");
        createNotificationChannel("3","Chats","To view Chats");
        createNotificationChannel("4","Calendar","To view Calendar");
        createNotificationChannel("5","Profile","To view Profile");
        Log.d(TAG, "All notification channels set up");
    }

    public void sendNotification(int notificationId,String channelId ,String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(notificationId,channelId ,title, content);
        } else {
            displayNotification(notificationId,channelId ,title, content);
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private void requestPermission(int notificationId, String channelId, String title, String content) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission already granted for POST_NOTIFICATIONS");
            displayNotification(notificationId, channelId, title, content);
        } else {
            Log.d(TAG, "Requesting permission for POST_NOTIFICATIONS");
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void displayNotification(int notificationId, String channelId, String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.chat_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
        Log.d(TAG, "Notification sent: [ID: " + notificationId + ", Title: " + title + "]");
    }
}
