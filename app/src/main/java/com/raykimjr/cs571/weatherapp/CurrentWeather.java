package com.raykimjr.cs571.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentWeather#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentWeather extends Fragment {

    private static final String TAG = "CURRENTWEATHER";
    private static WeatherCodeMap codeMap;
    public CurrentWeather() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentWeather.
     */
    private float lat;
    private float lng;
    private static RequestQueue queue;
    private RecyclerView recyclerView;
    private List<DailyWeather> dailyList;
    private DailyWeatherAdapter adapter;
    private boolean isCurrentWeather;
    private FloatingActionButton fab;
    private JSONObject currentValues;
    private String locationString;
    private ImageView currentlyImage;
    private TextView currentlyTemp;
    private TextView currentlyCode;
    private TextView city;
    private TextView currHumid;
    private TextView currWind;
    private TextView currVis;
    private TextView currPress;
    private WeatherData fragmentData;
    private boolean isFavorited;
    private String url;

    // TODO: Rename and change types and number of parameters
    public static CurrentWeather newInstance(JSONArray data, String city, boolean currWeather, String url) throws JSONException, ParseException {
        CurrentWeather fragment = new CurrentWeather();
        fragment.setData(data, city, currWeather, url);
        fragment.createDailyList();
        fragment.parseDataArray(data);
        fragment.setCurrentWeather(currWeather);
        fragment.setCity(city);
        codeMap = new WeatherCodeMap();
        return fragment;
    }


    public void setData(JSONArray data, String city, boolean currWeather, String url) {
        fragmentData = new WeatherData(data, city, currWeather, url);
        this.url = url;
    }

    public void createDailyList() {
        this.dailyList = new ArrayList<>();
    }

    public void setCity(String c) {
        this.locationString = c;
    }

    public String getCity() {
        return this.locationString;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "ONATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ONCREATE");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "ONCREATEVIEW");

        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        CardView topCard = view.findViewById(R.id.forecastTopCard);
        topCard.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(getActivity(), Detail.class);
                                           Bundle extra = new Bundle();
                                           extra.putString("city", fragmentData.getCityData());
                                           extra.putString("data", fragmentData.getWeatherData().toString());
                                           intent.putExtras(extra);
                                           startActivity(intent);
                                       }
                                   }
        );
        fab = view.findViewById(R.id.addfavorite_fab);
        recyclerView = view.findViewById(R.id.forecastRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        if (isCurrentWeather()) {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFabState();
            }
        });
        currentlyImage = view.findViewById(R.id.now_icon);
        currentlyTemp = view.findViewById(R.id.now_temp_text);
        city = view.findViewById(R.id.city_text);
        currentlyCode = view.findViewById(R.id.now_code_text);

        currHumid = view.findViewById(R.id.curr_humid_text);
        currWind = view.findViewById(R.id.curr_wind_text);
        currVis = view.findViewById(R.id.curr_vis_text);
        currPress = view.findViewById(R.id.curr_pressure_text);
        isFavorited = inFavorites(this.url);
        changeFabIcon();
        try {
            currentlyImage.setImageResource(codeMap.getImage(currentValues.getInt("weatherCode")));
            currentlyTemp.setText(Math.round(currentValues.getDouble("temperature")) + "\u00B0F");
            city.setText(getCity());
            currentlyCode.setText(codeMap.getCode(currentValues.getInt("weatherCode")));
            currHumid.setText(currentValues.getInt("humidity")+"%");
            currWind.setText(currentValues.getDouble("windSpeed") + "mph");
            currVis.setText(currentValues.getDouble("visibility")+"mi");
            currPress.setText(currentValues.getDouble("pressureSeaLevel")+"mmHg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void addToFavorite() {
        MainActivity.favoritesSet.add(this.url);
        MainActivity.citiesSet.add(this.getCity());
        MainActivity.editor.remove(MainActivity.FAVORITE_KEY);
        MainActivity.editor.remove(MainActivity.CITY_KEY);
        MainActivity.editor.commit();
        MainActivity.editor.putStringSet(MainActivity.FAVORITE_KEY, MainActivity.favoritesSet);
        MainActivity.editor.putStringSet(MainActivity.CITY_KEY, MainActivity.citiesSet);
        MainActivity.editor.commit();
        //((MainActivity)getActivity()).loadFavorites();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFavorites();
        }

    }

    public boolean inFavorites(String url) {
        if (MainActivity.favoritesSet.contains(url)) {
            return true;
        }

        return false;
    }

    public void removeFromFavorite() {
        MainActivity.favoritesSet.remove(this.url);
        MainActivity.citiesSet.remove(this.getCity());
        MainActivity.editor.remove(MainActivity.FAVORITE_KEY);
        MainActivity.editor.remove(MainActivity.CITY_KEY);
        MainActivity.editor.commit();
        MainActivity.editor.putStringSet(MainActivity.FAVORITE_KEY, MainActivity.favoritesSet);
        MainActivity.editor.putStringSet(MainActivity.CITY_KEY, MainActivity.citiesSet);
        MainActivity.editor.commit();
        //((MainActivity)getActivity()).loadFavorites();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFavorites();
        }
    }

    public void changeFabIcon() {
        if (isFavorited) {
            fab.setImageResource(R.drawable.map_marker_minus);
        }
        else {
            fab.setImageResource(R.drawable.map_marker_plus);
        }
    }

    public void changeFabState() {
        if (isFavorited) {
            Toast.makeText(getContext(), getCity() + " removed from favorites", Toast.LENGTH_SHORT).show();
            removeFromFavorite();
        } else {
            Toast.makeText(getContext(), getCity() + " added to favorites", Toast.LENGTH_SHORT).show();
            addToFavorite();
        }

        isFavorited = !isFavorited;
        changeFabIcon();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ONVIEWCREATED");
    }

    private void callServer() {
        Log.i(TAG,"CALLSERVER");
        String url = "https://cs571hwk8.appspot.com/" + String.valueOf(lat) + "," + String.valueOf(lng);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray dataArray = data.getJSONArray("timelines");
                    JSONObject timelineObj = dataArray.getJSONObject(0);
                    JSONArray dailyValues = timelineObj.getJSONArray("intervals");
                    Log.i(TAG, "ONRESPONSE");
                    parseDataArray(dailyValues);
                } catch (JSONException | ParseException e) {
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

    private class DailyWeatherHolder extends RecyclerView.ViewHolder {
        private TextView dateView;
        private TextView maxTempView;
        private TextView minTempView;
        private ImageView statusView;
        private DailyWeather weatherValues;

        public DailyWeatherHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.daily_list_view, parent, false));
            dateView = itemView.findViewById(R.id.list_date);
            maxTempView = itemView.findViewById(R.id.list_max_temp);
            minTempView = itemView.findViewById(R.id.list_min_temp);
            statusView = itemView.findViewById(R.id.list_status_icon);
        }

        public void bind(DailyWeather dw) {
            this.weatherValues = dw;
            dateView.setText(weatherValues.getDateString());
            maxTempView.setText(weatherValues.getMaxTemp() + "");
            minTempView.setText(weatherValues.getMinTemp() + "");
            statusView.setImageResource(codeMap.getImage(weatherValues.getWeatherCode()));

        }


    }

    private class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherHolder> {
        private List<DailyWeather> dailyList;

        public DailyWeatherAdapter(List<DailyWeather> dw) {
            dailyList = dw;
        }
        @NonNull
        @Override
        public DailyWeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new DailyWeatherHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DailyWeatherHolder holder, int position) {
            if (dailyList.size() > 0) {
                DailyWeather dw = dailyList.get(position);
                holder.bind(dw);
            }

        }

        @Override
        public int getItemCount() {
            return dailyList.size();
        }
    }

    private void parseDataArray(JSONArray dataArray) throws JSONException, ParseException {
        Log.i(TAG, "PARSEDATAARRAY");
        JSONObject oneDayTimestep = dataArray.getJSONObject(0);
        JSONObject currentTimestep = dataArray.getJSONObject(2);
        JSONArray currentValArray = currentTimestep.getJSONArray("intervals");
        JSONObject currentObj = currentValArray.getJSONObject(0);
        currentValues = currentObj.getJSONObject("values");
        JSONArray dailyValues = oneDayTimestep.getJSONArray("intervals");
        int len = dailyValues.length() > 7 ? 7 : dailyValues.length();
        for (int i = 0; i < len; i++) {
            JSONObject data = dailyValues.getJSONObject(i);
            DailyWeather newData = new DailyWeather(data);
            dailyList.add(newData);
        }
        adapter = new DailyWeatherAdapter(dailyList);

    }

    public boolean isCurrentWeather() {
        return isCurrentWeather;
    }

    public void setCurrentWeather(boolean cw) {
        this.isCurrentWeather = cw;
    }
}