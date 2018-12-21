/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// this is the GrappIt version

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  Boolean signUpModeActive = true;
  TextView loginTextView;
  EditText usernameEditText;
  EditText emailEditText;
  EditText passwordEditText;

  String username = "";
  String email = "";
  String password = "";

  public void showGrappList() {
    Intent intent = new Intent(getApplicationContext(), GrappListActivity.class);
    startActivity(intent);
  }

  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    if ( i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
      signUpClicked(view);
    }
    return false;
  }

  public void onClick(View view) {
    if (view.getId() == R.id.loginTextView) {

      Button signUpButton = (Button) findViewById(R.id.signUpButton);

      if(signUpModeActive) {
        signUpModeActive = false;
        signUpButton.setText("Login");
        loginTextView.setText("or Sign Up");
        emailEditText.setVisibility(View.INVISIBLE);

      } else {
        signUpModeActive = true;
        signUpButton.setText("Sign Up");
        loginTextView.setText("or Login");
        emailEditText.setVisibility(View.VISIBLE);
      }
    } else if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  public void signUpClicked(View view) {

      username = usernameEditText.getText().toString();
      email = emailEditText.getText().toString();
      password = passwordEditText.getText().toString();


      if(signUpModeActive) {

        if(username.matches("") || email.matches("") || password.matches("")) {
          Toast.makeText(MainActivity.this, "All fields are required. Please try again.", Toast.LENGTH_SHORT).show();
        }

        ParseUser user = new ParseUser();

        user.setUsername(usernameEditText.getText().toString());
        user.setEmail(emailEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("Sign Up", "Success");
              showGrappList();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      } else {

        if(username.matches("") || password.matches("")) {
          Toast.makeText(MainActivity.this, "All fields are required. Please try again.", Toast.LENGTH_SHORT).show();
        }

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user != null) {
                    Log.i("Login", "Ok");
                    showGrappList();
                } else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
      }
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("GrappIt");

    loginTextView = (TextView) findViewById(R.id.loginTextView);
    loginTextView.setOnClickListener(this);
    emailEditText = (EditText) findViewById(R.id.emailEditText);
    usernameEditText = (EditText) findViewById(R.id.usernameEditText);
    passwordEditText = (EditText) findViewById(R.id.passwordEditText);

    ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);
    ConstraintLayout background = (ConstraintLayout) findViewById(R.id.backgroundLayout);

    logoImageView.setOnClickListener(this);
    background.setOnClickListener(this);

    passwordEditText.setOnKeyListener(this);

    // if user is already logged in, proceed...
    if( ParseUser.getCurrentUser() != null ) {
        showGrappList();
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}