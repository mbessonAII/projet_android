package com.example.bm400736.meteo.data;

/**
 * Created by bm400736 on 17/03/2017.
 */

public class Coord {
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    private double lat;
    private double lon;
    public Coord(){

    }

}
