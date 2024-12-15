package com.example.temp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.temp.Domains.Review;
import com.example.temp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etReview;
    private Button btnSubmitReview;
    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_rating);

        // Lấy title của phim từ Intent
        String movieTitle = getIntent().getStringExtra("movieTitle");
        if (movieTitle == null || movieTitle.isEmpty()) {
            Log.e("RatingActivity", "Movie title is null or empty!");
            // Nếu movieTitle không hợp lệ, có thể thông báo cho người dùng và không tiếp tục
            finish();
            return;
        }

        // Liên kết các view
        ratingBar = findViewById(R.id.ratingBar);
        etReview = findViewById(R.id.etReview);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        backImg = findViewById(R.id.backImg);

        // Hiển thị tiêu đề phim
        TextView tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieTitle.setText(movieTitle);

        // Xử lý nút quay lại
        backImg.setOnClickListener(v -> finish());

        // Lắng nghe sự kiện khi người dùng nhấn nút submit
        btnSubmitReview.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String review = etReview.getText().toString().trim();

            if (!review.isEmpty()) {
                submitReview(movieTitle, rating, review);
            } else {
                Toast.makeText(RatingActivity.this, "Please write a review!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReview(String movieTitle, float rating, String review) {
        // Firebase Database reference
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(movieTitle);

        // Tạo đối tượng Review mới và lưu vào Firebase
        Review newReview = new Review(rating, review);
        reviewsRef.push().setValue(newReview)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RatingActivity.this, "Review submitted!", Toast.LENGTH_SHORT).show();
                        // Quay về màn hình trước đó sau 2 giây
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(RatingActivity.this, FavoritesActivity.class);
                            startActivity(intent);
                            finish();
                        }, 2000); // Đợi 2 giây
                    } else {
                        Toast.makeText(RatingActivity.this, "Failed to submit review.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("RatingActivity", "Error: " + e.getMessage()));
    }


}

