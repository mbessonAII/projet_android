package com.example.bm400736.meteo.forecastdata;

import com.example.bm400736.meteo.data.Clouds;
import com.example.bm400736.meteo.data.Weather;
import com.example.bm400736.meteo.data.Wind;

import java.io.Serializable;

/**
 * Created by bm400736 on 25/03/2017.
 */

public class DataList implements Serializable {
    private long dt;

    public DataList() {
    }

    private MainData main;
    private Weather[] weather;

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public MainData getMain() {
        return main;
    }

    public void setMain(MainData main) {
        this.main = main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

    private Clouds clouds;
    private Wind wind;
    private Sys sys;
    private String dt_txt;
}
