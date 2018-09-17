package com.engage.simonnewham.engageapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.adapters.NewsAdapter;
import com.engage.simonnewham.engageapp.models.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import java.util.ArrayList;

/**
 * User home page with list of news items
 * Page will load from online server when the app is launched or offline storage when the app is running
 * Load particular news item when user clicks on item
 * @author simonnewham
 */

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    //shared preference code
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private ListView news_list;
    private NewsAdapter newsAdapter;
    private LinearLayout main_panel;
    private ProgressBar progressBar;
    private TextView info;
    private String email;
    private String user_group;
    private String load;
    private TextView textProgress;

    private ContentDownload contentDownload;
    ArrayList<NewsItem> store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get extra info to load personalised content
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");
            load = extras.getString("load");
        }

        main_panel = findViewById(R.id.main_panel);
        progressBar = findViewById(R.id.progressBar);
        info = findViewById(R.id.textInfo);
        textProgress = findViewById(R.id.textProgress);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(load == null){
            //offlineDL
            Log.i(TAG, "Loading from offline");
            offlineDownload();
        }
        else{
            //onlineDL
            Log.i(TAG, "Loading from online");
            onlineDownload();
        }
    }

    /**
     * Method to display downloaded news items in a List View
     * Uses the NewsAdapter class to create each item
     * @param toDisplay
     */
    public void displayNews(ArrayList<NewsItem> toDisplay){

        store = toDisplay;
        news_list = findViewById(R.id.news_list);
        newsAdapter = new NewsAdapter(this, toDisplay);
        news_list.setAdapter(newsAdapter);

        news_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                intent.putExtra("News", store.get(position));
                startActivity(intent);
            }
        });
    }

    //load news from internal storage, used once content has been updated
    public void offlineDownload(){
        String newsList = readFromFile(getApplicationContext());
        //Log.i(TAG, "Offline news "+newsList);
        displayNews(getNews(newsList));
        progressBar.setVisibility(View.GONE);
        textProgress.setVisibility(View.GONE);
        info.setVisibility(View.VISIBLE);
        info.setTextSize(10);
        info.setText("Loaded offline, press the home icon to refresh");
    }

    //load news from server, used when user opens app for first time
    public void onlineDownload(){
        contentDownload = new ContentDownload(user_group);
        contentDownload.execute((Void) null);
    }

    /**
     * Represents an asynchronous task used to download news items and convert to NewsItem POJO
     * the user.
     */
    public class ContentDownload extends AsyncTask<Void, Void, String> {

        private final String mGroup;

        ContentDownload(String user_group) {
            mGroup = user_group;
        }

        /**
         * Connect to API to download news content for specific group
         */
        @Override
        protected String doInBackground(Void... params) {

            try{
                URL url = new URL("https://engage.cs.uct.ac.za/android/get_content"); //will return "Login Success:<user_group>"

                HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
                Log.i(TAG, "Connection established");

                httpConn.setRequestMethod("POST");

                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                OutputStream opStream = httpConn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(opStream, "UTF-8"));

                //Send POST message
                String postData = URLEncoder.encode("user_group", "UTF-8")+"="+URLEncoder.encode(mGroup, "UTF-8");

                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                opStream.close();

                // Should receive raw JSON of survey
                InputStream inputStream = httpConn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpConn.disconnect();

                Log.i(TAG, ">>>>>Response Result: "+result);

                return result;
            }
            catch (UnsupportedEncodingException e)  {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }
            return "Error";
        }

        @Override
        /**
         * Take in API result array
         */
        protected void onPostExecute(final String result) {
            contentDownload = null;

            if (result.startsWith("Error") || result.equals("")){
                Log.i(TAG, "Server error:"+result);
                onError();
            }
            else if (result.equals("[]")){
                //no new items
                onError();
            }
            else{
                ArrayList<NewsItem> toDisplay = getNews(result);
                progressBar.setVisibility(View.GONE);
                textProgress.setVisibility(View.GONE);
                //write file to internal memory
                writeToFile(result, getApplicationContext());
                //display array
                displayNews(toDisplay);
            }
        }

        @Override
        protected void onCancelled() {
            contentDownload = null;
        }
    }

    /**
     * Method to write server response to internal memory to reduce data usage
     */
    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("newsList.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Method to read news items from internal memory
     */
    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("newsList.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Cannot read file: " + e.toString());
        }
        return ret;
    }

    /**
     * Method to convert a JSON string of news items into an ArrayList of NewsItem objects
     */
    private ArrayList<NewsItem> getNews(String result) {
        ArrayList<NewsItem> toDisplay = new ArrayList<>();
        try {
            JSONArray items = new JSONArray(result);
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                NewsItem newsItem = new NewsItem(item.getString("filename"), item.getString("uploadDate").substring(0, 10), item.getString("type"), item.getString("id"), item.getString("path"),
                        item.getString("topic"), item.getString("survey_id"));
                toDisplay.add(newsItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toDisplay;
    }

    //Display a error message to the user
    public void onError(){
        progressBar.setVisibility(View.GONE);
        textProgress.setVisibility(View.GONE);
        info.setVisibility(View.VISIBLE);
        info.setTextSize(25);
        info.setText("Server error, please try again later");
    }

    //Method for setting up toolbar options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //Method to handle toolbar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.home:
                //Refresh from the server
                Toast.makeText(this, "Refreshing Content", Toast.LENGTH_SHORT).show();
                info.setVisibility(View.GONE);
                onlineDownload();
                return true;
            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                return true;
            case R.id.logout:
                intent = new Intent(MainActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //clear shared preferences
                mEditor = mPreferences.edit();
                mEditor.clear();
                mEditor.commit();
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
