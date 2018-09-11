package com.engage.simonnewham.engageapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.activities.ContentActivity;

/**
 * Class to download and display video items to users
 * @author simonnewham
 */
@SuppressLint("ValidFragment")
public class VideoFragment extends Fragment {

    VideoView video;
    private String path;
    ProgressBar progressBar;

    public VideoFragment (String path){
        this.path = path;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        video = view.findViewById(R.id.Video);
        progressBar = view.findViewById(R.id.progressContent);

        //download video from provided item path
        String vidAddress = "https://engage.cs.uct.ac.za"+path;
        Uri vidUri = Uri.parse(vidAddress);
        MediaController vidControl = new MediaController(getActivity());
        vidControl.setAnchorView(video);
        video.setMediaController(vidControl);
        video.setVisibility(View.VISIBLE);
        video.setVideoURI(vidUri);
        progressBar.setVisibility(View.GONE);
        video.start();

        return view;
    }
}
