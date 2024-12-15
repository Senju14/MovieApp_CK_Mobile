package com.example.temp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.Activities.BookingHistoryActivity;
import com.example.temp.Activities.VerifyTicketActivity;

import com.example.temp.Domains.Booking;
import com.example.temp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {
    private List<Booking> bookingList;
    private Context context;
    private DatabaseReference bookingsRef;


    public BookingHistoryAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
        this.bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        // Debug log để kiểm tra dữ liệu booking
        Log.d("BookingAdapter", "Binding booking: " + booking.getFilmName());

        holder.filmName.setText(booking.getFilmName());
        holder.selectedSeats.setText("Seats: " + booking.getSelectedSeats());
        holder.selectedDate.setText("Date: " + booking.getSelectedDate());
        holder.price.setText("Price: " + booking.getPrice());

        // Đơn giản hóa việc lấy booking ID và xử lý click
        holder.btnVerifyTicket.setOnClickListener(v -> {
            // Lấy trực tiếp từ Firebase theo filmName và date
            bookingsRef.orderByChild("filmName").equalTo(booking.getFilmName())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                                // Log để debug
                                Log.d("BookingAdapter", "Found booking: " + bookingSnapshot.getKey());

                                String bookingId = bookingSnapshot.getKey();
                                Intent intent = new Intent(context, VerifyTicketActivity.class);
                                intent.putExtra("BOOKING_ID", bookingId);
                                Log.d("BookingAdapter", "Starting VerifyTicketActivity with ID: " + bookingId);
                                context.startActivity(intent);
                                return; // Exit after finding first match
                            }
                            // If no booking found
                            Log.d("BookingAdapter", "No booking found for: " + booking.getFilmName());
                            Toast.makeText(context, "Không tìm thấy thông tin vé", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("BookingAdapter", "Error: " + error.getMessage());
                            Toast.makeText(context, "Lỗi khi tải thông tin vé", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView filmName, selectedSeats, selectedDate, price;
        Button btnVerifyTicket;

        public BookingViewHolder(View itemView) {
            super(itemView);
            filmName = itemView.findViewById( R.id.textViewFilmName);
            selectedSeats = itemView.findViewById(R.id.textViewSelectedSeats);
            selectedDate = itemView.findViewById(R.id.textViewSelectedDate);
            price = itemView.findViewById(R.id.textViewPrice);
            btnVerifyTicket = itemView.findViewById(R.id.btnVerifyTicket);
        }
    }
}

