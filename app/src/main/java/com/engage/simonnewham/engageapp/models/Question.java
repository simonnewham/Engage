package com.engage.simonnewham.engageapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonnewham on 2018/07/31.
 * Data model for a survey question
 */

public class Question {

    private String question;
    private String type;
    private ArrayList<String> options;

    //constructor for a non-mcq question
    public Question(String question, String type) {
        this.question = question;
        this.type = type;
    }

    //options will be null for non-mcq type question
    public Question(String question, String type, ArrayList<String> options) {
        this.question = question;
        this.type = type;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String toString(){
        return "Question: "+question+" Type: "+type;
    }
}
