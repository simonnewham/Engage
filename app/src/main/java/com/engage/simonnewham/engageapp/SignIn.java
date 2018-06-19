package com.engage.simonnewham.engageapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

/**
 * Login screen that allows users to login or create new account
 * Adapted from Android Studio login template
 */
public class SignIn extends AppCompatActivity {

    //login task
    private UserLoginTask mAuthTask = null;
    private final String TAG = this.getClass().getSimpleName();

    //UI components
    private EditText mEmailView;
    private EditText mPasswordView;
    //private View mProgressView;
    private View mLoginFormView; //no code included yet for progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //set up login form
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);

    }
    /**
     * Method called when login button is clicked
     * If errors occur then no login attempt is made
     */
    public void onLogin(View view) {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address and password.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        //empty password
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError("Password Required");
            focusView = mEmailView;
            cancel=true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    /**
     * Method called to sign up new user
     */
    public void SignUp(View view) {

        //Will need to load the baseline questionaire for new users

    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String loginUrl = "http://engage.cs.uct.ac.za/android/login";

            //access database
            Log.i(TAG, "Connection open");

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

            if (success == Boolean.TRUE) {

                Intent intent = new Intent(SignIn.this, MainActivity.class);
                intent.putExtra("email", mEmail);
                startActivity(intent);

                finish();
            }
            else {
                mPasswordView.setError("Incorrect Password");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }
}