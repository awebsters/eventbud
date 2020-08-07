package com.example.eventschill.ui.messaging;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.data.UserListMessageAdapter;
import com.example.eventschill.data.model.LoggedInUser;

import java.util.ArrayList;

public class MessagingViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<UserListMessageAdapter> userListMessageAdapter;

    public MessagingViewModel() {
        mText = new MutableLiveData<>();
    }

    public void initAdapter(FragmentManager fragmentManager){
        userListMessageAdapter = new MutableLiveData<UserListMessageAdapter>(){{setValue(new UserListMessageAdapter(fragmentManager));}};
    }

    public void setRecyclerView(RecyclerView recyclerView){
        recyclerView.setAdapter(userListMessageAdapter.getValue());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<UserListMessageAdapter> getAdapter(){
        return userListMessageAdapter;
    }
}