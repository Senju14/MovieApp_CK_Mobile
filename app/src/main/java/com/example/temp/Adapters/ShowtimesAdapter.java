package com.example.temp.Adapters;

import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.temp.Domains.NotificationReceiver;
import com.example.temp.R;
import java.util.List;
import java.util.Map;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShowtimesAdapter extends RecyclerView.Adapter<ShowtimesAdapter.ShowtimeViewHolder> {

    private List<Map<String, Object>> showtimes;

    public ShowtimesAdapter(List<Map<String, Object>> showtimes) {
        this.showtimes = showtimes;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Map<String, Object> showtime = showtimes.get(position);

        String movieTitle = (String) showtime.get("movieTitle");
        String theater = (String) showtime.get("theater");
        String showDate = (String) showtime.get("showDate");
        String showTime = (String) showtime.get("showTime");
        String location = (String) showtime.get("location");

        holder.tvMovieName.setText(movieTitle);
        holder.tvTheaterName.setText(theater);
        holder.tvSchedule.setText("Date: " + showDate + " | Time: " + showTime + " | Location: " + location);

        holder.ivNotification.setOnClickListener(v -> {
            setNotificationForShowtime(holder.itemView.getContext(), showtime);
        });
    }

    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    // Moved the notification method to the adapter class
    private void setNotificationForShowtime(Context context, Map<String, Object> showtime) {

        // Kiểm tra quyền cho Android 12 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, "Please allow exact alarms in Settings", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String movieTitle = (String) showtime.get("movieTitle");
        String showDate = (String) showtime.get("showDate");
        String showTime = (String) showtime.get("showTime");
        String dateTime = showDate + " " + showTime;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(format.parse(dateTime));

            // Use a unique request code based on the time
            int requestCode = (int) (calendar.getTimeInMillis() / 1000);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("movieTitle", movieTitle);
            intent.putExtra("showTime", dateTime);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

            Toast.makeText(context, "Notification set for " + movieTitle, Toast.LENGTH_SHORT).show();

            // Debug log
            Log.d("Notification", "Alarm set for: " + format.format(calendar.getTime()));

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error setting notification", Toast.LENGTH_SHORT).show();
        }
    }

    public static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieName, tvTheaterName, tvSchedule;
        ImageView ivNotification;

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvTheaterName = itemView.findViewById(R.id.tvTheaterName);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            ivNotification = itemView.findViewById(R.id.ivNotification);
        }
    }
}