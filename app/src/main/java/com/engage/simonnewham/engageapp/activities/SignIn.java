package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.engage.simonnewham.engageapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Login screen that allows users to login or create new account
 * Adapted from Android Studio login template
 */

public class SignIn extends AppCompatActivity {

    //login task
    private UserLoginTask mAuthTask;
    private final String TAG = "UserSignIn";

    //UI components
    private EditText mEmailView;
    private EditText mPasswordView;
    //private View mProgressView;
    private View mLoginFormView; //no code included yet for progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
        //empty password
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError("Password Required");
            focusView = mEmailView;
            cancel=true;
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
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

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();

    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private String mResponse;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            mResponse="";
        }

        /**
         * Connect to API to verify login details
         */
        @Override
        protected String doInBackground(Void... params) {

            try{
                URL url = new URL("http://engage.cs.uct.ac.za/android/login"); //will return "Login Success:<user_group>"

                HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
                Log.i(TAG, "Connection established");

                httpConn.setRequestMethod("POST");

                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                //OutputStream opStream = httpConn.getOutputStream();
                //BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(opStream, "UTF-8"));

                // ***** Send POST message *****
                String postData = URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(mEmail, "UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(mPassword, "UTF-8");

                httpConn.getOutputStream().write(postData.getBytes());
               // bufferedWriter.write(postData);
               // bufferedWriter.flush();
               // bufferedWriter.close();
               // opStream.close();

                // ***** Receive result of post message *****
                InputStream inputStream = httpConn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                //store group number to download content
               // mResponse = result;

                bufferedReader.close();
                inputStream.close();
                httpConn.disconnect();

                Log.i(TAG, ">>>>>Response Result: "+result);
                //>>>TESTING<<<<
                result = "Login Success:1";

                Log.i(TAG, "Response Result: "+result);
                return result;

            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        //runs after doInBackground
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            //showProgress(false);

           if (result.startsWith("Login Success")) {
                Log.i(TAG, "SUCCESS");
                String mGroup ="";

                if(result.contains(":")){
                    int index = result.indexOf(":");
                    mGroup = result.substring(index);
                }

                Intent intent = new Intent(SignIn.this, MainActivity.class);
                intent.putExtra("email", mEmail);
                intent.putExtra("group", mGroup);
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