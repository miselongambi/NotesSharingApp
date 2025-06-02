package com.example.notessharingapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView displayNameTextView, profilePictureUrlTextView;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        displayNameTextView = findViewById(R.id.displayNameTextView);
        profilePictureUrlTextView = findViewById(R.id.profilePictureUrlTextView);

        String creatorId = getIntent().getStringExtra("creatorId");
        if (creatorId == null) {
            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userReference = FirebaseDatabase.getInstance().getReference("users").child(creatorId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile profile = dataSnapshot.getValue(UserProfile.class);
                if (profile != null) {
                    displayNameTextView.setText(profile.getDisplayName());
                    profilePictureUrlTextView.setText(profile.getProfilePictureUrl() != null ? profile.getProfilePictureUrl() : "No profile picture");
                } else {
                    displayNameTextView.setText("Unknown User");
                    profilePictureUrlTextView.setText("No profile picture");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}