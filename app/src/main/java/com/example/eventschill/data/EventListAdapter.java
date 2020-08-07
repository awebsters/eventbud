package com.example.eventschill.data;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.R;
import com.example.eventschill.Server;
import com.example.eventschill.Util;
import com.example.eventschill.data.model.Event;
import com.example.eventschill.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    public ArrayList<Event> events = new ArrayList<>();
    public View.OnClickListener mClickListener;

    public EventListAdapter() {
        refresh();
    }

    public void refresh(){
        new Server().getServerInterface().getEvents(
                data -> {
                    events.clear();
                    for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                        String key = it.next();
                        JSONObject eventData = data.optJSONObject(key);
                        String name = key;
                        String category = eventData.optString("Category");
                        String eventID = eventData.optString("EventID");
                        String[] longlat = eventData.optString("Location").split(",");
                        double longitude = 0;
                        double latitude = 0;
                        try {
                            longitude = (double) eventData.optJSONArray("Location").get(0);
                            latitude = (double) eventData.optJSONArray("Location").get(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String startTime = eventData.optString("Start Date");
                        String description = eventData.optString("Description");
                        Event event = new Event(name, category, eventID, longitude,
                                latitude, startTime, description);
                        events.add(event);
                    }
                    notifyDataSetChanged();
                },
                e -> {});
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_view, parent, false);
        ViewHolder holder = new ViewHolder(userView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        if (LoggedInUser.getUser().isLiked(event.eventID)){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),
                    R.drawable.heart_white_filled));
        } else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),
                    R.drawable.heart_white_outline));
        }
        holder.eventName.setText(event.name);
        Integer resId = Util.catToImage.get(event.category);
        if (resId != null){
            holder.photo.setImageBitmap(BitmapFactory.decodeResource(holder.itemView.getResources(), resId));
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView eventName;
        public ImageView likeButton;
        public ImageView photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.name);
            photo = itemView.findViewById(R.id.photo);
            likeButton = itemView.findViewById(R.id.likeButton);

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Event event = events.get(ViewHolder.this.getAdapterPosition());

                    if (LoggedInUser.getUser().isLiked(event.eventID)){
                        LoggedInUser.getUser().removeLiked(event.eventID, event.name);
                    } else {
                        LoggedInUser.getUser().likeEvent(event.eventID, event.name);
                    }
                    notifyDataSetChanged();
                }
            });

            itemView.setTag(this);
            itemView.setOnClickListener(mClickListener);
        }
    }
}
