package com.example.eventschill.data.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.fragment.app.FragmentManager;

import com.example.eventschill.dialog.EventDialogFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Event {

    public String name;
    public String category;
    public String eventID;
    public double longitude;
    public double latitude;
    public String startTime;
    public String description;

    public Event(String name, String category, String eventID, double longitude, double latitude,
                 String startTime, String description) {
        this.name = name;
        this.category = category;
        this.eventID = eventID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startTime = startTime;
        this.description = description;
    }

    public String getLocation(Context context){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (Exception e) {
           return "Unknown";
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        return String.format("%s %s, %s, %s $s", address, city, state, country, postalCode);
    }

    public void displayMaximum(FragmentManager fragmentManager){
            EventDialogFragment eventDialogFragment = EventDialogFragment.newInstance(this);
            eventDialogFragment.show(fragmentManager, "event_dialog");
    }
}
