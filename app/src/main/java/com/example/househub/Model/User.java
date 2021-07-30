package com.example.househub.Model;

public class User {

    public String name, image, uid, family;

    public User(String name, String image, String uid, String family) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.family = family;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }
}
