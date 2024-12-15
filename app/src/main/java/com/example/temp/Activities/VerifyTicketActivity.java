package com.example.temp.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.temp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.text.NumberFormat;
import java.util.Locale;

public class VerifyTicketActivity extends AppCompatActivity {
    private TextView tvFilmName, tvSelectedSeats, tvSelectedDate, tvPrice, tvTime, tvUsername;
    private ImageView ivQRCode;
    private FirebaseDatabase database;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ticket);

        initializeViews();
        setupDatabase();

        // Get booking ID from intent
        String bookingId = getIntent().getStringExtra("BOOKING_ID");
        Log.d("VerifyTicket", "Received booking ID: " + bookingId);
        if (bookingId != null) {
            getBookingData(bookingId);
        } else {
            Toast.makeText(this, "Booking ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

//        TextView tvTicketInfo = findViewById(R.id.tvTicketInfo);
//
//        // Lấy thông tin booking ID được truyền từ Intent
//        String bookingId = getIntent().getStringExtra("BOOKING_ID");
//
//        // Lấy dữ liệu booking từ Firebase
//        getBookingData(bookingId);
//
//        // Hiển thị thông tin vé
//        tvTicketInfo.setText("Ticket information code: " + bookingId);

        // Liên kết nút back (ImageView)
        View backImg = findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình trước đó
                finish();
            }
        });

    }

    private void initializeViews() {
        tvFilmName = findViewById(R.id.tvFilmName);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvPrice = findViewById(R.id.tvPrice);
        tvTime = findViewById(R.id.tvTime);
        tvUsername = findViewById(R.id.tvUsername);
        ivQRCode = findViewById(R.id.ivQRCode);
    }

    private void setupDatabase() {
        database = FirebaseDatabase.getInstance();
        bookingsRef = database.getReference("bookings");
        Log.d("VerifyTicket", "Database reference path: " + bookingsRef.toString());
    }



    private void getBookingData(String bookingId) {
        Log.d("VerifyTicket", "Getting booking data for ID: " + bookingId);
        if (bookingId == null || bookingId.isEmpty()) {
            // Thông báo lỗi nếu bookingId là null hoặc rỗng
            Toast.makeText(VerifyTicketActivity.this, "Invalid booking ID", Toast.LENGTH_SHORT).show();
            return;
        }
        // Thêm log để debug
        Log.d("VerifyTicket", "Searching for booking ID: " + bookingId);

        bookingsRef.child(bookingId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("VerifyTicket", "DataSnapshot exists: " + dataSnapshot.exists());
                if (dataSnapshot.exists()) {
                    updateUI(dataSnapshot);
                } else {
                    Toast.makeText(VerifyTicketActivity.this, "Booking not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi khi lấy dữ liệu
                Toast.makeText(VerifyTicketActivity.this, "Failed to load booking data", Toast.LENGTH_SHORT).show();
                Log.e("VerifyTicket", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void updateUI(DataSnapshot dataSnapshot) {
        String filmName = dataSnapshot.child("filmName").getValue(String.class);
        String selectedDate = dataSnapshot.child("selectedDate").getValue(String.class);
        String selectedTime = dataSnapshot.child("selectedTime").getValue(String.class);
        String username = dataSnapshot.child("username").getValue(String.class);
        Double total = dataSnapshot.child("total").getValue(Double.class);

        // Format price with currency
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(total);

        // Update TextViews
        tvFilmName.setText(filmName);
        tvSelectedDate.setText("Date: " + selectedDate);
        tvTime.setText("Time: " + selectedTime);
        tvUsername.setText("Customer: " + username);
        tvPrice.setText("Total: " + formattedPrice);

        // Handle selected seats (which is now a child node)
        StringBuilder seatsBuilder = new StringBuilder("Seats: ");
        DataSnapshot seatsSnapshot = dataSnapshot.child("selectedSeats");
        if (seatsSnapshot.exists()) {
            for (DataSnapshot seat : seatsSnapshot.getChildren()) {
                seatsBuilder.append(seat.getValue()).append(", ");
            }
            // Remove last comma and space
            if (seatsBuilder.length() > 7) {
                seatsBuilder.setLength(seatsBuilder.length() - 2);
            }
        }
        tvSelectedSeats.setText(seatsBuilder.toString());

        // Generate QR code with all booking information
        String qrContent = String.format("Film: %s\nDate: %s\nTime: %s\nSeats: %s\nTotal: %s\nCustomer: %s",
                filmName, selectedDate, selectedTime, seatsBuilder.toString(), formattedPrice, username);
        generateQRCode(qrContent);
    }

    private void generateQRCode(String content) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);

            // Chuyển BitMatrix thành Bitmap
            Bitmap bitmap = toBitmap(bitMatrix);

            // Hiển thị mã QR trong ImageView
            ivQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Chuyển BitMatrix thành Bitmap
    private Bitmap toBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return bitmap;
    }
}
