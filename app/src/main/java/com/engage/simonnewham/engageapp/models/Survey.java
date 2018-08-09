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
    //private String date;
    //private String newsID;
    //usergroup?
    private ArrayList<Question> questions;
    //private int qNum;


    public Survey(String ID, String name, String description, ArrayList<Question> questions) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.questions = questions;
    }

//    public Survey(String surveyID, String title, String description, String newsID, String date, int qNum, ArrayList<Question> questions) {
//        this.surveyID = surveyID;
//        this.title = title;
//        this.description = description;
//        this.newsID = newsID;
//        this.date = date;
//        this.qNum = qNum;
//        this.questions = questions;
//    }

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

//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }

//    public String getNewsID() {
//        return newsID;
//    }
//
//    public void setNewsID(String newsID) {
//        this.newsID = newsID;
//    }

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

//    public int getqNum() {
//        return qNum;
//    }
//
//    public void setqNum(int qNum) {
//        this.qNum = qNum;
//    }
}
