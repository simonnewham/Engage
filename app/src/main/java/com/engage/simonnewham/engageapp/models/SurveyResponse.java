package com.engage.simonnewham.engageapp.models;

import java.util.ArrayList;

/**
 * Created by simonnewham on 2018/08/01.
 */

public class SurveyResponse {

    private String email;
    private String group;
    private String surveyID;
    private ArrayList<String> responses;

    public SurveyResponse(String email, String group, String surveyID, ArrayList<String> responses) {
        this.email = email;
        this.group = group;
        this.surveyID = surveyID;
        this.responses = responses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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
