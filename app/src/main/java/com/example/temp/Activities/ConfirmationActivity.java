package com.example.temp.Activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temp.databinding.ActivityConfirmationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity {

    private ActivityConfirmationBinding binding;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get data from Intent
        String name = getIntent().getStringExtra("name");
        String username = getIntent().getStringExtra("username");
        String filmName = getIntent().getStringExtra("filmName");
        String selectedDate = getIntent().getStringExtra("selectedDate");
        String selectedTime = getIntent().getStringExtra("selectedTime");
        ArrayList<String> selectedSeats = getIntent().getStringArrayListExtra("selectedSeats");
        double price = getIntent().getDoubleExtra("price", 0.0);
        double discount = getIntent().getDoubleExtra("discount", 0.0);

        // Display booking details
        displayBookingDetails(name, username, filmName, selectedDate,
                selectedTime, selectedSeats, price, discount);

        // Handle back button
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        // Handle continue button
        binding.continueBtn.setOnClickListener(v -> {
            // Save booking to Firebase
            saveBookingToFirebase(name, username, filmName, selectedDate,
                    selectedTime, selectedSeats, price, discount);
        });
    }

    // Function to display booking details
    private void displayBookingDetails(String name, String username, String filmName,
                                       String selectedDate, String selectedTime,
                                       ArrayList<String> selectedSeats,
                                       double price, double discount) {
        NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        double total = price - discount;

        binding.nameTxt.setText("Name: " + name);
        binding.usernameTxt.setText("Username: " + username);
        binding.titleTxt.setText("Film: " + filmName);
        binding.dateTxt.setText("Date: " + selectedDate);
        binding.timeTxt.setText("Time: " + selectedTime);
        binding.seatsTxt.setText("Seats: " + selectedSeats.toString());
        binding.priceTxt.setText("Price: " + vnFormat.format(price * 24000));
        binding.discountTxt.setText("Discount: " + vnFormat.format(discount * 24000));
        binding.totalTxt.setText("Total: " + vnFormat.format(total * 24000));
    }

    private void saveBookingToFirebase(String name, String username, String filmName,
                                       String selectedDate, String selectedTime,
                                       ArrayList<String> selectedSeats,
                                       double price, double discount) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference bookingsRef = mDatabase.child("bookings");
        DatabaseReference usersRef = mDatabase.child("users");

        // Generate unique booking ID
        String bookingId = bookingsRef.push().getKey();
        if (bookingId == null) return;

        String userEmail = currentUser.getEmail();
        String userId = currentUser.getUid();
        double total = price - discount;

        // Booking data
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("name", name);
        bookingData.put("username", username);
        bookingData.put("filmName", filmName);
        bookingData.put("selectedDate", selectedDate);
        bookingData.put("selectedTime", selectedTime);
        bookingData.put("selectedSeats", selectedSeats);
        bookingData.put("price", price);
        bookingData.put("discount", discount);
        bookingData.put("total", total);
        bookingData.put("userId", userId);
        bookingData.put("userEmail", userEmail);

        // Save booking
        bookingsRef.child(bookingId).setValue(bookingData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking saved successfully", Toast.LENGTH_SHORT).show();

                    // Add booking reference to user's profile
                    usersRef.child(userId).child("bookings").child(bookingId).setValue(true);

                    // Optional: Navigate to a success or next screen
                    // startActivity(new Intent(this, SomeNextActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}