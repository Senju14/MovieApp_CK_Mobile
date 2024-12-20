package com.example.temp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.temp.Domains.Review;
import com.example.temp.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews = new ArrayList<>(); // Default initialization

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = (reviews != null) ? reviews : new ArrayList<>();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = (reviews != null) ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // Safeguard against null list
        if (reviews == null || position >= reviews.size()) {
            return;
        }

        Review review = reviews.get(position);
        holder.tvRating.setText(String.valueOf(review.getRating()));
        holder.tvComment.setText(review.getReview());

        // Random user names
        String[] randomUsers = {"Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona", "George"};
        String randomUser = randomUsers[(int) (Math.random() * randomUsers.length)];
        holder.tvComment.setText(randomUser + ": " + review.getReview());

        // Load avatar
        Glide.with(holder.itemView.getContext())
                .load(review.getAvatarUrl())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .into(holder.ivAvatar);

        // Handle Like button
        holder.tvLike.setOnClickListener(v ->
                Toast.makeText(holder.itemView.getContext(), "Liked by " + randomUser, Toast.LENGTH_SHORT).show()
        );

        // Handle Comment button
        holder.tvCommentAction.setOnClickListener(v ->
                Toast.makeText(holder.itemView.getContext(), "Comment by " + randomUser, Toast.LENGTH_SHORT).show()
        );

        // Add sub-comments
        holder.layoutSubComments.removeAllViews();
        if (review.getSubComments() != null) {
            for (String subComment : review.getSubComments()) {
                TextView subCommentView = new TextView(holder.itemView.getContext());
                subCommentView.setText(subComment);
                subCommentView.setTextSize(12);
                subCommentView.setPadding(4, 4, 4, 4);
                holder.layoutSubComments.addView(subCommentView);
            }
        }

        // Share button
        holder.ivShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this review: " + review.getReview());
            holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    @Override
    public int getItemCount() {
        return (reviews != null) ? reviews.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvRating, tvComment, tvLike, tvCommentAction;
        ImageView ivAvatar, ivShare;
        LinearLayout layoutSubComments;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvComment = itemView.findViewById(R.id.tvComment);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivShare = itemView.findViewById(R.id.ivShare);
            layoutSubComments = itemView.findViewById(R.id.layoutSubComments);
            tvLike = itemView.findViewById(R.id.tvLike); // Like button
            tvCommentAction = itemView.findViewById(R.id.tvComment); // Comment button
        }
    }
}
