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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
//        Log.d("ConfirmationActivity", "Date: " + selectedDate);
//        Log.d("ConfirmationActivity", "Time: " + selectedTime);
        ArrayList<String> selectedSeats = getIntent().getStringArrayListExtra("selectedSeats");
        double price = getIntent().getDoubleExtra("price", 0.0);
        double discount = getIntent().getDoubleExtra("discount", 0.0);
//        Log.d("Test gia tien", "result" + price);

        // Display booking details
        displayBookingDetails(name, username, filmName, selectedDate,
                selectedTime, selectedSeats, price, discount);

        // Handle back button
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ZaloPaySDK.init(2553, Environment.SANDBOX);

        Double price1 = price * 24000;
        Double discount1 = price1 * discount;

        Double total =  price1 - discount1;
        Log.d("test", "total: " + total);
        String totalString = String.format("%.0f", total);

        binding.continueBtn.setOnClickListener(v -> showPaymentOptions(
                name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount
        ));

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    private void showPaymentOptions(String name, String username, String filmName,
                                    String selectedDate, String selectedTime,
                                    ArrayList<String> selectedSeats, double price, double discount) {
        BottomSheetDialog paymentDialog = new BottomSheetDialog(this);
        paymentDialog.setContentView(R.layout.dialog_payment_options);

        paymentDialog.findViewById(R.id.zalopayOption).setOnClickListener(v -> {
            paymentDialog.dismiss();
            saveBookingToFirebase(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
            processZaloPayPayment(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
        });

        paymentDialog.findViewById(R.id.cashOption).setOnClickListener(v -> {
            paymentDialog.dismiss();
            saveBookingToFirebase(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
            Toast.makeText(this, "Ticket booking successful (Cash payment)", Toast.LENGTH_SHORT).show();
        });

        paymentDialog.show();
    }

    private void processZaloPayPayment(String name, String username, String filmName,
                                       String selectedDate, String selectedTime,
                                       ArrayList<String> selectedSeats, double price, double discount) {
        Double price1 = price * 24000;
        Double discount1 = price1 * discount;
        Double total = price1 - discount1;
        String totalString = String.format("%.0f", total);

        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(totalString);
            String code = data.getString("return_code");

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String zpTransToken, String appTransId, String zpTransId) {

                        saveBookingToFirebase(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);

                        Intent intent = new Intent(ConfirmationActivity.this, ResultActivity.class);
                        intent.putExtra("result", "Payment successful");
                        intent.putExtra("name", name);
                        intent.putExtra("username", username);
                        intent.putExtra("filmName", filmName);
                        intent.putExtra("date", selectedDate);
                        intent.putExtra("time", selectedTime);
                        intent.putStringArrayListExtra("seats", selectedSeats);
                        intent.putExtra("totalPrice", total);
                        startActivity(intent);
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Intent intent = new Intent(ConfirmationActivity.this, ResultActivity.class);
                        intent.putExtra("result", "Payment Canceled");
                        startActivity(intent);
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Intent intent = new Intent(ConfirmationActivity.this, ResultActivity.class);
                        intent.putExtra("result", "Payment Error");
                        startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error when creating ZaloPay order", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayBookingDetails(String name, String username, String filmName,
                                       String selectedDate, String selectedTime,
                                       ArrayList<String> selectedSeats,
                                       double price, double discount) {
        NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        double total = price - discount;

        binding.nameTxt.setText("Name: " + name);
        binding.usernameTxt.setText("Email: " + username);
        binding.titleTxt.setText("Film: " + filmName);
        binding.dateTxt.setText("Date: " + selectedDate);
        binding.timeTxt.setText("Time: " + selectedTime);
        binding.seatsTxt.setText("Seats: " + selectedSeats.toString());

        binding.priceTxt.setText("Price: " + vnFormat.format(price * 24000));
        binding.discountTxt.setText("Discount: " + vnFormat.format((price * 24000) * discount));
        binding.totalTxt.setText("Total: " + vnFormat.format((price * 24000) - (price * 24000) * discount));


        Film film = (Film) getIntent().getSerializableExtra("film");

        if (film != null) {
            binding.titleTxt.setText("Film: " + film.getTitle());
            Glide.with(this)
                    .load(film.getPoster()) // URL hoặc resource của poster
                    .into(binding.filmPic); // ID của ImageView hiển thị poster
        }

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
        double total = (price - discount)*2400;

        int points = (int) (total / 1000);

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
                    Toast.makeText(this, "Directly to payment", Toast.LENGTH_SHORT).show();

                    // Add booking reference to user's profile
                    usersRef.child(userId).child("bookings").child(bookingId).setValue(true);
                    usersRef.child(userId).child("points").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            Integer currentPoints = currentData.getValue(Integer.class);
                            if (currentPoints == null) {
                                currentPoints = 0;
                            }
                            currentData.setValue(currentPoints + points);
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot currentData) {
                            if (databaseError != null) {
                                Toast.makeText(getApplicationContext(), "Failed to update points: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Integer updatedPoints = currentData.getValue(Integer.class);
                                Toast.makeText(getApplicationContext(), "Points updated: " + updatedPoints, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // Optional: Navigate to a success or next screen
                    // startActivity(new Intent(this, SomeNextActivity.class));
                     finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}