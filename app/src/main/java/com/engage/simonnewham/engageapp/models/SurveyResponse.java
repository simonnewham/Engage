package com.engage.simonnewham.engageapp.models;

import java.util.ArrayList;

/**
 * Created by simonnewham on 2018/08/01.
 */

public class SurveyResponse {

    private String email;
    private String user_group;
    private String surveyID;
    private ArrayList<String> responses;
    private String newsItem;
    private String date;

    public SurveyResponse(String email, String user_group, String surveyID, ArrayList<String> responses, String newsItem, String date) {
        this.email = email;
        this.user_group = user_group;
        this.surveyID = surveyID;
        this.responses = responses;
        this.newsItem = newsItem;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(String newsItem) {
        this.newsItem = newsItem;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroup() {
        return user_group;
    }

    public void setGroup(String group) {
        this.user_group = group;
    }

    public String getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(String surveyID) {
        this.surveyID = surveyID;
    }

    public ArrayList<String> getResponses() {
        return responses;
    }

    public void setResponses(ArrayList<String> responses) {
        this.responses = responses;
    }
}
