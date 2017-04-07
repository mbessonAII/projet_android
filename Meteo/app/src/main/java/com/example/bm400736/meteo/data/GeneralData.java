package com.example.bm400736.meteo.data;

/**
 * Created by bm400736 on 17/03/2017.
 */

public class GeneralData {
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

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    private int id;
    private String name;
    private int cod;
    private int visibility;
    private String base;

    public GeneralData(){

    }
}
