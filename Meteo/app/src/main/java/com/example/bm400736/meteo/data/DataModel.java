package com.example.bm400736.meteo.data;

/**
 * Created by bm400736 on 31/03/2017.
 */
public class DataModel {

    String date;
    String description;
    String temperature;

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    String temperatureUnit;
    String feature;

    public DataModel(String date, String description, String temperature, String temperatureUnit,String feature ) {
        this.date=date;
        this.description=description;
        this.temperature=temperature;
        this.temperatureUnit=temperatureUnit;
        this.temperatureUnit=temperatureUnit;
        this.feature=feature;

    }

    public String getFeature() {
        return feature;
    }

}