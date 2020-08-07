package com.example.eventschill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Util {

    public final static String ACCOUNT_SID = "";
    public final static String AUTH_TOKEN = "";
    public final static String SERVICE_SID = "SK860beb3db03bd02fe8adadda5342f690";

    public static final Map<String, Integer> catToImage = new HashMap<String, Integer>(){
        {
            put("community", R.drawable.community);
            put("concerts", R.drawable.concerts);
            put("conferences", R.drawable.conferences);
            put("expos", R.drawable.expos);
            put("festivals", R.drawable.festival);
            put("performing-arts", R.drawable.performingarts);
            put("politics", R.drawable.politics);
            put("public-holidays", R.drawable.publicholiday);
            put("school-holidays", R.drawable.schoolholiday);
            put("sports", R.drawable.sports);

        }
    };


    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
