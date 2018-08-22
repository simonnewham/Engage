package com.engage.simonnewham.engageapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonnewham on 2018/07/31.
 * Data Model for a survey loaded via a JSON response
 */

public class Survey {

    private String ID;
    private String name;
    private String description;
    private ArrayList<Question> questions;

    public Survey(String ID, String name, String description, ArrayList<Question> questions) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public String geID() {
        return ID;
    }

    public void setSurveyID(String surveyID) {
        this.ID = surveyID;
    }


}
