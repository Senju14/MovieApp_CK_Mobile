package com.example.temp.Domains;

import java.util.List;

public class MovieItem {
    private String Title;
    private List<String> Genre;
    private List<Cast> Casts;
    // Các getter và setter

    public String getTitle() { return Title; }
    public void setTitle(String title) { Title = title; }

    public List<String> getGenre() { return Genre; }
    public void setGenre(List<String> genre) { Genre = genre; }

    public List<Cast> getCasts() { return Casts; }
    public void setCasts(List<Cast> casts) { Casts = casts; }
}