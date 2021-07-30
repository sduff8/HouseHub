package com.example.househub.Notifications;

public class Data {
    private String user, body, title, sented;
    private int icon;

    public Data(String user, int icon, String body, String title, String sented) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.icon = icon;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sented;
    }

    public void setSent(String sent) {
        this.sented = sent;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
