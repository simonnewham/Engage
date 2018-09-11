package com.engage.simonnewham.engageapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.activities.MainActivity;
import com.engage.simonnewham.engageapp.activities.SignUpActivity;
import com.engage.simonnewham.engageapp.activities.SurveyActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Login fragment that allows users to login or create new account
 * Adapted from Android Studio login template
 * @author simonnewham
 */
public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    //shared preference code
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    //login task
    private UserLoginTask mAuthTask;

    //UI components
    private EditText mEmailView;
    private EditText mPasswordView;
    private ProgressBar progressBar;
    private Button login;
    private Button signUp;
    private TextView forgot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();
        checkPrefs();

        mEmailView = view.findViewById(R.id.email);
        mPasswordView = view.findViewById(R.id.password);
        progressBar = view.findViewById(R.id.progressBar);
        login = view.findViewById(R.id.login);
        signUp = view.findViewById(R.id.SignUp);
        forgot = view.findViewById(R.id.forgotPassword);

        //onClick listeners
        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                onLogin();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                onSignUp();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                onForgot();
            }
        });

        return view;
    }

    /**
     * Method to check if user has logged in before, if they have skip login procedure
     */
    public void checkPrefs(){
        String email = mPreferences.getString("email", "none");
        String user_group = mPreferences.getString("user_group", "none");
        String baseline = mPreferences.getString("baseline", "1");

        Log.i(TAG, "User "+email);
        Log.i(TAG, "Baseline "+baseline);

        //if baseline equals 0 the user has not completed the baseline survey yet
        if(baseline.equals("0")){
            Intent intent = new Intent(getActivity(), SurveyActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("group", user_group);
            intent.putExtra("surveyID", "BASELINE"); //will change for http connection
            startActivity(intent);

        }
        else if(!email.equals("none") && !user_group.equals("none")){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("group", user_group);
            intent.putExtra("load", "online");
            startActivity(intent);
            getActivity().finish();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Method called when login button is clicked
     * If errors occured then no login attempt is made
     */
    public void onLogin() {

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
            focusView.requestFocus();
        }
        else {
            // Show a progress spinner, and start login process
            progressBar.setVisibility(View.VISIBLE);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Method to switch to sign up fragment
     */
    public void onSignUp() {
        ((SignUpActivity)getActivity()).setViewPager(2);
    }

    /**
     * Method to switch to forgot password fragment
     */
    public void onForgot() {
        ((SignUpActivity)getActivity()).setViewPager(1);
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
                URL url = new URL("https://engage.cs.uct.ac.za/android/login"); //will return "Login Success:<user_group>"

                HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
                Log.i(TAG, "Connection established");

                httpConn.setRequestMethod("POST");

                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                OutputStream opStream = httpConn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(opStream, "UTF-8"));

                //Send POST message
                String postData = URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(mEmail, "UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(mPassword, "UTF-8");

                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                opStream.close();

                //Receive result of post message
                InputStream inputStream = httpConn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpConn.disconnect();

                Log.i(TAG, ">>>>>Response Result: "+result);
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
        //Method to check if login was successful and load the next activity
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            progressBar.setVisibility(View.GONE);

            if (result.startsWith("Login Success")) {
                Log.i(TAG, "SUCCESS");
                String mGroup ="";

                if(result.contains(":")){
                    int index = result.indexOf(":");
                    mGroup = result.substring(index+1);
                }

                //store user details to avoid login
                mEditor.putString("email", mEmail);
                mEditor.commit();
                mEditor.putString("user_group", mGroup);
                mEditor.commit();

                //TRACING
                //String test = mPreferences.getString("email","test");
                //Log.i(TAG, "User "+test);

                Intent intent = new Intent( getActivity(), MainActivity.class);
                intent.putExtra("email", mEmail);
                intent.putExtra("group", mGroup);
                intent.putExtra("load", "online");
                startActivity(intent);
                getActivity().finish();
            }
            else {
                mPasswordView.setError("Incorrect Password");
                mPasswordView.requestFocus();
            }
        }
    }
}
