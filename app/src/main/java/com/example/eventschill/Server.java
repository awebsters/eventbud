package com.example.eventschill;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventschill.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public class Interactor {
        private RequestQueue queue;
        private Status status = Status.NOT;

        public static final String BASEURL = "http://Eventbud-env.whujfx5i63.us-east-2.elasticbeanstalk.com/";

        private Interactor(Context context) {
            queue = Volley.newRequestQueue(context);
        }

        public void basicRequest(String path, final Map<String, String> params,
                                 final RequestSuccessListener onSuccess, @NonNull final RequestErrorListener onError){
            String fullPath = BASEURL + path;
            StringRequest request  = new StringRequest(
                    Request.Method.POST, fullPath,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject data = new JSONObject(response);
                                onSuccess.listener(data);
                            } catch(JSONException e){
                                onError.lisener(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onError.lisener(error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
        }

        public void createUser(LoggedInUser.User user, final RequestSuccessListener onSuccess,
                               final RequestErrorListener onError){
            Map<String, String> params = new HashMap<>();
            params.put("Username", user.getUsername());
            params.put("Password", user.getPassword());
            params.put("FullName", user.getFullName());
            params.put("Age", user.getAge());
            params.put("Bio", user.getBio());
            params.put("Picture", user.getPhotoString());
            params.put("Events_Liked", "");
            params.put("Show", user.getTvShowOrMovie());
            params.put("Location", user.getLongitude() + " " + user.getLatitude());
            params.put("Questionnaire", user.answerSheet);


            basicRequest("CreateLogin", params, onSuccess, onError);
        }

        public void loginUser(String username, String password, final RequestSuccessListener onSuccess,
                              final RequestErrorListener onError){
            Map<String, String> params = new HashMap<>();
            params.put("Username", username);
            params.put("Password", password);
            basicRequest("login", params, onSuccess, onError);
        }

        public void getEvents(final RequestSuccessListener onSuccess, final RequestErrorListener onError){
            Map<String, String> params = new HashMap<>();
            LoggedInUser.User user = LoggedInUser.user;
            params.put("Location", user.getLongitude() + " " + user.getLatitude());
            params.put("max_range", "50");

            basicRequest("GetEvents", params, onSuccess, onError);
        }

        public void likeEvent(final RequestSuccessListener onSuccess, final RequestErrorListener onError,
                              String eventId, String eventName){
            Map<String, String> params = new HashMap<>();
            params.put("Username", LoggedInUser.getUser().getUsername());
            params.put("eventId", eventId);
            params.put("eventName", eventName);

            basicRequest("LikeEvent", params, onSuccess, onError);
        }

        public void unlikeEvent(final RequestSuccessListener onSuccess, final RequestErrorListener onError,
                              String eventId, String eventName){
            Map<String, String> params = new HashMap<>();
            params.put("Username", LoggedInUser.getUser().getUsername());
            params.put("eventId", eventId);
            params.put("eventName", eventName);

            basicRequest("UnLikeEvent", params, onSuccess, onError);
        }

        public void getAttending(final RequestSuccessListener onSuccess, final RequestErrorListener onError,
                                 String eventId){
            Map<String, String> params = new HashMap<>();
            params.put("eventId", eventId);
            params.put("Username", LoggedInUser.getUser().getUsername());

            basicRequest("GetUsersAttending", params, onSuccess, onError);
        }

        public void getEvent(final RequestSuccessListener onSuccess, final RequestErrorListener onError,
                             String eventId){
            Map<String, String> params = new HashMap<>();
            params.put("ID", eventId);

            basicRequest("GetEventInfo", params, onSuccess, onError);
        }

        public void addChannel(final RequestSuccessListener onSuccess, final RequestErrorListener onError,
                             String eventId, String chatBuddy, String username){
            Map<String, String> params = new HashMap<>();
            params.put("ChannelID", eventId);
            params.put("ChatBuddy", chatBuddy);
            params.put("Username", username);

            basicRequest("NewChannel", params, onSuccess, onError);
        }

        public void getUser(final RequestSuccessListener onSuccess, final RequestErrorListener onError,
                            String username) {
            Map<String, String> params = new HashMap<>();
            params.put("Username", username);

            basicRequest("GetUser", params, onSuccess, onError);
        }
    }

    private static Interactor serverInteractor = null;

    private enum Status{
        RUNNING,
        NOT
    }

    public Server(){}

    public Server(Context context){
        serverInteractor = new Interactor(context);
    }

    public static Interactor getServerInterface() {
        return serverInteractor;
    }

    public boolean hasInteractor(){
        return serverInteractor != null;
    }

    public boolean isRunning(){
        return serverInteractor.status == Status.RUNNING;
    }

    public interface RequestSuccessListener{
        void listener(JSONObject data);
    }

    public interface RequestErrorListener{
        void lisener(Exception e);
    }
}

