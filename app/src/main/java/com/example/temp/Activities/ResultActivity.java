package com.example.temp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.temp.R;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    TextView txtResult, txtDetails;
    Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Ánh xạ View
        txtResult = findViewById(R.id.result);
        txtDetails = findViewById(R.id.details);
        btnHome = findViewById(R.id.btnHome);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");
        String filmName = intent.getStringExtra("filmName");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        ArrayList<String> seats = intent.getStringArrayListExtra("seats");
        double totalPrice = intent.getDoubleExtra("totalPrice", 0);

        txtResult.setText(result);
        txtDetails.setText(
                "Name: " + name + "\n" +
                        "Username: " + username + "\n" +
                        "Film: " + filmName + "\n" +
                        "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Seat: " + seats + "\n" +
                        "Total price: " + String.format("%.0f VND", totalPrice)
        );

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });
    }
}
