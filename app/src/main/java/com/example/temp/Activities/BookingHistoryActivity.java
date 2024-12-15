package com.example.temp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.Adapters.BookingHistoryAdapter;
import com.example.temp.Domains.Booking;
import com.example.temp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;



public class BookingHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingHistoryAdapter adapter;
    private List<Booking> bookingList;
    private FirebaseDatabase database;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_booking_history);

        // Liên kết nút back (ImageView)
        View backImg = findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình trước đó
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewBookingHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();

        // Truyền thêm context (this) khi khởi tạo adapter
        adapter = new BookingHistoryAdapter(bookingList, this);

        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        bookingsRef = database.getReference("bookings");

        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    bookingList.add(booking);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }


        });


        // QR
    }


}
