package com.example.temp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.R;
import com.example.temp.databinding.ItemTimeBinding;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {
    private final List<String> timeSlots;
//    private int selectedPosition = RecyclerView.NO_POSITION;
    private int selectedPosition = 0;
    private OnTimeSelectedListener listener;

    public interface OnTimeSelectedListener {
        void onTimeSelected(String selectedTime);
    }

    public TimeAdapter(List<String> timeSlots, OnTimeSelectedListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    public TimeAdapter(List<String> timeSlots) {  // Added overloaded constructor
        this.timeSlots = timeSlots;
        this.listener = null;
    }

    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        private final ItemTimeBinding binding;

        public TimeViewHolder(ItemTimeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String time, int selectedPosition, Context context) {
            binding.TextViewTime.setText(time);

            int adapterPosition = getAdapterPosition();
            if (adapterPosition == selectedPosition) {
                binding.TextViewTime.setBackgroundResource(R.drawable.white_bg);
                binding.TextViewTime.setTextColor(ContextCompat.getColor(context, R.color.black));
            } else {
                binding.TextViewTime.setBackgroundResource(R.drawable.light_black_bg);
                binding.TextViewTime.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        }
    }

    @Override
    public TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemTimeBinding binding = ItemTimeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TimeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(TimeViewHolder holder, int position) {
        holder.bind(
                timeSlots.get(position),
                selectedPosition,
                holder.itemView.getContext()
        );

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = adapterPosition;

                // Notify changes for previous and current selected items
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);

                // Callback to inform about time selection
                if (listener != null) {
                    listener.onTimeSelected(timeSlots.get(selectedPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }
}