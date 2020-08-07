package com.example.eventschill.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventschill.R;

public class SignUpDetailsActivity extends AppCompatActivity {
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    private EditText age;
    private EditText watch;
    private EditText bio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_details);
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");


        age = findViewById(R.id.age);
        watch = findViewById(R.id.show);
        bio = findViewById(R.id.bio);

        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (age.getText().toString().isEmpty() || watch.getText().toString().isEmpty()
                        || bio.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SignUpDetailsActivity.this, SignUpQuestionaireActivity.class);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("lastName", lastName);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("age", age.getText().toString());
                    intent.putExtra("watch", watch.getText().toString());
                    intent.putExtra("bio", bio.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
