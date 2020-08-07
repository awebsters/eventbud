package com.example.eventschill.data;

import android.graphics.BitmapFactory;
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
import com.example.eventschill.Util;
import com.example.eventschill.data.model.Event;
import com.example.eventschill.data.model.LoggedInUser;

import java.util.ArrayList;

public class YourProfileEventsListAdapter extends RecyclerView.Adapter<YourProfileEventsListAdapter.ViewHolder> {

    public ArrayList<Event> events;
    public FragmentManager fragmentManager;


    public YourProfileEventsListAdapter(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
        events = new ArrayList<>();
        LoggedInUser.User user = LoggedInUser.getUser();
        for (int i = 0; i < user.getEventIDs().size(); i ++){
            Server.getServerInterface().getEvent(
                    data -> {
                        if (data.optString("Status").equals("Error")){
                            return;
                        }
                        data = data.optJSONObject("Data");
                        String name = data.keys().next();
                        data = data.optJSONObject(name);
                        String category = data.optString("Category");
                        String eventID = data.optString("EventID");
                        String[] longlat = data.optString("Location").split(" ");
                        double longitude = 0;
                        double latitude = 0;
                        longitude = Double.parseDouble(longlat[0]);
                        latitude = Double.parseDouble(longlat[1]);
                        String startTime = data.optString("Start Date");
                        String description = data.optString("Description");
                        Event event = new Event(name, category, eventID, longitude,
                                latitude, startTime, description);
                        events.add(event);
                        notifyDataSetChanged();
                    }, e -> {}, LoggedInUser.getUser().getEventIDs().get(i));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_profile_event_list, parent, false);
        ViewHolder holder = new ViewHolder(userView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.name.setText(event.name);
        holder.time.setText(event.startTime);

        Integer resId = Util.catToImage.get(event.category);
        if (resId != null){
            holder.photo.setImageBitmap(BitmapFactory.decodeResource(holder.itemView.getResources(), resId));
        }
    }

    @Override
    public int getItemCount(){
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView time;
        public ImageView photo;
        public Button remove;
        public Button viewEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            photo = itemView.findViewById(R.id.photo);
            remove = itemView.findViewById(R.id.removeButton);
            viewEvent = itemView.findViewById(R.id.viewMoreButton);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Event event = events.get(ViewHolder.this.getAdapterPosition());
                    LoggedInUser.getUser().removeLiked(event.eventID, event.name);
                    events.remove(event);
                    notifyDataSetChanged();
                }
            });

            viewEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Event event = events.get(ViewHolder.this.getAdapterPosition());
                    event.displayMaximum(fragmentManager);
                }
            });
        }
    }
}

