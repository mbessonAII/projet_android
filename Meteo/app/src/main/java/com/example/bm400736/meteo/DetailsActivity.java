package com.example.bm400736.meteo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bm400736.meteo.data.DataModel;
import com.example.bm400736.meteo.data.Meteo;
import com.example.bm400736.meteo.forecastdata.City;
import com.example.bm400736.meteo.forecastdata.DataList;
import com.example.bm400736.meteo.forecastdata.Forecast5Days3Hours;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class DetailsActivity extends AppCompatActivity {

    private Meteo meteo;
    private Forecast5Days3Hours forecast;
    private City[] city;

    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_content);


        //on fait les réglages du module qui va gérer la listView
        listView=(ListView)findViewById(R.id.list);

        dataModels= new ArrayList<>();
        adapter = new CustomAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel= dataModels.get(position);

                Snackbar.make(view, dataModel.getDate()+"\n"+dataModel.getDescription() + " " + dataModel.getTemperature() + dataModel.getTemperatureUnit(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });



        //récupération des infos depuis l'activité précédente
        Intent intent = getIntent();

        if(savedInstanceState != null){
            //on restaure les prévisions météo
            Gson gson = new GsonBuilder().create();
            forecast = (Forecast5Days3Hours) savedInstanceState.getSerializable("forecast");
            //forecast = gson.fromJson(s, Forecast5Days3Hours.class);

            //on restaure le progrès de la seekBar
            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
            seekBar.setProgress(savedInstanceState.getInt("seekBarVal"));

            //on affiche tout à la bonne heure
            affichage(seekBar.getProgress()%24);
            //seekBar.notify();
        }
        else if (intent != null) {
            //récupération des données

            if(intent.hasExtra("lat") && intent.hasExtra("lon"))
            {
                double lat = intent.getDoubleExtra("lat", 0.0);
                double lon = intent.getDoubleExtra("lon", 0.0);
                Log.d("DetailsActivity" , "Information : lat : " + lat +", lon :" + lon );
                //TODO getMeteo Async

                OpenWeatherMapForecastClass forecastTask = new OpenWeatherMapForecastClass(new LatLng(lat, lon));
                forecastTask.execute();
            }
        }//on gère la rotation écran






    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //les prévisions météo
        outState.putSerializable("forecast", forecast);

        //la valeur de la seekBar
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        outState.putInt("seekBarVal", seekBar.getProgress());
    }


    private int findIndexOfNearestHour(String toFind, DataList[] searchIn){
        int distance = Math.abs(Integer.parseInt(getHour(searchIn[0].getDt_txt())) - Integer.parseInt(toFind));
        int idx = 0;
        for(int c = 0; c < searchIn.length; c++){
            int cdistance = Math.abs(Integer.parseInt(getHour(searchIn[c].getDt_txt())) - Integer.parseInt(toFind));
            if(cdistance < distance){
                idx = c;
                distance = cdistance;
            }
        }
        return idx;
    }

    private int findIndexOfNearestHourInDay(String toFind, DataList[] searchIn, Date date){
        int distance = 0;
        int idx = 0;
        boolean premierPassage = true;
        for(int c = 0; c < searchIn.length; c++){
            int i = c;
            premierPassage = true;
            while(i < searchIn.length && getDate(searchIn[i].getDt_txt()).equals(date)){
                if(premierPassage){
                    distance = Math.abs(Integer.parseInt(getHour(searchIn[i].getDt_txt())) - Integer.parseInt(toFind));
                    idx = i;
                    premierPassage = false;
                }
                int cdistance = Math.abs(Integer.parseInt(getHour(searchIn[i].getDt_txt())) - Integer.parseInt(toFind));
                if(cdistance < distance){
                    idx = i;
                    distance = cdistance;
                }
                i++;
                c = i;
            }
        }
        return idx;
    }

    private void affichage(){

        //affichage de la ville
        //TextView tvCityName = (TextView) findViewById(R.id.cityName);
        //tvCityName.setText(forecast.getCity().getName());
        //stockage de la date précédente
        Date previousDate = null;

        //si la date testée (format yyyy-mm--dd) est différente de la précédente on cherche l'heure la plus proche de celle demandée et on affiche la météo à cette heure là
        for(DataList li : forecast.getList())
        {
            if(previousDate == null){
                previousDate = getDate(li.getDt_txt());
                int index = findIndexOfNearestHourInDay("19", forecast.getList(), getDate(li.getDt_txt()));
                dataModels.add(new DataModel(getDayName(forecast.getList()[index].getDt_txt()) + " " + getDayNbr(forecast.getList()[index].getDt_txt()) + " " + getMonthName(forecast.getList()[index].getDt_txt()) + " " + getHour(forecast.getList()[index].getDt_txt() ) + "h", forecast.getList()[index].getWeather()[0].getDescription(), Double.toString(forecast.getList()[index].getMain().getTemp()), " °C",""));
                //dataModels.add(new DataModel(getDayName(li.getDt_txt()) + " " + getDayNbr(li.getDt_txt()) + " " + getMonthName(li.getDt_txt()) + " " + getHour(li.getDt_txt()), li.getWeather()[0].getDescription(), Double.toString(li.getMain().getTemp()), " °C",""));
            }
            else if(!getDate(li.getDt_txt()).equals(previousDate))
            {
                int index = findIndexOfNearestHourInDay("19", forecast.getList(), getDate(li.getDt_txt()));
                dataModels.add(new DataModel(getDayName(forecast.getList()[index].getDt_txt()) + " " + getDayNbr(forecast.getList()[index].getDt_txt()) + " " + getMonthName(forecast.getList()[index].getDt_txt()) + " " + getHour(forecast.getList()[index].getDt_txt()) + "h", forecast.getList()[index].getWeather()[0].getDescription(), Double.toString(forecast.getList()[index].getMain().getTemp()), " °C",""));
                //dataModels.add(new DataModel(getDayName(li.getDt_txt()) + " " + getDayNbr(li.getDt_txt()) + " " + getMonthName(li.getDt_txt()) + " " + getHour(li.getDt_txt()), li.getWeather()[0].getDescription(), Double.toString(li.getMain().getTemp()), " °C",""));
                previousDate = getDate(li.getDt_txt());
            }
        }

        //on avertit l'adapter que les données ont changé
        adapter.notifyDataSetChanged();
    }
    private void affichage(int hour){
        //affichage de la ville
        //TextView tvCityName = (TextView) findViewById(R.id.cityName);
        //tvCityName.setText(forecast.getCity().getName());

        //on efface de l'écran les anciennes données
        adapter.clear();

        String hourStr = Integer.toString(hour);
        Date previousDate = null;

        //si la date testée (format yyyy-mm--dd) est différente de la précédente on cherche l'heure la plus proche de celle demandée et on affiche la météo à cette heure là
        for(DataList li : forecast.getList())
        {
            if(previousDate == null){
                previousDate = getDate(li.getDt_txt());
                int index = findIndexOfNearestHourInDay(hourStr, forecast.getList(), getDate(li.getDt_txt()));
                dataModels.add(new DataModel(getDayName(forecast.getList()[index].getDt_txt()) + " " + getDayNbr(forecast.getList()[index].getDt_txt()) + " " + getMonthName(forecast.getList()[index].getDt_txt()) + " " + getHour(forecast.getList()[index].getDt_txt() ) + "h", forecast.getList()[index].getWeather()[0].getDescription(), Double.toString(forecast.getList()[index].getMain().getTemp()), " °C", "i" + forecast.getList()[index].getWeather()[0].getIcon()));
                //dataModels.add(new DataModel(getDayName(li.getDt_txt()) + " " + getDayNbr(li.getDt_txt()) + " " + getMonthName(li.getDt_txt()) + " " + getHour(li.getDt_txt()), li.getWeather()[0].getDescription(), Double.toString(li.getMain().getTemp()), " °C",""));
            }
            else if(!getDate(li.getDt_txt()).equals(previousDate))
            {
                int index = findIndexOfNearestHourInDay(hourStr, forecast.getList(), getDate(li.getDt_txt()));
                dataModels.add(new DataModel(getDayName(forecast.getList()[index].getDt_txt()) + " " + getDayNbr(forecast.getList()[index].getDt_txt()) + " " + getMonthName(forecast.getList()[index].getDt_txt()) + " " + getHour(forecast.getList()[index].getDt_txt()) + "h", forecast.getList()[index].getWeather()[0].getDescription(), Double.toString(forecast.getList()[index].getMain().getTemp()), " °C","i" + forecast.getList()[index].getWeather()[0].getIcon()));
                //dataModels.add(new DataModel(getDayName(li.getDt_txt()) + " " + getDayNbr(li.getDt_txt()) + " " + getMonthName(li.getDt_txt()) + " " + getHour(li.getDt_txt()), li.getWeather()[0].getDescription(), Double.toString(li.getMain().getTemp()), " °C",""));
                previousDate = getDate(li.getDt_txt());
            }
        }

        adapter.notifyDataSetChanged();
    }


    private class OpenWeatherMapForecastClass extends AsyncTask<Void, Void, String> {

        String queryForecast = "http://api.openweathermap.org/data/2.5/forecast?";
        String dummyAppid = "ede8ed629c321c8345a55d2879bc0104";
        String queryDummyKey = "&appid=" + dummyAppid;

        LatLng coordinates;

        OpenWeatherMapForecastClass(LatLng coords) {
            this.coordinates = coords;
        }
        @Override
        protected String doInBackground(Void... params) {
            String queryReturn = null;
            String query = null;
            try {
                //query = queryWeather + URLEncoder.encode(cityName, "UTF-8") + queryDummyKey;
                query = queryForecast
                        + "lat="
                        + URLEncoder.encode(Double.toString((coordinates.latitude)), "UTF-8")
                        + "&lon="
                        + URLEncoder.encode(Double.toString((coordinates.longitude)), "UTF-8")
                        + queryDummyKey
                        + "&lang=fr"
                        + "&units=metric";
                queryReturn = sendQuery(query);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return queryReturn;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Gson gson = new GsonBuilder().create();
                forecast = gson.fromJson(s, Forecast5Days3Hours.class);
                affichage();
            }
            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                   @Override
                                                   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                                   }

                                                   @Override
                                                   public void onStartTrackingTouch(SeekBar seekBar) {

                                                   }

                                                   @Override
                                                   public void onStopTrackingTouch(SeekBar seekBar) {
                                                       affichage(seekBar.getProgress()%24);//%24 au cas où mais le max est 23
                                                   }
                                               }
            );
        }

        private String sendQuery(String query) throws IOException {
            String result = "";

            URL searchURL = new URL(query);

            HttpURLConnection httpURLConnection = (HttpURLConnection) searchURL.openConnection();

            //can't be in the main thread
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader,
                        8192);

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
            }

            return result;
        }
    }

    //utilitaires pour récupérer les différents éléments de la date
    public static Date getDateAndHour(String dateString){
        Date d = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
    public static Date getDate(String dateString){
        Date d = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            d = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
    public static String getDayNbr(String date){
        String dayNbr = "";
        SimpleDateFormat outFormat = new SimpleDateFormat("d");
        dayNbr = outFormat.format(getDateAndHour(date));
        return dayNbr;
    }
    public static String getHour(String date){
        String hour = "";
        SimpleDateFormat outFormat = new SimpleDateFormat("H");
        hour = outFormat.format(getDateAndHour(date));
        return hour;
    }
    public static String getDayName(String date){
        String dayName ="";
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        dayName = outFormat.format(getDateAndHour(date));
        return dayName;
    }
    public static String getMonthName(String date){
        String monthName ="";
        SimpleDateFormat outFormat = new SimpleDateFormat("MMMM");
        monthName = outFormat.format(getDateAndHour(date));
        return monthName;
    }
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

}

