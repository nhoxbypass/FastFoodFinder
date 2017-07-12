package com.iceteaviet.fastfoodfinder.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String userName;
    private String avatar;
    private String content;
    private String date;
    private int rating;

    private String mediaUrl;

    public Comment(String userName, String avatar, String content, String mediaUrl, String date, int rating) {
        this.userName = userName;
        this.avatar = avatar;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.date = date;
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getContent() {
        return content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getDate() {
        return date;
    }

    public int getRating() {
        return rating;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
