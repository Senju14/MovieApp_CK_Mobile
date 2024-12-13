package com.example.temp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.temp.Adapters.SeatListAdapter;
import com.example.temp.Adapters.TimeAdapter;
import com.example.temp.Adapters.DateAdapter;
import com.example.temp.Domains.Film;
import com.example.temp.Domains.Seat;
import com.example.temp.R;
import com.example.temp.databinding.ActivitySeatListBinding;

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
    }

    private void initSeatsList() {
        // Initialize the GridLayoutManager with 7 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);

        // Set the SpanSizeLookup to manage how the items are placed in the grid
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
            public void Return(String selectedName, int num) {
                // Update the number of selected seats
                binding.numberSelectedTxt.setText(num + " Seat Selected");

                // Format the price
                DecimalFormat df = new DecimalFormat("#.##");
                double calculatedPrice = Double.parseDouble(df.format(num * film.getPrice()));

                // Update the price text
                binding.priceTxt.setText("$ " + calculatedPrice);

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