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
import android.widget.TextView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.activities.ContentActivity;
import com.engage.simonnewham.engageapp.activities.SignUpActivity;
import com.engage.simonnewham.engageapp.models.NewsItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


@SuppressLint("ValidFragment")
public class TextFragment extends Fragment {

    TextView title;
    TextView date;
    TextView content;

    NewsItem item;

    public TextFragment(NewsItem item) {
        this.item = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        title = view.findViewById(R.id.title);
        date = view.findViewById(R.id.date);
        content = view.findViewById(R.id.Text);

        title.setText(item.getName());
        date.setText("Uploaded on: "+item.getDate());

        new DownloadTextTask(content).execute("https://engage.cs.uct.ac.za"+item.getPath());

        return view;
    }


    /**
     *
     */
    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        TextView textView;

        public DownloadTextTask(TextView textView) {
            this.textView = textView;
        }

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

        protected void onPostExecute(String result) {
            textView.setText(result);
            ((ContentActivity)getActivity()).hideProgress();
        }
    }


}
