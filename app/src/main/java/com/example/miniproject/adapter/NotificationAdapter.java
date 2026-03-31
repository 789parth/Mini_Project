package com.example.miniproject.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.databinding.NotificationItemBinding;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final ArrayList<String> notificationList;
    private final ArrayList<Integer> notificationImage;

    public NotificationAdapter(ArrayList<String> notificationList, ArrayList<Integer> notificationImage) {
        this.notificationList = notificationList;
        this.notificationImage = notificationImage;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationItemBinding binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final NotificationItemBinding binding;

        public NotificationViewHolder(@NonNull NotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.notificationTextView.setText(notificationList.get(position));
            binding.notificationImageView.setImageResource(notificationImage.get(position));
        }
    }
}
