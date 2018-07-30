package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.adapters.NewsAdapter;
import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.models.NewsItem;

import java.util.ArrayList;

/**
 * user home page
 */

public class MainActivity extends AppCompatActivity {

    private ListView news_list;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setUp news item list
        news_list = (ListView) findViewById(R.id.news_list);
        ArrayList<NewsItem> newsList = new ArrayList<>();
        newsList.add(new NewsItem("Man Utd 1-4 Liverpool: Xherdan Shaqiri scores stunning overhead kick", "27-07-18"));
        newsList.add(new NewsItem("News Item 2", "28-07-18"));
        newsList.add(new NewsItem("Survey Number 1", "31-08-18"));
        newsAdapter = new NewsAdapter(this, newsList);
        news_list.setAdapter(newsAdapter);

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
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
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
    public void loadNews(View view){

        Intent intent = new Intent(MainActivity.this, ContentActivity.class);
        startActivity(intent);
        finish();

    }

}
