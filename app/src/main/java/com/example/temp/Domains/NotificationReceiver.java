package com.example.temp.Domains;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.example.temp.Activities.MainActivity;  // Thay bằng Activity bạn muốn mở khi click notification
import com.example.temp.R;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String movieTitle = intent.getStringExtra("movieTitle");
        String showTime = intent.getStringExtra("showTime");

        // Intent khi click vào notification
        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingContentIntent = PendingIntent.getActivity(
                context,
                0,
                contentIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo channel với độ ưu tiên cao
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "showtime_channel",
                    "Showtime Notifications",
                    NotificationManager.IMPORTANCE_HIGH  // Đặt độ ưu tiên cao
            );

            // Cấu hình thêm cho channel
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(channel);
        }

        // Tạo notification với nhiều tùy chỉnh hơn
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "showtime_channel")
                .setContentTitle("Movie Reminder: " + movieTitle)
                .setContentText("Your movie starts at " + showTime)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)  // Tự động đóng notification khi click
                .setContentIntent(pendingContentIntent)  // Intent khi click
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                // Thêm style cho notification
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Don't forget your movie " + movieTitle + " starting at " + showTime +
                                "\nMake sure to arrive early for the best experience!"))
                // Thêm các actions
                .addAction(R.drawable.ic_notification, "View Details", pendingContentIntent)
                // Thêm các effects
                .setLights(Color.RED, 1000, 300)
                .setVibrate(new long[]{100, 200, 300, 400})
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Show notification với ID duy nhất
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}