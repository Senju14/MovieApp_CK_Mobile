package com.example.temp.Domains;

public class Review {
    private float rating;
    private String review;


    public Review() {
        // Default constructor
    }


    public Review(float rating, String review) {
        this.rating = rating;
        this.review = review;
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

