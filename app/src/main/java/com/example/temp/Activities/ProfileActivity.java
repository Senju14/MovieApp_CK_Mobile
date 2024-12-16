package com.example.temp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.temp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    TextView profileName, profileEmail, profileUsername, profilePassword, profilePoints, rankText;
    TextView titleName, titleUsername;
    Button editProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        profilePoints = findViewById(R.id.point);
        rankText = findViewById(R.id.rank);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);
        showAllUserData();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });

        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut(); // Đăng xuất khỏi Firebase
        Toast.makeText(ProfileActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

        // Chuyển sang màn hình đăng nhập
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa ngăn xếp
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAllUserData() {
        Intent intent = getIntent();
        String nameUser = intent.getStringExtra("name");
        String emailUser = intent.getStringExtra("email");
        String usernameUser = intent.getStringExtra("username");
        String passwordUser = intent.getStringExtra("password");

        titleName.setText(nameUser);
        titleUsername.setText(usernameUser);
        profileName.setText(nameUser);
        profileEmail.setText(emailUser);
        profileUsername.setText(usernameUser);
        profilePassword.setText(passwordUser);

        getUserPoints();
    }


    private void getUserPoints() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        usersRef.child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer points = snapshot.getValue(Integer.class);
                    if (points != null) {
                        // Hiển thị điểm vào TextView
                        profilePoints.setText(points + "");

                        // Tính Spending từ points
                        int spending = points * 10000;

                        // Hiển thị Spending
                        TextView spendingTextView = findViewById(R.id.spending);
                        spendingTextView.setText(String.valueOf(spending));

                        // Cập nhật rank và discount
                        updateDiscount(points);
                    } else {
                        profilePoints.setText("0");

                        // Hiển thị Spending mặc định
                        TextView spendingTextView = findViewById(R.id.spending);
                        spendingTextView.setText("0");

                        updateDiscount(0);
                    }
                } else {
                    profilePoints.setText("0");

                    // Hiển thị Spending mặc định
                    TextView spendingTextView = findViewById(R.id.spending);
                    spendingTextView.setText("0");

                    updateDiscount(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load points: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Function to update discount and rank based on points
    private void updateDiscount(int points) {
        double discount = 0.0;
        String rank = "Standard"; // Default rank

        // Apply discount logic
        if (points >= 50 && points <= 50) {
            discount = 0.1;
            rank = "Silver";
        } else if (points >= 51 && points <= 99) {
            discount = 0.2;
            rank = "Gold";
        } else if (points >= 100) {
            discount = 0.25;
            rank = "VIP";
        }

        // Display discount in rank TextView
        rankText.setText(rank + " (" + discount * 100 + "% discount)");  // Set rank with discount

        // Push discount to Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Update discount in Firebase
        usersRef.child("discount").setValue(discount).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Discount saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to save discount: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Truyền discount qua Intent sang ConfirmationActivity
        Intent intent = new Intent(ProfileActivity.this, ConfirmationActivity.class);
        intent.putExtra("discount", discount);
    }

    public void passUserData() {
        String userUsername = profileUsername.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String nameFromDB = userSnapshot.child("name").getValue(String.class);
                        String emailFromDB = userSnapshot.child("email").getValue(String.class);
                        String usernameFromDB = userSnapshot.child("username").getValue(String.class);
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                        break;
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}