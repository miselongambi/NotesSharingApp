package com.example.notessharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    private EditText displayNameEditText, profilePictureUrlEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        displayNameEditText = findViewById(R.id.displayNameEditText);
        profilePictureUrlEditText = findViewById(R.id.profilePictureUrlEditText);
        Button saveProfileButton = findViewById(R.id.saveProfileButton);
        Button backButton = findViewById(R.id.backButton);

        userReference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        saveProfileButton.setOnClickListener(v -> saveProfile());
        backButton.setOnClickListener(v -> finish());
    }

    private void saveProfile() {
        String displayName = displayNameEditText.getText().toString().trim();
        String profilePictureUrl = profilePictureUrlEditText.getText().toString().trim();

        if (displayName.isEmpty()) {
            Toast.makeText(this, "Display name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfile userProfile = new UserProfile(displayName, profilePictureUrl);
        userReference.setValue(userProfile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}