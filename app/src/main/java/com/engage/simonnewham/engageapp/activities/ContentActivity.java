package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.adapters.SectionsStatePagerAdapter;
import com.engage.simonnewham.engageapp.fragments.AudioFragment;
import com.engage.simonnewham.engageapp.fragments.ConsentFragment;
import com.engage.simonnewham.engageapp.fragments.ImageFragment;
import com.engage.simonnewham.engageapp.fragments.SignUpFragment;
import com.engage.simonnewham.engageapp.fragments.VideoFragment;
import com.engage.simonnewham.engageapp.models.NewsItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author simonnewham
 * Class to show individual news items downloaded from server
 * Text items are rendered within the activity
 * Image, Audio and Video items are rendered within their own fragment
 */

public class ContentActivity extends AppCompatActivity {

    private final String TAG = "ContentActivity";


    TextView title;
    TextView date;
    TextView titleF;
    TextView dateF;
    TextView content;
    Button surveyB;

    String email;
    String user_group;
    NewsItem item;

    private ProgressBar progressBar;
    private ScrollView textScroll;
    private FrameLayout frameLayout;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        title = (TextView) findViewById(R.id.title);
        date = (TextView) findViewById(R.id.date);
        content = (TextView) findViewById(R.id.Text);
        textScroll = findViewById(R.id.text_scroll);
        progressBar = findViewById(R.id.progressBar);
        surveyB = findViewById(R.id.surveyButton);
        titleF = findViewById(R.id.titleFixed);
        dateF = findViewById(R.id.dateFixed);
        frameLayout = findViewById(R.id.container);

        //get Extra
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");
            item = (NewsItem) getIntent().getSerializableExtra("News");
        }

        //set item on background thread?
        setItem();
    }


    /**
     * Method to set up the news article within the android view
     */
    public void setItem(){

        String type = item.getType().toUpperCase();
        progressBar.setVisibility(View.VISIBLE);

        if(type.equals("IMAGE")){
            //set date and title
            frameLayout.setVisibility(View.VISIBLE);
            titleF.setVisibility(View.VISIBLE);
            dateF.setVisibility(View.VISIBLE);
            dateF.setText("Uploaded on: "+item.getDate());
            titleF.setText(item.getName());

            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.container, new ImageFragment(item.getPath()));
            transaction.commit();

            progressBar.setVisibility(View.GONE);

        }
        else if(type.equals("VIDEO")){
            frameLayout.setVisibility(View.VISIBLE);
            titleF.setVisibility(View.VISIBLE);
            dateF.setVisibility(View.VISIBLE);
            dateF.setText("Uploaded on: "+item.getDate());
            titleF.setText(item.getName());

            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.container, new VideoFragment(item.getPath()));
            transaction.commit();
            progressBar.setVisibility(View.GONE);

        }
        else if(type.equals("TEXT")){
            textScroll.setVisibility(View.VISIBLE);
            title.setText(item.getName());
            date.setText("Uploaded on: "+item.getDate());
            content.setVisibility(View.VISIBLE);

            new DownloadTextTask(content).execute("https://engage.cs.uct.ac.za"+item.getPath());

        }
        else if(type.equals("AUDIO")){
            frameLayout.setVisibility(View.VISIBLE);
            titleF.setVisibility(View.VISIBLE);
            dateF.setVisibility(View.VISIBLE);
            dateF.setText("Uploaded on: "+item.getDate());
            titleF.setText(item.getName());

            //set fragment to audio version

            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.container, new AudioFragment(item.getPath()));
            transaction.commit();
            progressBar.setVisibility(View.GONE);
        }

    }

    public boolean onSupportNavigateUp() {

        Intent intent = new Intent(ContentActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("group", user_group);
        startActivity(intent);
        finish();
        return true;
    }

    public void loadSurvey(View view){

        Intent intent = new Intent(ContentActivity.this, SurveyActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("group", user_group);
        intent.putExtra("surveyID", "ITEM");
        intent.putExtra("News", item);
        startActivity(intent);
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
            progressBar.setVisibility(View.GONE);
            textView.setText(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    /**
     * Method responsible for handling events on the toolbar
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.home:

                intent = new Intent(ContentActivity.this, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                //finish();
                return true;
            case R.id.about:

                intent = new Intent(ContentActivity.this, AboutActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                //finish();
                return true;
            case R.id.logout:

                intent = new Intent(ContentActivity.this, SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Boolean removeFragment(){

        String type = item.getType().toUpperCase();

        if(type.equals("AUDIO")){

        }
        else if(type.equals("IMAGE")){

        }
        else if(type.equals("VIDEO")){

        }
        return true;

    }
}
