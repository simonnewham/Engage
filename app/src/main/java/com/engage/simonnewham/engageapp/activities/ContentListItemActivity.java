package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.engage.simonnewham.engageapp.R;

/**
 * Class to create the News List Item within the MainActivity
 * @author simonnewham
 */

public class ContentListItemActivity extends AppCompatActivity {

    private final String TAG = "NewsListItem";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list_item);

    }

}
