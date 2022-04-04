package com.raykimjr.cs571.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class TodayFragment extends Fragment {
    private float wind;
    private float pressure;
    private float precip;
    private int temp;
    private int status;
    private int humid;
    private float vis;
    private int cloud;
    private float uv;
    private TextView windText;
    private TextView pressureText;
    private TextView rainText;
    private TextView tempText;
    private TextView statusText;
    private ImageView statusImage;
    private TextView humidText;
    private TextView visText;
    private TextView cloudText;
    private TextView uvText;
    private static WeatherCodeMap codeMap = new WeatherCodeMap();

    public static TodayFragment newInstance(WeatherData data) throws JSONException {
        TodayFragment fragment = new TodayFragment();
        fragment.setData(data);
        return fragment;
    }

    public void setData(WeatherData data) throws JSONException {
        JSONObject currObj = data.getWeatherData().getJSONObject(2).getJSONArray("intervals").getJSONObject(0).getJSONObject("values");
        temp = currObj.getInt("temperature");
        wind = (float) currObj.getDouble("windSpeed");
        pressure = (float) currObj.getDouble("pressureSeaLevel");
        precip = (float) currObj.getDouble("precipitationProbability");
        status = currObj.getInt("weatherCode");
        humid = currObj.getInt("humidity");
        vis = (float) currObj.getDouble("visibility");
        cloud = currObj.getInt("cloudCover");
        uv = (float) currObj.getDouble("uvIndex");
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        windText = view.findViewById(R.id.today_wind_text);
        pressureText = view.findViewById(R.id.today_pressure_text);
        rainText = view.findViewById(R.id.today_rain_text);
        tempText = view.findViewById(R.id.today_temp_text);
        statusText = view.findViewById(R.id.today_status_text);
        statusImage = view.findViewById(R.id.today_status_image);
        humidText = view.findViewById(R.id.today_humid_text);
        visText = view.findViewById(R.id.today_vis_text);
        cloudText = view.findViewById(R.id.today_cloud_text);
        uvText = view.findViewById(R.id.today_ozone_text);

        windText.setText(String.format("%.2f", wind) + " mph");
        pressureText.setText(String.format("%.2f", pressure) + " inHg");
        rainText.setText(String.format("%.2f", precip) + " %");
        tempText.setText(temp + "\u00B0F");
        humidText.setText(humid + " %");
        visText.setText(String.format("%.2f", vis) + " mi");
        cloudText.setText(cloud + " %");
        uvText.setText(String.format("%.2f", uv));

        statusText.setText(codeMap.getCode(status));
        statusImage.setImageResource(codeMap.getImage(status));


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }




}
