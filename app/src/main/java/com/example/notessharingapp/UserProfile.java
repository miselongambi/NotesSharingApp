package com.example.notessharingapp;

public class UserProfile {
    private String displayName;
    private String profilePictureUrl;

    public UserProfile() {
    }

    public UserProfile(String displayName, String profilePictureUrl) {
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}