package com.example.temp.Activities;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.Adapters.ShowtimesAdapter;
import android.Manifest;
import com.example.temp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowtimesActivity extends AppCompatActivity {

    private RecyclerView rvShowtimes;
    private ShowtimesAdapter adapter;
    private List<Map<String, Object>> showtimesList; // Danh sách các Map

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtimes);

        rvShowtimes = findViewById(R.id.rvShowtimes);
        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        showtimesList = new ArrayList<>();
        adapter = new ShowtimesAdapter(showtimesList);
        rvShowtimes.setAdapter(adapter);

        // Xử lý nút back
        ImageView backImg = findViewById(R.id.backImg);
        backImg.setOnClickListener(v -> onBackPressed()); // Gọi phương thức onBackPressed() để quay lại trang trước


        loadShowtimes();
        checkNotificationPermission();
    }

    private void loadShowtimes() {
        DatabaseReference showtimesRef = FirebaseDatabase.getInstance().getReference("showtimes");
        showtimesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showtimesList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Map<String, Object> showtime = (Map<String, Object>) data.getValue();
                    if (showtime != null) {
                        showtimesList.add(showtime);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ShowtimesActivity", "Error: " + error.getMessage());
            }
        });
    }

    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
