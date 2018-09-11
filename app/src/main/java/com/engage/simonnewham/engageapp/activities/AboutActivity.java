package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;

import org.w3c.dom.Text;

/**
 * Class responsible for handling logic of activity_about
 * Displays current user and user group
 * @author simonnewham
 */
public class AboutActivity extends AppCompatActivity {

    //shared preference code
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private String email;
    private String user_group;
    TextView mUser;
    TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //get extra info
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");
        }

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);

        mEmail = findViewById(R.id.user);
        mEmail.setText("User: "+email);
        mUser = findViewById(R.id.user_group);
        mUser.setText("Group Number: "+user_group);
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
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                finish();
                return true;
            case R.id.about:
                return true;
            case R.id.logout:
                intent = new Intent(this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //clear shared preferences on logout
                mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
