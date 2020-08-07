package com.example.eventschill.data;

import android.content.SharedPreferences;

import com.example.eventschill.data.model.LoggedInUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AppData {
    private static SharedPreferences appData;
    private static SharedPreferences.Editor appDataEditor;


    public static void set(SharedPreferences appData) {
        AppData.appData = appData;
        appDataEditor = appData.edit();
    }

    public static LoggedInUser getLogin(){
        Gson gson = new Gson();
        Type type = new TypeToken<LoggedInUser>(){}.getType();
        LoggedInUser user = gson.fromJson(appData.getString("USER", ""), type);
        return user;
    }

    public static void saveLogin(LoggedInUser user){
        Gson gson = new Gson();
        appDataEditor.putString("USER", gson.toJson(user));
        appDataEditor.commit();
    }
}
