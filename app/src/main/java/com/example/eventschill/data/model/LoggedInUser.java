package com.example.eventschill.data.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.ArrayMap;
import android.util.Base64;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventschill.Server;
import com.example.eventschill.Util;
import com.example.eventschill.dialog.MessagingDialogFragment;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoggedInUser {
    public class User implements Comparable {
        private String username;
        private String fullName;
        private String password;
        private String age;
        private String tvShowOrMovie;
        private String bio;
        private Bitmap photo;
        private double longitude;
        private double latitude;
        private List<String> eventIDs;
        private List<String> eventNames;
        public Map<String, String> channels;
        public String answerSheet;
        public double scoreToLoggedinUser;

        public User(String username, String password, String fullName){
            this.username = username;
            this.password = password;
            this.fullName = fullName;
        }

        public User(String username, String password, String fullName,
                    String age, String tvShowOrMovie, String bio, Bitmap photo,
                    double longitude, double latitude, List<String> eventIDs, List<String> names,
                    Map<String, String> channel, String answerSheet){
            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.age = age;
            this.tvShowOrMovie = tvShowOrMovie;
            this.bio = bio;
            this.photo = photo;
            this.longitude = longitude;
            this.latitude = latitude;
            this.eventIDs = eventIDs;
            this.eventNames = names;
            this.channels = channel;
            this.answerSheet = answerSheet;
        }

        public void addChannel(String name){
            String[] usernames = name.split("_");
            channels.put(name, usernames[0]);
            Server.getServerInterface().addChannel(data -> {}, e -> {}, name, usernames[0], usernames[1]);
            Server.getServerInterface().addChannel(data -> {}, e -> {}, name, usernames[1], usernames[0]);
        }

        public void removeLiked(String id, String name){
            eventIDs.remove(id);
            eventNames.remove(name);
            Server.getServerInterface().unlikeEvent(
                    data -> {

                    }, e -> {}, id, name);
        }

        public boolean isLiked(String id){
            return eventIDs.contains(id);
        }

        public void likeEvent(String id, String name){
            eventIDs.add(id);
            eventNames.add(name);
            Server.getServerInterface().likeEvent(
                    data -> {

                    }, e -> {}, id, name);
        }

        public List<String> getEventIDs() {
            return eventIDs;
        }

        public void setEventIDs(List<String> eventIDs) {
            this.eventIDs = eventIDs;
        }

        public List<String> getEventNames() {
            return eventNames;
        }

        public void setEventNames(List<String> eventNames) {
            this.eventNames = eventNames;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getFullName(){
            return fullName;
        }

        public String getAge() {
            return age;
        }

        public String getTvShowOrMovie() {
            return tvShowOrMovie;
        }

        public String getBio() {
            return bio;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public Bitmap getPhoto() {
            return photo;
        }

        public String getPhotoString(){
            return Util.BitMapToString(photo);
        }

        public void displayMessenger(FragmentManager fragmentManager){
            MessagingDialogFragment eventDialogFragment = MessagingDialogFragment.newInstance(this);
            eventDialogFragment.show(fragmentManager, "messaging");
        }

        @Override
        public int compareTo(Object o) {
            if (this.scoreToLoggedinUser == ((User) o).scoreToLoggedinUser){
                return 0;
            } else if (this.scoreToLoggedinUser > ((User) o).scoreToLoggedinUser) {
                return -1;
            }
            return 1;
        }
    }

    public enum AttemptStatus{
        NONE,
        IN_PROGRESS,
        FAILED,
        SUCCESS
    }

    public static User user = null;
    private MutableLiveData<AttemptStatus> lastAttemptStatus = new MutableLiveData<AttemptStatus>(){{setValue(AttemptStatus.NONE);}};

    public LiveData<Boolean> isLoggedIn() {
        LiveData<Boolean> loggedIn = new LiveData<Boolean>() {{setValue(user!=null);}};
        return loggedIn;
    }
    public LiveData<AttemptStatus> attemptStatus(){
        return lastAttemptStatus;
    }

    public void login(final String username, final String password){
        lastAttemptStatus.setValue(AttemptStatus.IN_PROGRESS);

        new Server().getServerInterface().loginUser(
                username, password,
                data -> {
                    if (data.optString("Status").equals("Success")){

                        data = data.optJSONObject("Data");
                        LoggedInUser.user = createUserFromJSON(data, username, password);
                        LoggedInUser.this.lastAttemptStatus.setValue(AttemptStatus.SUCCESS);
                    } else{
                        LoggedInUser.this.lastAttemptStatus.setValue(AttemptStatus.FAILED);
                    }
                }, e -> LoggedInUser.this.lastAttemptStatus.setValue(AttemptStatus.FAILED));
    }

    public User createUserFromJSON(JSONObject data, String username, String password){
        String fullName = data.optString("FullName");
        String age = data.optString("Age");
        String bio = data.optString("Bio");
        List<String> eventIDs = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if (data.has("Events_Liked")){
            data.optJSONObject("Events_Liked").keys().forEachRemaining(eventIDs::add);
            for (Iterator<String> it = data.optJSONObject("Events_Liked").keys(); it.hasNext(); ) {
                String name = it.next();
                names.add(data.optJSONObject("Events_Liked").optString(name));
            }
        }

        Bitmap photo = Util.StringToBitMap(data.optString("Picture"));
        String show = data.optString("Show");
        String[] longlat = data.optString("Location").split("\\s");
        double longitude = Double.parseDouble(longlat[0]);
        double latitude = Double.parseDouble(longlat[1]);
        String answerSheet = data.optString("Questionnaire");

        Map<String, String> channels = new HashMap<>();
        if (data.optJSONObject("Channel") != null){
            channels = new Gson().fromJson(data.optJSONObject("Channel").toString(), HashMap.class);
        }

        return new User(username, password, fullName, age, show,
                bio, photo, longitude, latitude, eventIDs, names, channels, answerSheet);
    }

    public static User getUser() {
        return user;
    }
}


