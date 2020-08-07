package com.example.eventschill.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eventschill.R;
import com.example.eventschill.data.EventListAdapter;

public class EventsFragment extends Fragment {

    private EventsViewModel eventsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventsViewModel =
                ViewModelProviders.of(this).get(EventsViewModel.class);
        eventsViewModel.fragmentManager = getFragmentManager();

        View root = inflater.inflate(R.layout.fragment_events, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsViewModel.setRecyclerView(recyclerView);

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.eventRefresh);
        eventsViewModel.setSwipeLayout(swipeRefreshLayout);

        eventsViewModel.getEventListAdapter().observe(this, new Observer<EventListAdapter>() {
            @Override
            public void onChanged(EventListAdapter eventListAdapter) {
                eventListAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }
}