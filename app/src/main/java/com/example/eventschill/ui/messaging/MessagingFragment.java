package com.example.eventschill.ui.messaging;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.R;
import com.example.eventschill.data.UserListMessageAdapter;
import com.example.eventschill.data.model.LoggedInUser;

import java.util.ArrayList;

public class MessagingFragment extends Fragment {

    private MessagingViewModel messagingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messagingViewModel =
                ViewModelProviders.of(this).get(MessagingViewModel.class);

        messagingViewModel.initAdapter(getFragmentManager());

        View root = inflater.inflate(R.layout.fragment_messaging, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messagingViewModel.setRecyclerView(recyclerView);

        messagingViewModel.getAdapter().observe(this, new Observer<UserListMessageAdapter>() {
            @Override
            public void onChanged(UserListMessageAdapter userListMessageAdapter) {
                userListMessageAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }
}