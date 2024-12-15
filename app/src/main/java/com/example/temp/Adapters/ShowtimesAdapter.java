package com.example.temp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.Domains.Showtime;
import com.example.temp.R;

import java.util.List;

public class ShowtimesAdapter extends RecyclerView.Adapter<ShowtimesAdapter.ShowtimeViewHolder> {

    private List<Showtime> showtimes;

    public ShowtimesAdapter(List<Showtime> showtimes) {
        this.showtimes = showtimes;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimes.get(position);
        holder.tvMovieName.setText(showtime.getMovieName());
        holder.tvTheaterName.setText(showtime.getTheaterName());
        holder.tvSchedule.setText(showtime.getSchedule());
    }

    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    public static class ShowtimeViewHolder extends RecyclerView.ViewHolder {

        TextView tvMovieName, tvTheaterName, tvSchedule;

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvTheaterName = itemView.findViewById(R.id.tvTheaterName);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
        }
    }
}
