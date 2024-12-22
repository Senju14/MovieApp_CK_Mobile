package com.example.temp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.temp.Domains.Film;
import com.example.temp.Api.CreateOrder;
import com.example.temp.R;
import com.example.temp.databinding.ActivityConfirmationBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ConfirmationActivity extends AppCompatActivity {

    private ActivityConfirmationBinding binding;
    private DatabaseReference mDatabase;
    private double discount = 0.0; // Default discount

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

        // Fetch discount from Firebase
        fetchDiscountAndDisplay(name, username, filmName, selectedDate, selectedTime, selectedSeats, price);

        // Handle back button
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ZaloPaySDK.init(2553, Environment.SANDBOX);

        binding.continueBtn.setOnClickListener(v -> showPaymentOptions(
                name, username, filmName, selectedDate, selectedTime, selectedSeats, price
        ));
    }

    private void fetchDiscountAndDisplay(String name, String username, String filmName,
                                         String selectedDate, String selectedTime,
                                         ArrayList<String> selectedSeats, double price) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference discountRef = mDatabase.child("users").child(userId).child("discount");

        discountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    discount = snapshot.getValue(Double.class); // Get discount value from Firebase
                } else {
                    discount = 0.0; // Default to no discount
                }
                // Update UI with booking details
                displayBookingDetails(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConfirmationActivity.this, "Failed to fetch discount: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                discount = 0.0; // Default to no discount
                displayBookingDetails(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
            }
        });
    }

    private void displayBookingDetails(String name, String username, String filmName,
                                       String selectedDate, String selectedTime,
                                       ArrayList<String> selectedSeats, double price, double discount) {
        NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        double priceInVND = price * 24000; // Convert price to VND
        double discountAmount = priceInVND * discount;
        double total = priceInVND - discountAmount;

        binding.nameTxt.setText("Name: " + name);
        binding.usernameTxt.setText("Email: " + username);
        binding.titleTxt.setText("Film: " + filmName);
        binding.dateTxt.setText("Date: " + selectedDate);
        binding.timeTxt.setText("Time: " + selectedTime);
        binding.seatsTxt.setText("Seats: " + selectedSeats.toString());

        binding.priceTxt.setText("Price: " + vnFormat.format(priceInVND));
        binding.discountTxt.setText("Discount: " + vnFormat.format(discountAmount));
        binding.totalTxt.setText("Total: " + vnFormat.format(total));

        Film film = (Film) getIntent().getSerializableExtra("film");
        if (film != null) {
            binding.titleTxt.setText("Film: " + film.getTitle());
            Glide.with(this)
                    .load(film.getPoster())
                    .into(binding.filmPic);
        }
    }

    private void showPaymentOptions(String name, String username, String filmName,
                                    String selectedDate, String selectedTime,
                                    ArrayList<String> selectedSeats, double price) {
        BottomSheetDialog paymentDialog = new BottomSheetDialog(this);
        paymentDialog.setContentView(R.layout.dialog_payment_options);

        paymentDialog.findViewById(R.id.zalopayOption).setOnClickListener(v -> {
            paymentDialog.dismiss();
            saveBookingToFirebase(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
        });

        paymentDialog.findViewById(R.id.cashOption).setOnClickListener(v -> {
            paymentDialog.dismiss();
            saveBookingToFirebase(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
            Toast.makeText(this, "Ticket booking successful (Cash payment)", Toast.LENGTH_SHORT).show();
        });

        paymentDialog.show();
    }

//    private void saveBookingToFirebase(String name, String username, String filmName,
//                                       String selectedDate, String selectedTime,
//                                       ArrayList<String> selectedSeats, double price, double discount) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser == null) {
//            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        DatabaseReference bookingsRef = mDatabase.child("bookings");
//        DatabaseReference usersRef = mDatabase.child("users");
//
//        String bookingId = bookingsRef.push().getKey();
//        if (bookingId == null) return;
//
//        String userEmail = currentUser.getEmail();
//        String userId = currentUser.getUid();
//        double priceInVND = price * 24000;
//        double discountAmount = priceInVND * discount;
//        double total = priceInVND - discountAmount;
//        int points = (int) (total / 1000);
//
//        Map<String, Object> bookingData = new HashMap<>();
//        bookingData.put("name", name);
//        bookingData.put("username", username);
//        bookingData.put("filmName", filmName);
//        bookingData.put("selectedDate", selectedDate);
//        bookingData.put("selectedTime", selectedTime);
//        bookingData.put("selectedSeats", selectedSeats);
//        bookingData.put("price", priceInVND);
//        bookingData.put("discount", discountAmount);
//        bookingData.put("total", total);
//        bookingData.put("userId", userId);
//        bookingData.put("userEmail", userEmail);
//
//        bookingsRef.child(bookingId).setValue(bookingData)
//                .addOnSuccessListener(aVoid -> {
//                    usersRef.child(userId).child("bookings").child(bookingId).setValue(true);
//                    usersRef.child(userId).child("points").setValue(points);
//                    Toast.makeText(this, "Booking saved successfully!", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to save booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//    }

    private void saveBookingToFirebase(String name, String username, String filmName,
                                       String selectedDate, String selectedTime,
                                       ArrayList<String> selectedSeats, double price, double discount) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference bookingsRef = mDatabase.child("bookings");
        DatabaseReference usersRef = mDatabase.child("users");

        String bookingId = bookingsRef.push().getKey();
        if (bookingId == null) return;

        String userEmail = currentUser.getEmail();
        String userId = currentUser.getUid();
        double priceInVND = price * 24000;
        double discountAmount = priceInVND * discount;
        double total = priceInVND - discountAmount;
        int points = (int) (total / 1000);

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("name", name);
        bookingData.put("username", username);
        bookingData.put("filmName", filmName);
        bookingData.put("selectedDate", selectedDate);
        bookingData.put("selectedTime", selectedTime);
        bookingData.put("selectedSeats", selectedSeats);
        bookingData.put("price", priceInVND);
        bookingData.put("discount", discountAmount);
        bookingData.put("total", total);
        bookingData.put("userId", userId);
        bookingData.put("userEmail", userEmail);

        bookingsRef.child(bookingId).setValue(bookingData)
                .addOnSuccessListener(aVoid -> {
                    usersRef.child(userId).child("bookings").child(bookingId).setValue(true);

                    usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int currentPoints = 0;
                            double currentTotalSpent = 0;

                            if (snapshot.hasChild("points")) {
                                currentPoints = snapshot.child("points").getValue(Integer.class);
                            }

                            if (snapshot.hasChild("totalSpent")) {
                                currentTotalSpent = snapshot.child("totalSpent").getValue(Double.class);
                            }

                            int updatedPoints = currentPoints + points;
                            double updatedTotalSpent = currentTotalSpent + total;

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("points", updatedPoints);
                            updates.put("totalSpent", updatedTotalSpent);

                            usersRef.child(userId).updateChildren(updates)
                                    .addOnSuccessListener(unused -> Toast.makeText(ConfirmationActivity.this, "Booking saved successfully!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(ConfirmationActivity.this, "Failed to update user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ConfirmationActivity.this, "Failed to fetch user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save booking: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
