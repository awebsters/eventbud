package com.example.eventschill.ui.profile;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.data.ViewProfileEventsListAdapter;
import com.example.eventschill.data.YourProfileEventsListAdapter;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<YourProfileEventsListAdapter> yourProfileEventsListAdapter;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public void initListAdapter(FragmentManager fragmentManager){
        yourProfileEventsListAdapter = new MutableLiveData<YourProfileEventsListAdapter>()
        {{setValue(new YourProfileEventsListAdapter(fragmentManager));}};
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(yourProfileEventsListAdapter.getValue());
    }

    public LiveData<YourProfileEventsListAdapter> getYourProfileEventsListAdapter() {
        return yourProfileEventsListAdapter;
    }
}