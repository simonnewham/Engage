package com.engage.simonnewham.engageapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.activities.SignIn;
import com.engage.simonnewham.engageapp.activities.SignUpActivity;

/**
 * Class responsible for consent fragment
 * @author simonnewham
 */

public class ConsentFragment extends Fragment {

    private static final String TAG = "ConsentFragment";
    private Button accept;
    private Button reject;

    public ConsentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consent, container, false);
        accept = view.findViewById(R.id.buttonAccept);
        reject = view.findViewById(R.id.butonReject);

        accept.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                ((SignUpActivity)getActivity()).setViewPager(1);
            }
        });
        reject.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent (getActivity(), SignIn.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
