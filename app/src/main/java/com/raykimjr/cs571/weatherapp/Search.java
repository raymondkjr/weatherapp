package com.raykimjr.cs571.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.dynamic.SupportFragmentWrapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;

public class Search extends AppCompatActivity {
    private WeatherData fragmentData;
    private FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
        }catch (JSONException e) {
            e.printStackTrace();
        }

        setTitle(city);
        fm = getSupportFragmentManager();
        try {
            CurrentWeather cw = CurrentWeather.newInstance(fragmentData.getWeatherData(), city, false, url);
            fm.beginTransaction().add(R.id.search_fragment_container, cw).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}