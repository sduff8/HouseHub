package com.example.househub.Model;

import de.hdodenhof.circleimageview.CircleImageView;

public class Contacts {

    String name, image, phone, address, cid;

    public Contacts(String image, String name, String phone, String address, String cid) {
        this.image = image;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.cid = cid;
    }

    public Contacts() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
