package com.example.eventschill.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.R;
import com.example.eventschill.data.BuddiesListAdapter;
import com.example.eventschill.data.ViewProfileEventsListAdapter;
import com.example.eventschill.data.YourProfileEventsListAdapter;
import com.example.eventschill.data.model.Event;
import com.example.eventschill.data.model.LoggedInUser;
import com.google.gson.Gson;

public class ProfileDialogFragment extends DialogFragment {

    LoggedInUser.User user;
    private RecyclerView userEventsList;
    private ViewProfileEventsListAdapter userEventsListAdapter;

    public static ProfileDialogFragment newInstance(LoggedInUser.User user){
        ProfileDialogFragment fragment = new ProfileDialogFragment();
        Gson gson = new Gson();
        Bundle args = new Bundle();
        args.putString("user", gson.toJson(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_profile_modal, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        Gson gson = new Gson();
        user = gson.fromJson(getArguments().getString("user"), LoggedInUser.User.class);

        TextView name = view.findViewById(R.id.name);
        name.setText(user.getFullName());

        TextView age = view.findViewById(R.id.age);
        age.setText(user.getAge());

        TextView bio = view.findViewById(R.id.bio);
        bio.setText(user.getBio());

        TextView show = view.findViewById(R.id.show);
        show.setText(user.getTvShowOrMovie());

        ImageView photo = view.findViewById(R.id.photo);
        photo.setImageBitmap(user.getPhoto());

        view.findViewById(R.id.message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessagingDialogFragment messagingDialogFragment = MessagingDialogFragment.newInstance(user);
                messagingDialogFragment.show(getFragmentManager(), "message_dialog");
            }
        });


        userEventsList = (RecyclerView)view.findViewById(R.id.goingToList);
        userEventsList.setLayoutManager(new LinearLayoutManager(getContext()));
        userEventsListAdapter = new ViewProfileEventsListAdapter(user, getFragmentManager());
        userEventsList.setAdapter(userEventsListAdapter);

        ImageView closeButton = (ImageView)view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

}
