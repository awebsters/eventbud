package com.example.eventschill.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventschill.R;

public class SignUpQuestionaireActivity extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String age;
    private String watch;
    private String bio;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_questionaire);

        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        age = getIntent().getStringExtra("age");
        watch = getIntent().getStringExtra("watch");
        bio = getIntent().getStringExtra("bio");

        RadioGroup q1 = findViewById(R.id.radioGroup);
        RadioGroup q2 = findViewById(R.id.radioGroup2);
        RadioGroup q3 = findViewById(R.id.radioGroup3);
        RadioGroup q4 = findViewById(R.id.radioGroup4);
        RadioGroup q5 = findViewById(R.id.radioGroup5);
        RadioGroup q6 = findViewById(R.id.radioGroup6);
        RadioGroup q7 = findViewById(R.id.radioGroup7);
        RadioGroup q8 = findViewById(R.id.radioGroup8);
        RadioGroup q9 = findViewById(R.id.radioGroup9);
        RadioGroup q10 = findViewById(R.id.radioGroup10);

        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (q1.getCheckedRadioButtonId() == -1 || q2.getCheckedRadioButtonId() == -1 ||
                        q3.getCheckedRadioButtonId() == -1 || q4.getCheckedRadioButtonId() == -1 ||
                        q5.getCheckedRadioButtonId() == -1 || q6.getCheckedRadioButtonId() == -1 ||
                        q7.getCheckedRadioButtonId() == -1 || q8.getCheckedRadioButtonId() == -1 ||
                        q9.getCheckedRadioButtonId() == -1 || q10.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SignUpQuestionaireActivity.this, SignUpPhotoActivity.class);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("lastName", lastName);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("age", age);
                    intent.putExtra("watch", watch);
                    intent.putExtra("bio", bio);


                    String answerSheet = "";
                    answerSheet += findViewById(q1.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q2.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q3.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q4.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q5.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q6.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q7.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q8.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q9.getCheckedRadioButtonId()).getTag();
                    answerSheet += findViewById(q10.getCheckedRadioButtonId()).getTag();


                    intent.putExtra("answerSheet", answerSheet);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
