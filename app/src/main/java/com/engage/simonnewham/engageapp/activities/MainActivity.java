package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.adapters.NewsAdapter;
import com.engage.simonnewham.engageapp.models.NewsItem;
import com.engage.simonnewham.engageapp.models.NewsListItem;
import com.engage.simonnewham.engageapp.models.Question;
import com.engage.simonnewham.engageapp.models.Survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

//import android.support.v7.app.AppCompatActivity;

/**
 * user home page with list of news items
 * Load particular news item when user clicks on item
 */

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private ListView news_list;
    private NewsAdapter newsAdapter;
    private LinearLayout main_panel;

    private String email;
    private String user_group;

    private ContentDownload contentDownload;

    ArrayList<NewsItem> store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get extra info to load personalised content
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");
        }

        //TRACING
        TextView tmail = (TextView) findViewById(R.id.tmail);
        tmail.setText("Email: "+email);
        TextView tgroup = (TextView) findViewById(R.id.tgroup);
        tgroup.setText("User Group: "+user_group);





        contentDownload = new ContentDownload(user_group);
        contentDownload.execute((Void) null);

    }




    public void displayNews(ArrayList<NewsItem> toDisplay){

        store = toDisplay;
        news_list = (ListView) findViewById(R.id.news_list);
        newsAdapter = new NewsAdapter(this, toDisplay);
        news_list.setAdapter(newsAdapter);

        news_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
            {
               //Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                intent.putExtra("News", store.get(position)); //**UPDATE THIS WITH ACTUAL NEWS ID FOR DB
                startActivity(intent);
                finish();
            }
        });


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

                // ***** Send POST message *****
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
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        /**
         * Take in API result array
         */

        protected void onPostExecute(final String result) {
            contentDownload = null;
            //showProgress(false);
            if (result.startsWith("Error") || result.equals("")){
                Log.i(TAG, "Server error:"+result);
                //onError();
            }
            else if (result.equals("[]")){
                //no new items
            }
            else{
                //convert from JSON string to JSONObject
                ArrayList<NewsItem> toDisplay = new ArrayList<>();
                try {
                    JSONArray items = new JSONArray(result);

                    for (int i=0; i<items.length(); i++){

                        JSONObject item = items.getJSONObject(i);
                        NewsItem newsItem = new NewsItem(item.getString("filename"),item.getString("uploadDate").substring(0,10), item.getString("type"), item.getString("id"), item.getString("path"),
                                item.getString("topic"));

                       toDisplay.add(newsItem);
                    }

                    displayNews(toDisplay);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onCancelled() {
            contentDownload = null;
            //showProgress(false);
        }
    }

    public void onError(){
        TextView error = new TextView(this);
        error.setText("Server Error, please try again later");
        error.setTextSize(25);
        main_panel.addView(error);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.home:
                //Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
                //Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, AboutActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                finish();
                return true;
            case R.id.logout:
                //Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //check
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
