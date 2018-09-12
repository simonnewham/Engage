package com.engage.simonnewham.engageapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.activities.ContentActivity;
import com.engage.simonnewham.engageapp.activities.SignUpActivity;
import com.engage.simonnewham.engageapp.models.NewsItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Class to download text articles from the server
 * @author simonnewham
 */
public class TextFragment extends Fragment {

    TextView title;
    TextView date;
    TextView content;
    ProgressBar progressBar;

    public TextFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String itemPath = this.getArguments().getString("path");
        String titleT = this.getArguments().getString("title");
        String dateT = this.getArguments().getString("date");

        View view = inflater.inflate(R.layout.fragment_text, container, false);
        title = view.findViewById(R.id.title);
        date = view.findViewById(R.id.date);
        content = view.findViewById(R.id.Text);
        progressBar = view.findViewById(R.id.progressContent);

        title.setText(titleT);
        date.setText("Uploaded on: "+dateT);

        new DownloadTextTask(content).execute("https://engage.cs.uct.ac.za"+itemPath);

        return view;
    }

    /**
     * Download text item from the server and display to user
     */
    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        TextView textView;

        public DownloadTextTask(TextView textView) {
            this.textView = textView;
        }

        //download of text item from server
        protected String doInBackground(String... urls) {
            String urldisplay = urls[0];

            try {
                URL url = new URL(urldisplay);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String result="";
                String line="";
                while ((line = in.readLine()) != null) {
                    result +=line;
                }
                in.close();
                return result;

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return "";
        }

        //display to user
        protected void onPostExecute(String result) {
            textView.setText(result);
            progressBar.setVisibility(View.GONE);
        }
    }
}
