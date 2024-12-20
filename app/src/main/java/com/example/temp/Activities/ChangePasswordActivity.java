package com.example.temp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temp.R;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText newPasswordEditText, confirmPasswordEditText;
    Button changePasswordButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.change_password);

        newPasswordEditText = findViewById(R.id.new_password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        changePasswordButton = findViewById(R.id.change_password_button);
        backButton = findViewById(R.id.back_button);

        // Back button functionality
        backButton.setOnClickListener(v -> onBackPressed());

        // Change password button functionality
        changePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate password
            if (newPassword.isEmpty()) {
                newPasswordEditText.setError("Please enter a new password");
                return;
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.setError("Please confirm your new password");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordEditText.setError("Passwords do not match");
                return;
            }

            // Proceed to update password (e.g., with Firebase or your backend)
            changePassword(newPassword);
        });
    }

    private void changePassword(String newPassword) {
        // Here, you can call the API or service to change the password
        // For example, using FirebaseAuth:
        // FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword)
        //     .addOnCompleteListener(task -> {
        //         if (task.isSuccessful()) {
        //             Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
        //         } else {
        //             Toast.makeText(ChangePasswordActivity.this, "Error changing password", Toast.LENGTH_SHORT).show();
        //         }
        //     });

        // Dummy success case:
        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

        // After 2 seconds, navigate back to LoginActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish this activity so the user cannot go back to the change password screen
        }, 2000); // 2000 milliseconds = 2 seconds
    }
}
