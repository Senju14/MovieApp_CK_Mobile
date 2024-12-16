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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.Adapters.ReviewAdapter;
import com.example.temp.Domains.Review;
import com.example.temp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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


        addFakeReviews(movieTitle);
        loadReviews(movieTitle);

    }

    // Dữ liệu bình luận mẫu
    private void addFakeReviews(String movieTitle) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(movieTitle);

        reviewsRef.push().setValue(new Review(4.5f, "Amazing movie! Highly recommended."));
        reviewsRef.push().setValue(new Review(3.0f, "It was okay, not great."));
        reviewsRef.push().setValue(new Review(5.0f, "Masterpiece! The best I've ever seen."));
        reviewsRef.push().setValue(new Review(2.0f, "Disappointing, expected more."));
        reviewsRef.push().setValue(new Review(4.0f, "Good movie with some great moments."));
    }

    private void loadReviews(String movieTitle) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(movieTitle);
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Review> reviewList = new ArrayList<>();
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        reviewList.add(review);
                    }
                }

                RecyclerView rvReviews = findViewById(R.id.rvReviews);
                rvReviews.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                rvReviews.setAdapter(new ReviewAdapter(reviewList));

                if (reviewList.isEmpty()) {
                    Toast.makeText(RatingActivity.this, "No reviews available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RatingActivity", "Failed to load reviews: " + error.getMessage());
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

