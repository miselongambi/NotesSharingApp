package com.example.notessharingapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> notesList;
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteDelete(Note note);
        void onNoteShare(Note note);
    }

    public NotesAdapter(List<Note> notesList, OnNoteClickListener listener) {
        this.notesList = notesList;
        this.listener = listener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());
        holder.creatorTextView.setText("By: " + note.getCreatorDisplayName());
        holder.deleteButton.setOnClickListener(v -> listener.onNoteDelete(note));
        holder.shareButton.setOnClickListener(v -> listener.onNoteShare(note));
        holder.creatorTextView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ViewProfileActivity.class);
            intent.putExtra("creatorId", note.getCreatorId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, creatorTextView;
        Button deleteButton, shareButton;

        NoteViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            creatorTextView = itemView.findViewById(R.id.creatorTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}