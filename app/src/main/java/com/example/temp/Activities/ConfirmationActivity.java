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

        // Handle continue button
//        binding.continueBtn.setOnClickListener(v -> {
//            // Save booking to Firebase
//            saveBookingToFirebase(name, username, filmName, selectedDate,
//                    selectedTime, selectedSeats, price, discount);
//
//        });
        Double total =  (price - discount)*24000;
        String totalString = String.format("%.0f", total);

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveBookingToFirebase(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(totalString);
                    String code = data.getString("return_code");

                    if (code.equals("1")) {
                        String token = data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(ConfirmationActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String zpTransToken, String appTransId, String zpTransId) {
                                Log.d("Kiem tra ne ahihih", "Oi troi oi no khong xuat hien");

                                saveBookingToFirebase(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);

                                // Lắng nghe kết quả lưu Firebase
                                DatabaseReference bookingsRef = mDatabase.child("bookings");
                                bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Intent intent = new Intent(ConfirmationActivity.this, ResultActivity.class);
                                            intent.putExtra("result", "Payment successful");
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(ConfirmationActivity.this, "Lưu dữ liệu lên Firebase thất bại.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Xử lý lỗi nếu không thể lưu Firebase
                                        Toast.makeText(ConfirmationActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Intent intent1 = new Intent(ConfirmationActivity.this, ResultActivity.class);
                                intent1.putExtra("result", "Payment Canceled");
                                startActivity(intent1);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Intent intent1 = new Intent(ConfirmationActivity.this, ResultActivity.class);
                                intent1.putExtra("result", "Payment Error");
                                startActivity(intent1);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
//            paymentDialog.dismiss();
//            openZaloPayScreen(filmName, price - discount);
        });

        paymentDialog.findViewById(R.id.momoOption).setOnClickListener(v -> {
//            paymentDialog.dismiss();
//            openMoMoScreen(filmName, price - discount);
        });

        paymentDialog.findViewById(R.id.cashOption).setOnClickListener(v -> {
            paymentDialog.dismiss();
            saveBookingToFirebase(name, username, filmName, selectedDate, selectedTime, selectedSeats, price, discount);
        });

        paymentDialog.show();
    }

//    private void openMoMoScreen(String filmName, double totalAmount) {
//        Intent intent = new Intent(this, MoMoPaymentActivity.class);
//        intent.putExtra("filmName", filmName);
//        intent.putExtra("totalAmount", totalAmount);
//        startActivity(intent);
//    }

//    private void openZaloPayScreen(String filmName, double totalAmount) {
//        Intent intent = new Intent(this, ZaloPayPaymentActivity.class);
//        intent.putExtra("filmName", filmName);
//        intent.putExtra("totalAmount", totalAmount);
//        startActivity(intent);
//    }



    // Function to display booking details
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
        binding.discountTxt.setText("Discount: " + vnFormat.format(discount * 24000));
        binding.totalTxt.setText("Total: " + vnFormat.format(total * 24000));


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