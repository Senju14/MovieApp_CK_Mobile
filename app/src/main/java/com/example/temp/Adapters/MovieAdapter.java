package com.example.temp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.temp.Domains.MovieItem;
import com.example.temp.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<MovieItem> movieList;

    public MovieAdapter(Context context, List<MovieItem> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_search, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Gắn dữ liệu cho mỗi item
        MovieItem movie = movieList.get(position);

        holder.titleTextView.setText(movie.getTitle());

        // Kiểm tra và hiển thị thể loại
        if (movie.getGenre() != null && !movie.getGenre().isEmpty()) {
            holder.genreTextView.setText(String.join(", ", movie.getGenre()));
        }

    }

    @Override
    public int getItemCount() {
        // Trả về số lượng phim trong danh sách
        return movieList.size();
    }

    // Inner class ViewHolder
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;
        TextView genreTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các view từ layout item
            posterImageView = itemView.findViewById(R.id.moviePosterImageView);
            titleTextView = itemView.findViewById(R.id.movieTitleTextView);
            genreTextView = itemView.findViewById(R.id.movieGenreTextView);
        }
    }
}