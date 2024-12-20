package com.example.temp.Domains;

import java.util.List;

public class Review {
    private float rating;
    private String review;

    private String avatarUrl;
    private List<String> subComments;
    private boolean isLiked; // Trạng thái Like
    private List<String> comments; // Danh sách bình luận

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Review(float rating, String review, String avatarUrl, List<String> subComments, boolean isLiked, List<String> comments) {
        this.rating = rating;
        this.review = review;
        this.avatarUrl = avatarUrl;
        this.subComments = subComments;
        this.isLiked = isLiked;
        this.comments = comments;
    }

    public Review() {
        // Default constructor
    }

    public Review(float rating, String review, String avatarUrl, List<String> subComments) {
        this.rating = rating;
        this.review = review;
        this.avatarUrl = avatarUrl;
        this.subComments = subComments;
    }

    public Review(float rating, String review) {
        this.rating = rating;
        this.review = review;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<String> getSubComments() {
        return subComments;
    }

    public void setSubComments(List<String> subComments) {
        this.subComments = subComments;
    }

    // Getter và Setter
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}

