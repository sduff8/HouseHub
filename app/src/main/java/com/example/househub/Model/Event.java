package com.example.househub.Model;

public class Event {

    private String type;
    private String date;
    private String time;
    private String description;

    public Event(String type, String date, String time, String description) {
        this.type = type;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public Event() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
