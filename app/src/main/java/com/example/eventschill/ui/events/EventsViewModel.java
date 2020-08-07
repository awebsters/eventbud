package com.example.eventschill.ui.events;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eventschill.data.EventListAdapter;

public class EventsViewModel extends ViewModel implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private MutableLiveData<EventListAdapter> eventListAdapter;
    public FragmentManager fragmentManager;

    public EventsViewModel() {

        eventListAdapter = new MutableLiveData<EventListAdapter>(){{setValue(new EventListAdapter());}};
        eventListAdapter.getValue().mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventListAdapter.ViewHolder viewHolder = (EventListAdapter.ViewHolder) view.getTag();
                eventListAdapter.getValue().events.get(viewHolder.getAdapterPosition()).displayMaximum(fragmentManager);
            }
        };
    }

    public void setRecyclerView(RecyclerView recyclerView){
        recyclerView.setAdapter(eventListAdapter.getValue());
    }

    public void setSwipeLayout(SwipeRefreshLayout swipeLayout){
        swipeRefreshLayout = swipeLayout;
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (eventListAdapter.getValue() != null){
            eventListAdapter.getValue().refresh();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public LiveData<EventListAdapter> getEventListAdapter(){
        return eventListAdapter;
    }
}