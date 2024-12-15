package com.example.temp.Activities;

import com.google.firebase.database.DatabaseReference;

import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.temp.Adapters.SeatListAdapter;
import com.example.temp.Adapters.TimeAdapter;
import com.example.temp.Adapters.DateAdapter;
import com.example.temp.Domains.Film;
import com.example.temp.Domains.Seat;
import com.example.temp.databinding.ActivitySeatListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeatListActivity extends AppCompatActivity {
    private ActivitySeatListBinding binding;
    private Film film;
    private double price = 0.0;
    private int number = 0;

    private ArrayList<String> selectedSeats = new ArrayList<>();
    private double discount = 10.0;
    private String filmName;
    private String selectedDate;
    private String selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivitySeatListBinding.inflate(getLayoutInflater());

        // Set the root view as the content view
        setContentView(binding.getRoot());

        // Get intent extras
        getIntentExtra();
        setVariables();
        initSeatsList();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Handle "Continue" button
        binding.button2.setOnClickListener(v -> {
            proceedToConfirmation();

        });
    }

    private void proceedToConfirmation() {
        // Check if seats are selected
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "Please select seats", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime == null || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Read data from Realtime Database
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                String name = snapshot.child("name").getValue(String.class);
                String email = currentUser.getEmail() != null ? currentUser.getEmail() : "Unknown";

                // Create intent and pass data
                Intent intent = new Intent(SeatListActivity.this, ConfirmationActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("username", email);
                intent.putExtra("filmName", filmName);
                intent.putExtra("selectedDate", selectedDate);
                intent.putExtra("selectedTime", selectedTime);
                intent.putExtra("film", film);
                intent.putExtra("selectedSeats", selectedSeats);
                intent.putExtra("price", price);
                intent.putExtra("discount", discount);
                Log.d("Test gia tien", "result" + price);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initSeatsList() {


        // Set up time adapter with selection listener
        List<String> timeSlots = generateTimeSlots();
        TimeAdapter timeAdapter = new TimeAdapter(timeSlots, new TimeAdapter.OnTimeSelectedListener() {
            @Override
            public void onTimeSelected(String time) {
                selectedTime = time;
            }
        });
        binding.TimeRecyclerview.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.TimeRecyclerview.setAdapter(timeAdapter);

        // Set up date adapter with selection listener
        List<String> dates = generateDates();
        DateAdapter dateAdapter = new DateAdapter(dates, new DateAdapter.OnDateSelectedListener() {
            @Override
            public void onDateSelected(String date) {
                selectedDate = date;
            }
        });
        binding.dateRecyclerview.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.dateRecyclerview.setAdapter(dateAdapter);


        // Initialize the GridLayoutManager with 7 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1; // Default to 1 span per item
            }
        });

        // Set the LayoutManager for the RecyclerView
        binding.seatRecyclerview.setLayoutManager(gridLayoutManager);

        // Initialize the seat list
        List<Seat> seatList = new ArrayList<>();
        int numberSeats = 81; // Example number of seats

        // Loop through and create Seat objects
        for (int i = 0; i < numberSeats; i++) {
            String seatName = "Seat " + (i + 1); // Generate seat name
            Seat.SeatStatus seatStatus;

            // Set specific seats as unavailable based on their index
            if (i == 2 || i == 20 || i == 33 || i == 41 || i == 50 || i == 72 || i == 73) {
                seatStatus = Seat.SeatStatus.UNAVAILABLE;
            } else {
                seatStatus = Seat.SeatStatus.AVAILABLE;
            }

            // Create a new seat and add it to the list
            seatList.add(new Seat(seatName, seatStatus));
        }

        // Create and set the SeatListAdapter with a custom implementation of SelectedSeat
        SeatListAdapter seatAdapter = new SeatListAdapter(seatList, this, new SeatListAdapter.SelectedSeat() {
            @Override
            public void Return(ArrayList<String> selectedName, int num) {
                // Update the number of selected seats
                binding.numberSelectedTxt.setText(num + " Seat Selected");

                selectedSeats = selectedName;

                // Format the price
                DecimalFormat df = new DecimalFormat("#.##");
                price = Double.parseDouble(df.format(num * film.getPrice()));

                // Update the price text
                binding.priceTxt.setText("$ " + price);

                // Update the number of selected seats
                number = num;
            }
        });

        // Set the adapter for the RecyclerView
        binding.seatRecyclerview.setAdapter(seatAdapter);
        binding.seatRecyclerview.setNestedScrollingEnabled(false);

        // Set layout manager and adapter for the time RecyclerView
        binding.TimeRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.TimeRecyclerview.setAdapter(new TimeAdapter(generateTimeSlots()));

        // Set layout manager and adapter for the date RecyclerView
        binding.dateRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.dateRecyclerview.setAdapter(new DateAdapter(generateDates()));
    }

    private void setVariables() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    // Method to get data from Intent
    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            film = (Film) intent.getSerializableExtra("film");
            filmName = film != null ? film.getTitle() : "Unknown Film";

            // Initialize with first items in lists
            List<String> dates = generateDates();
            List<String> times = generateTimeSlots();



            selectedDate = intent.getStringExtra("selectedDate");
            selectedTime = intent.getStringExtra("selectedTime");

        }

    }

    // Method to generate time slots
    private List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        for (int i = 0; i < 24; i += 2) {
            LocalTime time = LocalTime.of(i, 0);
            timeSlots.add(time.format(formatter));
        }

        return timeSlots;
    }

    // Method to generate dates
    private List<String> generateDates() {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE/dd/MMM");

        for (int i = 0; i < 7; i++) {
            dates.add(today.plusDays(i).format(formatter));
        }

        return dates;
    }
}