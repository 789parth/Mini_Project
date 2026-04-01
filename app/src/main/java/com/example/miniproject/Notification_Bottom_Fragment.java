package com.example.miniproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.miniproject.adapter.NotificationAdapter;
import com.example.miniproject.databinding.FragmentNotificationBottomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class Notification_Bottom_Fragment extends BottomSheetDialogFragment {

    private FragmentNotificationBottomBinding binding;

    public Notification_Bottom_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBottomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> notifications = new ArrayList<>();
        notifications.add("Your order has been Canceled Successfully");
        notifications.add("Order has been taken by the driver");
        notifications.add("Congrats Your Order Placed");

        ArrayList<Integer> notificationImages = new ArrayList<>();
        notificationImages.add(R.drawable.sademoji);
        notificationImages.add(R.drawable.truck);
        notificationImages.add(R.drawable.completed);

        NotificationAdapter adapter = new NotificationAdapter(notifications, notificationImages);
        binding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.notificationRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
