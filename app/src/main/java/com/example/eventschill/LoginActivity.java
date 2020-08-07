package com.example.eventschill;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.eventschill.data.model.LoggedInUser;
import com.example.eventschill.ui.register.BasicSignUpInfoActivity;

public class LoginActivity extends AppCompatActivity {

    private LoggedInUser user = new LoggedInUser();
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ProgressBar loadingProgressBar;
    private Boolean saveLogin;

    private Server server;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText)findViewById(R.id.time);
        passwordEditText = findViewById(R.id.lastName);
        loadingProgressBar = findViewById(R.id.loading);
        final Button loginButton = findViewById(R.id.next);
        final TextView registerButton = findViewById(R.id.register);
        server = new Server(getApplicationContext());

        user.attemptStatus().observe(this, new Observer<LoggedInUser.AttemptStatus>() {
            @Override
            public void onChanged(LoggedInUser.AttemptStatus attemptStatus) {
                switch (attemptStatus){
                    case NONE:
                        loginButton.setEnabled(true);
                        loadingProgressBar.setVisibility(View.GONE);
                        break;
                    case IN_PROGRESS:
                        loginButton.setEnabled(false);
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        loginButton.setEnabled(false);
                        loadingProgressBar.setVisibility(View.GONE);
                        loginPassed();
                        break;
                    case FAILED:
                        loginButton.setEnabled(true);
                        loadingProgressBar.setVisibility(View.GONE);
                        showLoginFailed(R.string.invalid_sign_in);
                        break;
                }
            }
        });

        // Login when enter is pressed on password box
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, BasicSignUpInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Login with button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void attemptLogin(){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        user.login(username, password);
    }

    private void loginPassed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
