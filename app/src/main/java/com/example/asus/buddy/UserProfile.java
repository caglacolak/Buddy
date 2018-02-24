package com.example.asus.buddy;

/**
 * Created by ASUS on 19.12.2017.
 */

public class UserProfile {
    private String header;
    private String profileContent;

    public UserProfile(String header, String profileContent) {
        this.header = header;
        this.profileContent = profileContent;
    }

    public String getHeader() {
        return header;
    }

    public String getProfileContent() {
        return profileContent;
    }
}
