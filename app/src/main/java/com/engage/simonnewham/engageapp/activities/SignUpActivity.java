package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;

/**
 * Class for when new user signs up
 *
 */

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText password2;
    private EditText mGroup;

    private final String TAG = "UserSignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mGroup = (EditText) findViewById(R.id.user_group);
    }

    public void onSignUp(View view) {

        Boolean error = false;

        //get values from form
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
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
        else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            error = true;
        }

        //access to DB


        //TRACING


        //if successful load mainActivity
        if(error == false) {
            Log.i(TAG, "****SignUp details**** Email:"+email+"  Password:"+password+" Group:"+group);
            Toast.makeText(this, "Sign Up Success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SurveyActivity.class);
            intent.putExtra("surveyID", "baseline");
            startActivity(intent);
            finish();
        }
        else{

        }

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
