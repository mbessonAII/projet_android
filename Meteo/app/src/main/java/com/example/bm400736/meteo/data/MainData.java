package com.example.bm400736.meteo.data;

/**
 * Created by bm400736 on 17/03/2017.
 */

public class MainData {
    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getPressure1() {
        return pressure1;
    }

    public void setPressure1(int pressure1) {
        this.pressure1 = pressure1;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }

    private double temp;
    private int pressure1;
    private int humidity;
    private double temp_min;
    private double temp_max;

    public MainData(){

    }
}
