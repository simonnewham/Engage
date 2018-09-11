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
import com.engage.simonnewham.engageapp.fragments.EmailFragment;
import com.engage.simonnewham.engageapp.fragments.SignInFragment;
import com.engage.simonnewham.engageapp.fragments.SignUpFragment;

/**
 * Activity that starts when user first opens the app
 * Container for sign in, forgot password, user consent and user sign up fragments
 * @author simonnewham
 */

public class SignUpActivity extends AppCompatActivity {

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    private final String TAG = "StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);
    }

    /**
     * method to setup and manage fragment screens
     * SignInFagment loaded first by default
     */
    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignInFragment(), "User Sign In");
        adapter.addFragment(new EmailFragment(), "Forgot Password");
        adapter.addFragment(new ConsentFragment(), "User Consent");
        adapter.addFragment(new SignUpFragment(), "User Sign Up");
        viewPager.setAdapter(adapter);
    }

    //provide access to allow fragments to change the fragment that is being displayed
    public void setViewPager (int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }
}
