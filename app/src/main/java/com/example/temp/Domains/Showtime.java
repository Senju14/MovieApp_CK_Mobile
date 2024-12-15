package com.example.temp.Domains;

public class Showtime {
    private String movieName;
    private String theaterName;
    private String schedule;

    public Showtime() {
        // Default constructor for Firebase
    }

    public Showtime(String movieName, String theaterName, String schedule) {
        this.movieName = movieName;
        this.theaterName = theaterName;
        this.schedule = schedule;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
