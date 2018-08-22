package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.engage.simonnewham.engageapp.R;
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
 */

public class ContentActivity extends AppCompatActivity {

    private final String TAG = "ContentActivity";

    TextView title;
    TextView date;
    TextView content;
    ImageView image;
    VideoView video;

    String email;
    String user_group;
    NewsItem item;

    MediaPlayer mediaPlayer;
    private VideoView audio;

    private ScrollView textScroll;

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
        image = findViewById(R.id.Image);
        video = (VideoView) findViewById(R.id.Video);
        textScroll = findViewById(R.id.text_scroll);

        //get Extra
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");
            item = (NewsItem) getIntent().getSerializableExtra("News");
        }

        setItem();
    }

    /**
     * Method to set up the news article within the android view
     */
    public void setItem(){

        title.setText(item.getName());
        date.setText("Uploaded on: "+item.getDate());
        String type = item.getType().toUpperCase();


        if(type.equals("IMAGE")){
            textScroll.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            new DownloadImageTask(image).execute("https://engage.cs.uct.ac.za"+item.getPath());

        }
        else if(type.equals("VIDEO")){
            String vidAddress = "https://engage.cs.uct.ac.za"+item.getPath();
            Uri vidUri = Uri.parse(vidAddress);
            MediaController vidControl = new MediaController(this);
            vidControl.setAnchorView(video);
            video.setMediaController(vidControl);
            video.setVisibility(View.VISIBLE);
            video.setVideoURI(vidUri);
            video.start();

        }
        else if(type.equals("TEXT")){
            textScroll.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
            new DownloadTextTask(content).execute("https://engage.cs.uct.ac.za"+item.getPath());

        }
        else if(type.equals("AUDIO")){
            video.setVisibility(View.VISIBLE);
            //video.setBackground(ContextCompat.getDrawable(this, R.drawable.logo2));
            video.setBackgroundResource(R.drawable.ic_music);
            MediaController mediaController = new MediaController(this){
                @Override
                public void hide() {
                //Do not hide.
                }
            };
            mediaController.setAnchorView(video);
            Uri uri = Uri.parse("https://engage.cs.uct.ac.za"+item.getPath());
            video.setMediaController(mediaController);
            video.setVideoURI(uri);
            video.start();
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

        Toast.makeText(this, "Load Survey clicked", Toast.LENGTH_SHORT).show();
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
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

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

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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
                finish();
                return true;
            case R.id.about:
                intent = new Intent(ContentActivity.this, AboutActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                finish();
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
}
