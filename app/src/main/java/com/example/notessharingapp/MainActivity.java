package com.example.notessharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText titleEditText, contentEditText;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private List<Note> notesList = new ArrayList<>();
    private DatabaseReference databaseReference, sharedNotesReference, usersReference;
    private FirebaseAuth mAuth;
    private String currentUserDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        Button addNoteButton = findViewById(R.id.addNoteButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button profileButton = findViewById(R.id.profileButton);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter(notesList, new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteDelete(Note note) {
                deleteNote(note);
            }

            @Override
            public void onNoteShare(Note note) {
                shareNote(note);
            }
        });
        notesRecyclerView.setAdapter(notesAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("notes").child(mAuth.getCurrentUser().getUid());
        sharedNotesReference = FirebaseDatabase.getInstance().getReference("shared_notes");
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        // Load current user's display name
        usersReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile profile = dataSnapshot.getValue(UserProfile.class);
                currentUserDisplayName = profile != null && profile.getDisplayName() != null ? profile.getDisplayName() : mAuth.getCurrentUser().getEmail();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                currentUserDisplayName = mAuth.getCurrentUser().getEmail();
            }
        });

        loadNotes();
        loadSharedNotes();

        addNoteButton.setOnClickListener(v -> addNote());
        logoutButton.setOnClickListener(v -> logout());
        profileButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
    }

    private void addNote() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String noteId = databaseReference.push().getKey();
        Note note = new Note(noteId, title, content, mAuth.getCurrentUser().getUid(), currentUserDisplayName);
        databaseReference.child(noteId).setValue(note)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        titleEditText.setText("");
                        contentEditText.setText("");
                        Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add note", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadNotes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    notesList.add(note);
                }
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load notes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSharedNotes() {
        sharedNotesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    if (!notesList.contains(note)) {
                        notesList.add(note);
                    }
                }
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load shared notes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNote(Note note) {
        databaseReference.child(note.getId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void shareNote(Note note) {
        sharedNotesReference.child(note.getId()).setValue(note)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Note shared", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to share note", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}