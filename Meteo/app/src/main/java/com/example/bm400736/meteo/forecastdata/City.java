package com.example.bm400736.meteo.forecastdata;

import com.example.bm400736.meteo.data.Coord;

import java.io.Serializable;

/**
 * Created by bm400736 on 25/03/2017.
 */

public class City implements Serializable{
    public City() {
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private int id;
    private String name;
    private Coord coord;
    private String country;
}
