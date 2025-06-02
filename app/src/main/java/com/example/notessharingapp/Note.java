package com.example.notessharingapp;

public class Note {
    private String id;
    private String title;
    private String content;
    private String creatorId;
    private String creatorDisplayName;

    public Note() {
    }

    public Note(String id, String title, String content, String creatorId, String creatorDisplayName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.creatorId = creatorId;
        this.creatorDisplayName = creatorDisplayName;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getCreatorDisplayName() {
        return creatorDisplayName;
    }
}