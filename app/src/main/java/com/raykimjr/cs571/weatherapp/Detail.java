package com.raykimjr.cs571.weatherapp;

import static com.raykimjr.cs571.weatherapp.WeeklyFragment.newInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Detail extends AppCompatActivity {
    private TabLayout tabLayout;
    private FragmentContainerView displayFragment;
    private FragmentManager fm;
    private final String TAG = "DETAIL";
    private WeatherData fragmentData;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String city = extras.getString("city");
        String data = extras.getString("data");
        String url = extras.getString("url");
        JSONArray jsonData;
        try {
            jsonData = new JSONArray(data);
            fragmentData = new WeatherData(jsonData, city, false, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setTitle(fragmentData.getCityData());
        tabLayout = findViewById(R.id.tabLayout);

        displayFragment = findViewById(R.id.tab_fragment_container);
        fm = getSupportFragmentManager();
        //fm.beginTransaction().add(R.id.tab_fragment_container, WeatherDataFragment.newInstance(fragmentData)).commit();
        try {
            fm.beginTransaction().add(R.id.tab_fragment_container, TodayFragment.newInstance(fragmentData)).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TodayFragment tfrag = null;
        try {
            tfrag = TodayFragment.newInstance(fragmentData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WeeklyFragment wfrag = WeeklyFragment.newInstance(fragmentData);
        WeatherDataFragment wdfrag = WeatherDataFragment.newInstance(fragmentData);


        TodayFragment finalTfrag = tfrag;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        Log.i(TAG, "CASE0: TODAY");
                        fm.beginTransaction().replace(R.id.tab_fragment_container, finalTfrag).commit();
                        break;
                    case 1:
                        Log.i(TAG, "CASE1: WEEKLY");
                        fm.beginTransaction().replace(R.id.tab_fragment_container, wfrag).commit();
                        break;
                    case 2:
                        Log.i(TAG, "CASE2: WEATHER DATA");
                        fm.beginTransaction().replace(R.id.tab_fragment_container, wdfrag).commit();
                        break;
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).select();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.tweet_button:
                Log.i(TAG, "PRESSED TWEET BUTTON");
                try {
                    float temp = (float) fragmentData.getWeatherData().getJSONObject(2).getJSONArray("intervals").getJSONObject(0).getJSONObject("values").getDouble("temperature");
                    String textToTweet = "Check out " + fragmentData.getCityData() + "'s weather! It is " + temp + "\u00B0F! #CSCI571WeatherSearch";
                    String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s", URLEncoder.encode( textToTweet, "UTF-8"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                    startActivity(intent);
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}