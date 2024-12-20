package com.example.temp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temp.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailOrPhoneEditText;
    Button resetPasswordButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.forgot_password);

        emailOrPhoneEditText = findViewById(R.id.email_or_phone_edit_text);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        backButton = findViewById(R.id.back_button);

        // Back button functionality
        backButton.setOnClickListener(v -> onBackPressed());

        // Reset password logic
        resetPasswordButton.setOnClickListener(v -> {
            String emailOrPhone = emailOrPhoneEditText.getText().toString().trim();

            if (emailOrPhone.isEmpty()) {
                emailOrPhoneEditText.setError("Please enter your email or phone number");
                return;
            }

            // Perform the reset password operation (e.g., Firebase)
            resetPassword(emailOrPhone);
        });
    }

    private void resetPassword(String emailOrPhone) {
        // Here you can handle the password reset logic using Firebase or any other service.
        // For example, with Firebase Authentication:

        // Assuming Firebase Auth instance is set up:
        // FirebaseAuth auth = FirebaseAuth.getInstance();

        // For Firebase reset password:
        // auth.sendPasswordResetEmail(emailOrPhone)
        //     .addOnCompleteListener(task -> {
        //         if (task.isSuccessful()) {
        //             // Password reset email sent, now navigate to ChangePasswordActivity
        //             Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
        //             Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
        //             startActivity(intent);
        //             finish();
        //         } else {
        //             // Handle error
        //             Toast.makeText(ForgotPasswordActivity.this, "Error resetting password", Toast.LENGTH_SHORT).show();
        //         }
        //     });

        // Dummy success case:
        Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
        finish();
    }
}
