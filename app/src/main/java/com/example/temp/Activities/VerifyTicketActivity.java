package com.example.temp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temp.R;

public class VerifyTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ticket);

        TextView tvTicketInfo = findViewById(R.id.tvTicketInfo);

        // Lấy thông tin booking ID được truyền từ Intent
        String bookingId = getIntent().getStringExtra("BOOKING_ID");

        // Hiển thị thông tin vé
        tvTicketInfo.setText("Ticket information code: " + bookingId);

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
}
