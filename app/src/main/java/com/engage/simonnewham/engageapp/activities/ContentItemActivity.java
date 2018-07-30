package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.engage.simonnewham.engageapp.R;

/**
 * Created by simonnewham on 2018/07/27.
 */

public class ContentItemActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list_item);

    }

    public void loadNews(View view){

        Intent intent = new Intent(this, ContentActivity.class);
        startActivity(intent);
        finish();

    }
}
