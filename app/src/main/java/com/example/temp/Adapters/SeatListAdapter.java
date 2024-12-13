package com.example.temp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temp.R;
import com.example.temp.databinding.SeatItemBinding;
import com.example.temp.Domains.Seat;

import java.util.ArrayList;
import java.util.List;

public class SeatListAdapter extends RecyclerView.Adapter<SeatListAdapter.SeatViewHolder> {

    private List<Seat> seatList;
    private Context context;
    private SelectedSeat selectedSeat;
    private List<String> selectedSeatName;

    public SeatListAdapter(List<Seat> seatList, Context context, SelectedSeat selectedSeat) {
        this.seatList = seatList;
        this.context = context;
        this.selectedSeat = selectedSeat;
        this.selectedSeatName = new ArrayList<>();
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        private SeatItemBinding binding;

        public SeatViewHolder(SeatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public SeatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SeatItemBinding binding = SeatItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SeatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        holder.binding.seat.setText(seat.getName());

        switch (seat.getStatus()) {
            case AVAILABLE:
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_available);
                holder.binding.seat.setTextColor(context.getColor(R.color.white));
                break;
            case SELECTED:
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_selected);
                holder.binding.seat.setTextColor(context.getColor(R.color.black));
                break;
            case UNAVAILABLE:
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_unavailable);
                holder.binding.seat.setTextColor(context.getColor(R.color.gray));
                break;
        }

        holder.binding.seat.setOnClickListener(v -> {
            switch (seat.getStatus()) {
                case AVAILABLE:
                    seat.setStatus(Seat.SeatStatus.SELECTED);
                    selectedSeatName.add(seat.getName());
                    notifyItemChanged(position);
                    break;
                case SELECTED:
                    seat.setStatus(Seat.SeatStatus.AVAILABLE);
                    selectedSeatName.remove(seat.getName());
                    notifyItemChanged(position);
                    break;
                default:
                    break;
            }

            String selected = String.join(", ", selectedSeatName);
            selectedSeat.Return(selected, selectedSeatName.size());
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public interface SelectedSeat {
        void Return(String selectedName, int num);
    }
}