package com.engage.simonnewham.engageapp.models;

/**
 * Created by simonnewham on 2018/07/27.
 */

public class NewsItem {

    private String title;
    private String date;
    private String decription;



    public NewsItem (String t, String d){

        title = t;
        date = d;
       // decription = descrip;

    }

    public String getTitle() {

        return title;
    }

    public String getDate() {

        return date;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }
}
