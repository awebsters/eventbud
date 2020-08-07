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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.R;
import com.example.eventschill.data.BuddiesListAdapter;
import com.example.eventschill.data.model.Event;
import com.google.gson.Gson;

public class BuddiesDialogFragment extends DialogFragment {

    private Event event;
    private TextView eventName;
    private RecyclerView buddiesList;
    private BuddiesListAdapter buddiesListAdapter;

    public static BuddiesDialogFragment newInstance(Event event){
        BuddiesDialogFragment fragment = new BuddiesDialogFragment();
        Gson gson = new Gson();
        Bundle args = new Bundle();
        args.putString("getEvents", gson.toJson(event));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buddies_view, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        Gson gson = new Gson();
        event = gson.fromJson(getArguments().getString("getEvents"), Event.class);
        FragmentManager fragmentManager = gson.fromJson(getArguments().getString("fragmentManager"), FragmentManager.class);

        eventName = (TextView)view.findViewById(R.id.name);
        eventName.setText(event.name);

        buddiesList = (RecyclerView)view.findViewById(R.id.buddiesList);
        buddiesList.setLayoutManager(new LinearLayoutManager(getContext()));
        buddiesListAdapter = new BuddiesListAdapter(event, getFragmentManager());
        buddiesList.setAdapter(buddiesListAdapter);

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
