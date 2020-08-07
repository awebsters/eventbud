package com.example.eventschill.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.R;
import com.example.eventschill.Server;
import com.example.eventschill.data.model.LoggedInUser;

import java.util.ArrayList;

public class UserListMessageAdapter extends RecyclerView.Adapter<UserListMessageAdapter.ViewHolder> {

    public ArrayList<LoggedInUser.User> users;
    public FragmentManager fragmentManager;


    public UserListMessageAdapter(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
        users = new ArrayList<>();

        for (String username : LoggedInUser.getUser().channels.values()) {
            Server.getServerInterface().getUser(
                    data -> {
                        data = data.optJSONObject("Data");
                        LoggedInUser.User user = new LoggedInUser().createUserFromJSON(data,
                                data.optString("Username"), data.optString("Password"));
                        users.add(user);
                        notifyDataSetChanged();
                    }, e -> {}, username);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_user_list_view, parent, false);
        ViewHolder holder = new ViewHolder(userView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoggedInUser.User user = users.get(position);

        holder.name.setText(user.getFullName());
        holder.username.setText(user.getUsername());
        holder.photo.setImageBitmap(user.getPhoto());
    }

    @Override
    public int getItemCount(){
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView username;
        public ImageView photo;
        public Button message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.time);
            photo = itemView.findViewById(R.id.photo);
            message = itemView.findViewById(R.id.messageButton);

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoggedInUser.User user = users.get(ViewHolder.this.getAdapterPosition());
                    user.displayMessenger(fragmentManager);
                }
            });

        }
    }
}
