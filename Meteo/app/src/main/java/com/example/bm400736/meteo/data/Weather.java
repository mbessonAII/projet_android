package com.example.bm400736.meteo.data;

/**
 * Created by bm400736 on 17/03/2017.
 */

public class Weather {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private int id;
    private String main;
    private String description;
    private String icon;


    public Weather(){

    }
}
