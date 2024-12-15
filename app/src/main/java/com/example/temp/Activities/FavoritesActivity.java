package com.example.temp.Activities;

import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.Adapters.FavoritesAdapter;
import com.example.temp.Domains.Film;
import com.example.temp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private FavoritesAdapter favoritesAdapter;
    private List<Film> favoriteMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Liên kết nút back (ImageView)
        View backImg = findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình trước đó
                finish();
            }
        });

        rvFavorites = findViewById( R.id.rvFavorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        favoriteMovies = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(favoriteMovies);
        rvFavorites.setAdapter(favoritesAdapter);

        loadFavoriteMovies();
    }

    private void loadFavoriteMovies() {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("favorites");
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteMovies.clear();
                for (DataSnapshot movieSnapshot : snapshot.getChildren()) {
                    Film film = movieSnapshot.getValue(Film.class);
                    if (film != null) {
                        favoriteMovies.add(film);
                    }
                }
                favoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}

