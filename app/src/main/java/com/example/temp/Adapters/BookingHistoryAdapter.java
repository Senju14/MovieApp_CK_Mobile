package com.example.temp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.temp.Activities.VerifyTicketActivity;

import com.example.temp.Domains.Booking;
import com.example.temp.R;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {
    private List<Booking> bookingList;
    private Context context;

    public BookingHistoryAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
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
        holder.filmName.setText(booking.getFilmName());
        holder.selectedSeats.setText("Seats: " + booking.getSelectedSeats());
        holder.selectedDate.setText("Date: " + booking.getSelectedDate());
        holder.price.setText("Price: " + booking.getPrice());

        // Xử lý sự kiện khi nhấn vào nút "Xác Minh Vé Điện Tử"
        holder.btnVerifyTicket.setOnClickListener(v -> {
            Intent intent = new Intent(context, VerifyTicketActivity.class);
            intent.putExtra("BOOKING_ID", booking.getId()); // Truyền thông tin booking
            context.startActivity(intent);
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

