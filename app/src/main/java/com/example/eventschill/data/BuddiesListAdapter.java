package com.example.eventschill.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.R;
import com.example.eventschill.Server;
import com.example.eventschill.data.model.Event;
import com.example.eventschill.data.model.LoggedInUser;
import com.example.eventschill.dialog.MessagingDialogFragment;
import com.example.eventschill.dialog.ProfileDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class BuddiesListAdapter extends RecyclerView.Adapter<BuddiesListAdapter.ViewHolder> {

    public ArrayList<LoggedInUser.User> users;
    public FragmentManager fragmentManager;


    public BuddiesListAdapter(Event event, FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
        users = new ArrayList<>();
        Server.getServerInterface().getAttending(
                data -> {
                    ArrayList<LoggedInUser.User> arrayList = new ArrayList<>();
                    data = data.optJSONObject("Data");
                    if (data == null){
                        return;
                    }
                    for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                        String user = it.next();
                        JSONObject userData = data.optJSONObject(user);
                        double rating = userData.optDouble("Rating");
                        LoggedInUser.User newUser = new LoggedInUser().createUserFromJSON(userData, user, userData.optString("Password"));
                        newUser.scoreToLoggedinUser = rating;
                        arrayList.add(newUser);
                    }
                    users = arrayList;
                    Collections.sort(users);
                    notifyDataSetChanged();
                }, e -> {}, event.eventID);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buddy_list_view, parent, false);
        ViewHolder holder = new ViewHolder(userView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoggedInUser.User user = users.get(position);

        holder.name.setText(user.getFullName());
        holder.age.setText(user.getAge());
        holder.show.setText(user.getTvShowOrMovie());
        holder.photo.setImageBitmap(user.getPhoto());
    }

    @Override
    public int getItemCount(){
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView age;
        public TextView show;
        public ImageView photo;
        public Button viewProfile;
        public Button message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            age = itemView.findViewById(R.id.age);
            show = itemView.findViewById(R.id.show);
            photo = itemView.findViewById(R.id.photo);
            viewProfile = itemView.findViewById(R.id.viewProfileButton);
            message = itemView.findViewById(R.id.messageButton);


            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoggedInUser.User user = users.get(ViewHolder.this.getAdapterPosition());
                    ProfileDialogFragment profileDialogFragment = ProfileDialogFragment.newInstance(user);

                    profileDialogFragment.show(fragmentManager, "profile_dialog");
                }
            });

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoggedInUser.User user = users.get(ViewHolder.this.getAdapterPosition());
                    MessagingDialogFragment messagingDialogFragment = MessagingDialogFragment.newInstance(user);

                    messagingDialogFragment.show(fragmentManager, "message_dialog");
                }
            });
        }
    }
}

