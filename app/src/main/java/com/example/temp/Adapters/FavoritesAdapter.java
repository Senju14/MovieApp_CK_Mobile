package com.example.temp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.Activities.RatingActivity;
import com.example.temp.R;
import com.squareup.picasso.Picasso;
import com.example.temp.Domains.Film;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Film> films;
    private Context context;

    public FavoritesAdapter(Context context, List<Film> films) {
        this.context = context;
        this.films = films;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_favorite_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Film film = films.get(position);  // Access the film at the specified position
        holder.tvMovieTitle.setText(film.getTitle());  // Set the movie title
        holder.tvMovieDescription.setText(film.getDescription());  // Set the movie description
        Picasso.get().load(film.getPoster()).into(holder.ivMoviePoster);  // Load the poster image using Picasso

        // Sự kiện khi nhấn nút đánh giá
        holder.btnRate.setOnClickListener(v -> {
            String movieTitle  = film.getTitle();
            if (movieTitle  == null || movieTitle .isEmpty()) {
                Log.e("FavoritesAdapter", "Movie ID is null or empty for film: " + film.getDescription());
            } else {
                Intent intent = new Intent(context, RatingActivity.class);
                intent.putExtra("movieTitle", movieTitle);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return films.size();  // Return the size of the films list
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMoviePoster;
        TextView tvMovieTitle, tvMovieDescription;
        Button btnRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMoviePoster = itemView.findViewById(R.id.ivMoviePoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieDescription = itemView.findViewById(R.id.tvMovieDescription);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
}
