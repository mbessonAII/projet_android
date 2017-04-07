package com.example.bm400736.meteo.forecastdata;

import java.io.Serializable;

/**
 * Created by bm400736 on 25/03/2017.
 */

public class Sys implements Serializable {
    public Sys() {

    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    private String pod;
}
