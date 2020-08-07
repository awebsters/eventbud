package com.example.eventschill.ui.register;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.eventschill.MainActivity;
import com.example.eventschill.R;
import com.example.eventschill.Server;
import com.example.eventschill.data.model.LoggedInUser;

import java.util.ArrayList;
import java.util.HashMap;

public class SignUpPhotoActivity extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String age;
    private String watch;
    private String bio;
    private String answerSheet;

    private ImageView userPhoto;
    private ProgressBar progressBar;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMAGE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_photo);
        checkPermissions();
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        age = getIntent().getStringExtra("age");
        watch = getIntent().getStringExtra("watch");
        bio = getIntent().getStringExtra("bio");
        answerSheet = getIntent().getStringExtra("answerSheet");

        userPhoto = findViewById(R.id.photo);
        progressBar = findViewById(R.id.loading);
        progressBar.setVisibility(View.GONE);

        Button takePhoto = findViewById(R.id.takePhotoButton);
        takePhoto.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });


        Button fromLibrary = findViewById(R.id.libraryPhotoButton);
        fromLibrary.setOnClickListener(view -> {
            checkPermissions();
            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });

        Button finish = findViewById(R.id.finishButton);
        finish.setOnClickListener(view -> {
            if (userPhoto.getDrawable() == null) {
                Toast.makeText(getApplicationContext(), "Please Upload a Photo.", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.VISIBLE);
            checkPermissions();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            LoggedInUser.User user = new LoggedInUser().new User(username, password,
                    firstName + " " + lastName, age, watch, bio, ((BitmapDrawable) userPhoto.getDrawable()).getBitmap(),
                    longitude, latitude, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), answerSheet);
            new Server().getServerInterface().createUser(user,
                    data -> {
                        if (data.optString("Status", "").equals("Error")) {
                            Toast.makeText(getApplicationContext(), "Sorry, we can't register you, try again later.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        } else {

                            progressBar.setVisibility(View.INVISIBLE);
                            LoggedInUser.user = user;
                            Intent intent = new Intent(SignUpPhotoActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, error -> {
                        Toast.makeText(getApplicationContext(), "Sorry, we can't register you, try again later.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    });

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            userPhoto.setImageBitmap(imageBitmap);
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // String picturePath contains the path of selected Image
            userPhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    private void checkPermissions(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1051);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted.

                } else {
                    // Permission denied - Show a message to inform the user that this app only works
                    // with these permissions granted
                }
                return;
            }
            case 1051: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

        }
    }
}
