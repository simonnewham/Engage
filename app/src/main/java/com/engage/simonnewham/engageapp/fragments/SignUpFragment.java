package com.engage.simonnewham.engageapp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;
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
import java.util.Random;

/**
 * Class to handle the signing up of new users
 * @author simonnewham
 */
public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    //shared preference code
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private UserSignUpTask mAuthTask;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPassword2;
    private Button signUp;
    private Button cancel;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mEmail = view.findViewById(R.id.email);
        mPassword = view.findViewById(R.id.password);
        mPassword2 = view.findViewById(R.id.password2);
        signUp = view.findViewById(R.id.signup);
        cancel = view.findViewById(R.id.cancel);
        progressBar = view.findViewById(R.id.progressUser);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //method when sign up button is pressed
        signUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                onSignUp();
            }
        });

        //Method when cancel button is pressed
        cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                ((SignUpActivity)getActivity()).setViewPager(0);
            }
        });
        return view;
    }

    public void onSignUp() {

        Boolean error = false;
        //get values from form
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String password2 = mPassword2.getText().toString();

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
            mAuthTask = new SignUpFragment.UserSignUpTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    //Check if inputted email is valid
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Connects to server to upload new user details
     */
    public class UserSignUpTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mGroup;

        UserSignUpTask(String email, String password) {
            progressBar.setVisibility(View.VISIBLE);
            mEmail = email;
            mPassword = password;

            //random group between 1 and 3
            Random random = new Random();
            int val = random.nextInt(3) +1;
            mGroup = String.valueOf(val);
        }

        /**
         * Connect to API to signUp new user
         * User details sent as parameters
         */
        @Override
        protected String doInBackground(Void... params) {

            try{
                URL url = new URL("https://engage.cs.uct.ac.za/android/new_user"); //will return "Login Success:<user_group>"

                HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
                Log.i(TAG, "Connection established");

                httpConn.setRequestMethod("POST");

                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                OutputStream opStream = httpConn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(opStream, "UTF-8"));

                //Send POST message
                String postData = URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(mEmail, "UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(mPassword, "UTF-8")+"&"+
                        URLEncoder.encode("user_group", "UTF-8")+"="+URLEncoder.encode(mGroup, "UTF-8");

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
        //Method to display baseline survey on successful sign up or error
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            progressBar.setVisibility(View.GONE);

            if (result.startsWith("SignUp Success")) {
                Log.i(TAG, "SUCCESS");
                String mGroup ="";

                if(result.contains(":")){
                    int index = result.indexOf(":");
                    mGroup = result.substring(index+1);
                }

                //Store email and user_group in stored preferences
                mEditor = mPreferences.edit();
                mEditor.putString("email", mEmail);
                mEditor.commit();
                mEditor.putString("user_group", mGroup);
                mEditor.commit();
                mEditor.putString("baseline", "0");
                mEditor.commit();

                //load the next activity
                Intent intent = new Intent(getActivity(), SurveyActivity.class);
                intent.putExtra("email", mEmail);
                intent.putExtra("group", mGroup);
                intent.putExtra("surveyID", "BASELINE"); //will change for http connection
                startActivity(intent);

            }
            else {
                Toast.makeText(getActivity(), "SignUp error: Email already in use",Toast.LENGTH_SHORT).show();
            }
        }
    }
}