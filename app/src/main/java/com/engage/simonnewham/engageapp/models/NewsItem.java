package com.engage.simonnewham.engageapp.models;

import java.io.Serializable;

/**
 * Data Model for a News Item loaded via API JSON response
 * @author simonnewham
 */

public class NewsItem implements Serializable {

    private String name;
    private String date;
    private String type;
    private String id;
    private String path;
    private String topic;
    private String survey_id;

    public NewsItem(String name, String date, String type, String id, String path, String topic, String survey_id) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.id = id;
        this.path = path;
        this.topic = topic;
        this.survey_id = survey_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(String survey_id) {
        this.survey_id = survey_id;
    }
}
