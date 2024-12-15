package com.example.temp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.Adapters.ShowtimesAdapter;
import com.example.temp.Domains.Showtime;
import com.example.temp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowtimesActivity extends AppCompatActivity {

    private RecyclerView rvShowtimes;
    private ShowtimesAdapter adapter;
    private List<Showtime> showtimesList;
    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtimes);

        // Liên kết các View
        rvShowtimes = findViewById(R.id.rvShowtimes);
        backImg = findViewById(R.id.backImg); // Liên kết nút Back

        rvShowtimes = findViewById(R.id.rvShowtimes);
        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        showtimesList = new ArrayList<>();
        adapter = new ShowtimesAdapter(showtimesList);
        rvShowtimes.setAdapter(adapter);

        loadShowtimes();

        // Xử lý khi nhấn nút quay lại
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Gọi hàm quay lại
            }
        });
    }

    private void loadShowtimes() {
        DatabaseReference showtimesRef = FirebaseDatabase.getInstance().getReference("showtimes");
        showtimesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showtimesList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Showtime showtime = data.getValue(Showtime.class);
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
}
