package com.engage.simonnewham.engageapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.activities.ContentActivity;
import com.engage.simonnewham.engageapp.models.NewsItem;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.InputStream;

/**
 * Class to handle the downloading of images to display to user
 * Makes use of PhotoView from https://github.com/chrisbanes/PhotoView
 * @author simonnewham
 */
public class ImageFragment extends Fragment {

    PhotoView image;
    private String path;
    ProgressBar progressBar;

    private TextView title;
    private TextView date;

    public ImageFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        String itemPath = this.getArguments().getString("path");
        NewsItem item = (NewsItem) this.getArguments().getSerializable("Item");

        View view =inflater.inflate(R.layout.fragment_image, container, false);
        image = view.findViewById(R.id.Image);
        progressBar = view.findViewById(R.id.progressContent);
        title = view.findViewById(R.id.titleFixed);
        date = view.findViewById(R.id.dateFixed);

        title.setText(item.getName());
        date.setText("Uploaded on: "+item.getDate());
        //download image from download path provided
        new ImageFragment.DownloadImageTask(image).execute("https://engage.cs.uct.ac.za"+itemPath);

        return view;
    }

    /**
     * Downloads image from the server and displays it to user
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        PhotoView bmImage;

        public DownloadImageTask(PhotoView bmImage) {
            this.bmImage = bmImage;
        }

        //download image from server
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        //display image to user
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.GONE);
            bmImage.setImageBitmap(result);
        }
    }
}
