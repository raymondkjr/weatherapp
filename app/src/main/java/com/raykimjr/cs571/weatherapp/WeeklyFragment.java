package com.raykimjr.cs571.weatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;


import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.core.*;
import com.highsoft.highcharts.common.hichartsclasses.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeeklyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeeklyFragment extends Fragment {

    private WeatherData fragmentData;

    public WeeklyFragment() {
        // Required empty public constructor
    }

    public static WeeklyFragment newInstance(WeatherData data) {
        WeeklyFragment fragment = new WeeklyFragment();
        fragment.setData(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setData(WeatherData data) {
        fragmentData = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);

        HIChartView chartView = view.findViewById(R.id.chart_view_arearange);

        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();

        chart.setType("arearange");
        chart.setZoomType("x");
        options.setChart(chart);
        HIGradient grad = new HIGradient(0,0,0,1);

        HITitle title = new HITitle();
        title.setText("Temperature Variation By Day");
        options.setTitle(title);

        HIXAxis xaxis = new HIXAxis();
        //xaxis.setType("datetime");
        options.setXAxis(new ArrayList<HIXAxis>(){{add(xaxis);}});

        HIYAxis yaxis = new HIYAxis();
        yaxis.setTitle(new HITitle());
        options.setYAxis(new ArrayList<HIYAxis>(){{add(yaxis);}});

        HITooltip tooltip = new HITooltip();
        tooltip.setShadow(true);
        tooltip.setValueSuffix("\u00B0F");
        options.setTooltip(tooltip);

        HILegend legend = new HILegend();
        legend.setEnabled(false);
        options.setLegend(legend);
        LinkedList<HIStop> stopList = new LinkedList<HIStop>();
        stopList.add(new HIStop(0, HIColor.initWithRGB(232,147,51)));
        stopList.add(new HIStop(1,HIColor.initWithRGB(91,133,242)));

        HIArearange series = new HIArearange();
        series.setName("Temperatures");
        series.setFillColor(HIColor.initWithLinearGradient(grad, stopList));

        /*HIMarker marker = series.getMarker();
        marker.setColor(HIColor.initWithRGB(160,160,160));
        marker.setFillColor(HIColor.initWithRGB(160,160,160));
        series.setMarker(marker);*/

        Object[][] seriesData;

        try {
            JSONArray oneDayTimeline = fragmentData.getWeatherData().getJSONObject(0).getJSONArray("intervals");
            seriesData = new Object[oneDayTimeline.length()][3];
            for (int i = 0; i < oneDayTimeline.length(); i++) {
                JSONObject data = oneDayTimeline.getJSONObject(i);
                float tempMax = (float) data.getJSONObject("values").getDouble("temperatureMax");
                float tempMin = (float) data.getJSONObject("values").getDouble("temperatureMin");
                seriesData[i] = new Object[]{i, tempMin, tempMax};
            }
            series.setData(new ArrayList<>(Arrays.asList(seriesData)));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        options.setSeries(new ArrayList<>(Arrays.asList(series)));

        chartView.setOptions(options);

        return view;
    }
}