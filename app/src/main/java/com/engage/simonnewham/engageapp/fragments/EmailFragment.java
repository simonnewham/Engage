package com.engage.simonnewham.engageapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
 * Class responsible for sending password to users email if they forget their password
 * Email sent as parameter to the server via a post request
 * @author simonnewham
 */
public class EmailFragment extends Fragment {

    private static final String TAG = "EmailFragment";

    private Button send;
    private Button back;
    private TextView text;
    private EditText email;
    private UserEmailTask userEmailTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_email, container, false);

        send = view.findViewById(R.id.buttonSend);
        back = view.findViewById(R.id.buttonBack);
        text = view.findViewById(R.id.textViewConfirm);
        email = view.findViewById(R.id.email);

        send.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                if(email.getText().length()==0){
                    email.setError("PLease enter a valid email");
                }
                else{
                    String toSend = email.getText().toString();
                    userEmailTask = new UserEmailTask(toSend);
                    userEmailTask.execute((Void) null);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                ((SignUpActivity)getActivity()).setViewPager(0);
            }
        });

        return view;
    }

    //Thread to call forgot_password on API to send user password
    public class UserEmailTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;

        UserEmailTask(String email) {
            mEmail = email;
        }

        /**
         * Connect to API to send password to inputted email address
         */
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL("https://engage.cs.uct.ac.za/android/forgot_password");

                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                httpConn.setRequestMethod("POST");

                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                OutputStream opStream = httpConn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(opStream, "UTF-8"));

                //Send POST message
                String postData = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(mEmail, "UTF-8");

                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                opStream.close();

                //Receive result of post message
                InputStream inputStream = httpConn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpConn.disconnect();

                Log.i(TAG, "Response Result: " + result);

                return result;

            } catch (UnsupportedEncodingException e) {
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
        //Display success message or error
        protected void onPostExecute(final String result) {
            userEmailTask = null;

            if (result.equals("Email Success")) {
                Log.i(TAG, "Email Sent");
                text.setText("Thank you, your password has been sent to "+mEmail);

            } else {
                Toast.makeText(getActivity(), "Error with email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
