package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.models.User;
import com.google.gson.Gson;

/**
 * Class for when new user signs up
 *
 */

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mPassword2;
    private EditText mGroup;

    private final String TAG = "UserSignUp";

    User new_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mPassword2 = (EditText) findViewById(R.id.password2);
        mGroup = (EditText) findViewById(R.id.user_group);
    }

    public void onSignUp(View view) {

        Boolean error = false;

        //get values from form
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String password2 = mPassword2.getText().toString();
        String group = mGroup.getText().toString();

        // no email entered
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            error = true;

        }
        //empty password
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Password Required");
            error=true;
        }
        if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            error = true;
        }
        if(!password2.equals(password)){
            mPassword.setError("Passwords do not match");
            mPassword2.setError("Passwords do not match");
            error = true;
        }


        //if successful load mainActivity
        if(error == false) {

            //create user JSON document and send to DB
            new_user = new User(email, password, group);
            Gson gson = new Gson();
            String json = gson.toJson(new_user);

            //Log.i(TAG, "****SignUp details**** Email:"+email+"  Password:"+password+" Group:"+group);
            Log.i(TAG, "****JSON user**** Email:"+json);
            Toast.makeText(this, "JSON"+json, Toast.LENGTH_LONG).show();
            loadSurvey();

        }
        else{

        }

    }

    public void loadSurvey(){

        Intent intent = new Intent(this, SurveyActivity.class);
        intent.putExtra("email", new_user.getEmail());
        intent.putExtra("group", new_user.getUser_group());
        intent.putExtra("surveyID", "BASELINE");
        startActivity(intent);
        finish();
    }


    public void onCancel(View view) {
        Toast.makeText(this, "Sign Up Cancelled", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
        finish();

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


}
