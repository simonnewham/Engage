package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author simonnewham
 * Class to show individual news items downloaded from server
 */

public class ContentActivity extends AppCompatActivity {

    private final String TAG = "ContentActivity";

    TextView title;
    TextView date;
    TextView content;

    String email;
    String user_group;

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
        content = (TextView) findViewById(R.id.content);

        //get Extra
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");

        }

        //connect to DB and get Article details

        //load article
        setItem();

    }

    /**
     * Method to set up the news article within the android view
     */
    public void setItem(){

        String jTitle="";
        String jDate ="";
        String jContent ="" ;
        String jSource ="";
        String jType ="";

        try{
            JSONObject reader = new JSONObject(loadJSONFromAsset());

            //JSONObject jTitle = reader.getJSONObject("Title");
            jTitle = reader.getString("Title");
            jDate= reader.getString("Date");
            jContent= reader.getString("Main_content");
            jSource= reader.getString("Source");
            jType= reader.getString("Content_type");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        title.setText(jTitle);
        date.setText("Uploaded on: "+jDate);
        content.setText(jContent);

        Log.i(TAG, "*********Content details********** Title:"+jTitle+" Type: "+jType+" Source"+jSource);

    }

    /**
     * method to read from a JSON file located in the assets folder
     * @return String
     */
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("item.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public boolean onSupportNavigateUp() {
        Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ContentActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    public void loadSurvey(View view){
        Toast.makeText(this, "Load Survey clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ContentActivity.this, SurveyActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("group", user_group);
        intent.putExtra("surveyID", "test3"); //will change for http connection
        startActivity(intent);
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
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(ContentActivity.this, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                finish();
                return true;
            case R.id.about:
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(ContentActivity.this, AboutActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                finish();
                return true;
            case R.id.logout:
                Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
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
