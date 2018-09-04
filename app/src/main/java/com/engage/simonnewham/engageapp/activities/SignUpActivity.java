package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.adapters.SectionsStatePagerAdapter;
import com.engage.simonnewham.engageapp.fragments.ConsentFragment;
import com.engage.simonnewham.engageapp.fragments.SignUpFragment;

/**
 * Class for when new user signs up
 *
 */

public class SignUpActivity extends AppCompatActivity {

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    private final String TAG = "UserSignUp Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        //setup pager
        setupViewPager(mViewPager);

    }

    //method to manage fragment screens
    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ConsentFragment(), "User Consent");
        adapter.addFragment(new SignUpFragment(), "User Sign Up");
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
        finish();
        return true;
    }

    //provide access to allow fragments to change the fragment that is being displayed
    public void setViewPager (int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }
}
