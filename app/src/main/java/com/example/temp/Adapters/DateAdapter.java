package com.example.temp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.R;
import com.example.temp.databinding.ItemDateBinding;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private final List<String> timeSlots;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(int position);
    }

    public DateAdapter(List<String> timeSlots) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        private final ItemDateBinding binding;

        public DateViewHolder(ItemDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String date, int selectedPosition, Context context) {
            String[] dateParts = date.split("/");

            if (dateParts.length == 3) {
                binding.dayTxt.setText(dateParts[0]);
                binding.datMonthTxt.setText(dateParts[1] + " " + dateParts[2]);

                int adapterPosition = getAdapterPosition();
                if (adapterPosition == selectedPosition) {
                    binding.mailLayout.setBackgroundResource(R.drawable.white_bg);
                    binding.dayTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
                    binding.datMonthTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
                } else {
                    binding.mailLayout.setBackgroundResource(R.drawable.light_black_bg);
                    binding.dayTxt.setTextColor(ContextCompat.getColor(context, R.color.white));
                    binding.datMonthTxt.setTextColor(ContextCompat.getColor(context, R.color.white));
                }
            }
        }
    }

    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDateBinding binding = ItemDateBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {
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

                // Callback to inform about date selection
                if (listener != null) {
                    listener.onDateSelected(selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }
}