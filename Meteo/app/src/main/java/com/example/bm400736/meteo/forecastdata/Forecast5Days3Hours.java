package com.example.bm400736.meteo.forecastdata;

import java.io.Serializable;

/**
 * Created by bm400736 on 25/03/2017.
 */

public class Forecast5Days3Hours implements Serializable {
    public Forecast5Days3Hours() {
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    private City city;

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public double getMessage() {
        return message;
    }

    public void setMessage(double message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    private int cod;
    private double message;
    private int cnt;

    public DataList[] getList() {
        return list;
    }

    public void setList(DataList[] list) {
        this.list = list;
    }

    private DataList[] list;
}
