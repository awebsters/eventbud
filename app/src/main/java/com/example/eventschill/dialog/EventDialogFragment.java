package com.example.eventschill.dialog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventschill.R;
import com.example.eventschill.Util;
import com.example.eventschill.data.model.Event;
import com.google.gson.Gson;

public class EventDialogFragment extends DialogFragment {

    private Event event;
    private TextView eventName;
    private Button findBuddies;
    private TextView description;

    public EventDialogFragment(){}

    public static EventDialogFragment newInstance(Event event){
        EventDialogFragment fragment = new EventDialogFragment();
        Gson gson = new Gson();
        Bundle args = new Bundle();
        args.putString("getEvents", gson.toJson(event));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_expand_view, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        Gson gson = new Gson();
        event = gson.fromJson(getArguments().getString("getEvents"), Event.class);

        eventName = (TextView)view.findViewById(R.id.name);
        eventName.setText(event.name);

        description = view.findViewById(R.id.description);
        description.setText(event.description);

        ImageView imageView = view.findViewById(R.id.photo);
        Integer resId = Util.catToImage.get(event.category);
        if (resId != null) {
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), resId));
        }

        ((TextView)view.findViewById(R.id.show)).setText(String.format("%f, %f", event.longitude, event.latitude));

        ((TextView)view.findViewById(R.id.time)).setText(event.startTime);
        ((TextView)view.findViewById(R.id.categories)).setText(event.category);


        findBuddies = view.findViewById(R.id.buddiesButton);
        findBuddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuddiesDialogFragment buddiesDialogFragment = BuddiesDialogFragment.newInstance(event);
                buddiesDialogFragment.show(getFragmentManager(), "buddies_dialog");

            }
        });

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
