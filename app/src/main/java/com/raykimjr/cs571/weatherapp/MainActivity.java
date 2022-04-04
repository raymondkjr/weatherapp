package com.raykimjr.cs571.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String FAVORITE_KEY = "com.raykimjr.cs571.weatherapp.FAVORITES_SET";
    public static final String CITY_KEY = "com.raykimjr.cs571.weatherapp.CITY_SET";
    public static HashSet<String> favoritesSet;
    public static HashSet<String> citiesSet;
    private static final String TAG = "MAINACTIVITY";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 0;
    private static final int SEARCH_REQUEST_CODE = 1;
    private String lat = "NO DATA";
    private String lng = "NO DATA";
    RequestQueue queue;
    FragmentManager fm;
    private ViewPager2 viewPager;
    private List<WeatherData> weatherData;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    private int COUNT;
    private boolean loaded;
    private boolean fromSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*set.add("https://cs571hwk8.appspot.com/21.6305348,-158.0418191");
        set.add("https://cs571hwk8.appspot.com/33.8586285,-118.3415996");
        set.add("https://cs571hwk8.appspot.com/39.627207,-106.5163408");
        COUNT = 3;
        */
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        editor = sharedPref.edit();
        favoritesSet = (HashSet<String>) sharedPref.getStringSet(FAVORITE_KEY,new HashSet<>());
        citiesSet = (HashSet<String>) sharedPref.getStringSet(CITY_KEY, new HashSet<>());
        fromSearch = false;
        /*editor = sharedPref.edit();
        editor.putStringSet(FAVORITE_KEY, set);
        editor.apply();*/
        setTheme(R.style.Theme_WeatherApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);
        queue = Volley.newRequestQueue(this);
        fm = getSupportFragmentManager();
        String apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);
        weatherData = new ArrayList<>();
        this.getLocation();
        //load some data from preferences

    }

    public void loadViewPager() {
        viewPager = (ViewPager2) findViewById(R.id.fragment_container);
        viewPager.setAdapter(new FragmentStateAdapter(this) {

            @Override
            public int getItemCount() {
                return weatherData.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                WeatherData w = weatherData.get(position);
                try {
                    return CurrentWeather.newInstance(w.getWeatherData(), w.getCityData(), w.getCurrWeatherData(), w.getUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

        });
    }

    public void loadFavorites() {
        favoritesSet = (HashSet<String>) sharedPref.getStringSet(FAVORITE_KEY, new HashSet<>());
        citiesSet = (HashSet<String>) sharedPref.getStringSet(CITY_KEY, new HashSet<>());
        COUNT = favoritesSet.size();
        while (weatherData.size() > 1)
        {
            weatherData.remove(weatherData.size()-1);
        }


        if (COUNT > 0 ) {
            String[] favList = favoritesSet.toArray(new String[favoritesSet.size()]);
            String[] cityList = citiesSet.toArray(new String[citiesSet.size()]);
            int numFav = COUNT;
            for (int i = 0; i < numFav; i++) {
                String url = favList[i];
                String city = cityList[i];
                Log.i(TAG, url);
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data = null;
                        try {
                            data = response.getJSONObject("data");
                            JSONArray dataArray = data.getJSONArray("timelines");
                            WeatherData w = new WeatherData(dataArray, city, false, url);
                            weatherData.add(w);
                            COUNT--;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (COUNT == 0) {
                            Log.i(TAG, "BEOFRESETCONTENTVIEW");
                            //if (!loaded) {
                                setContentView(R.layout.activity_main);
                                loaded = true;
                            //}
                            Log.i(TAG, "AFTERSETCONTENTVIEW");

                            loadViewPager();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "NO RESPONSE FROM SERVER");
                    }
                });
                queue.add(req);
            }
        }
        else {
            setContentView(R.layout.activity_main);
            loaded = true;
            loadViewPager();
        }

    }

    public void getLocation() {
        String url = "https://ipinfo.io?token=43c4ef528bd7e4";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    String location = (String) response.get("loc");
                    String city = (String) response.get("city");
                    city += ",";
                    city += (String) response.get("region");
                    String[] locationArr = location.split(",");
                    lat = locationArr[0];
                    lng = locationArr[1];
                    Log.i(TAG, "LOCATIONRESPONSE");
                    String backendUrl = "https://cs571hwk8.appspot.com/" + lat + "," + lng;
                    String finalCity = city;
                    JsonObjectRequest weatherReq = new JsonObjectRequest(Request.Method.GET, backendUrl, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray dataArray = data.getJSONArray("timelines");
                                WeatherData w = new WeatherData(dataArray, finalCity, true, backendUrl);
                                weatherData.add(w);
                                loadFavorites();
                                /*CurrentWeather fragment = CurrentWeather.newInstance(dataArray, finalCity,true);
                                fm.beginTransaction()
                                        .add(R.id.fragment_container, fragment)
                                        .commit();*/

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "NO RESPONSE FROM SERVER");
                        }
                    });

                    queue.add(weatherReq);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "NO RESPONSE FROM SERVER");
            }
        });

        queue.add(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        /*MenuItem searchItem = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                onSearchCalled();
                return true;

            default:
                return false;

        }
    }

    public void onSearchCalled() {
        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("US").build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Toast.makeText(getApplicationContext(), place.getLatLng().toString(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, place.getAddressComponents().asList().toString());
                String city = getCityComponents(place.getAddressComponents().asList());
                double lat = place.getLatLng().latitude;
                double lng = place.getLatLng().longitude;
                String url = "https://cs571hwk8.appspot.com/" + String.valueOf(lat) + "," + String.valueOf(lng);
                final JSONArray[] dataArray = {null};
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject data = null;
                        try {
                            data = response.getJSONObject("data");
                            dataArray[0] = data.getJSONArray("timelines");
                            Intent intent = new Intent(getApplicationContext(), Search.class);
                            Bundle extra = new Bundle();
                            extra.putString("city", city);
                            extra.putString("data", dataArray[0].toString());
                            extra.putString("url", url);
                            intent.putExtras(extra);
                            fromSearch = true;
                            startActivityForResult(intent, SEARCH_REQUEST_CODE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "NO RESPONSE FROM SERVER");
                    }
                });
                queue.add(req);
                setContentView(R.layout.activity_load_data);
            }
            else {
                //error
            }
        } else if (requestCode == SEARCH_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
            {
                Log.i(TAG, "SEARCH RETURNED TO MAIN");
                //setContentView(R.layout.activity_main);
                loadFavorites();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loaded) {
            Log.i(TAG, "ONRESUMECALLED");
            //setContentView(R.layout.activity_main);
            //loadFavorites();
        } else {
            Log.i(TAG, "FIRST TIME CALLING ONRESUME");
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"ONSTOP CALLED");
        editor.remove(MainActivity.FAVORITE_KEY);
        editor.remove(MainActivity.CITY_KEY);
        editor.commit();
        editor.putStringSet(FAVORITE_KEY, favoritesSet);
        editor.putStringSet(CITY_KEY, citiesSet);
        editor.commit();
    }

    public String getCityComponents(List<AddressComponent> place) {
        String state = "";
        String city = "";
        for (AddressComponent component : place) {
            if (component.getTypes().contains("administrative_area_level_1")){
                Log.i(TAG, "CITY: " + component.getName());
                state = component.getName();
            }
            else if (component.getTypes().contains("locality")) {
                city = component.getName();
            }
        }

        if (state == "" || city == ""){
            return "CITY NOT FOUND";
        }else {
            return city + ", " + state;
        }
    }

}