package com.example.eventschill.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.R;
import com.example.eventschill.data.ViewProfileEventsListAdapter;
import com.example.eventschill.data.YourProfileEventsListAdapter;
import com.example.eventschill.data.model.LoggedInUser;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);

        profileViewModel.initListAdapter(getFragmentManager());

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        LoggedInUser.User user = LoggedInUser.getUser();
        ImageView imageView = root.findViewById(R.id.photo);
        imageView.setImageBitmap(user.getPhoto());

        TextView name = root.findViewById(R.id.name);
        name.setText(user.getFullName());

        TextView age = root.findViewById(R.id.age);
        age.setText(user.getAge());

        TextView bio = root.findViewById(R.id.bio);
        bio.setText(user.getBio());

        TextView shows = root.findViewById(R.id.show);
        shows.setText(user.getTvShowOrMovie());

        final RecyclerView recyclerView = root.findViewById(R.id.goingToList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profileViewModel.setRecyclerView(recyclerView);

        profileViewModel.getYourProfileEventsListAdapter().observe(this, new Observer<YourProfileEventsListAdapter>() {
            @Override
            public void onChanged(YourProfileEventsListAdapter yourProfileEventsListAdapter) {
                yourProfileEventsListAdapter.notifyDataSetChanged();
            }
        });

        /*final TextView textView = root.findViewById(R.id.text_notifications);
        profileViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}