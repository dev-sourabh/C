package com.foodcubo.foodcubo.foodcubo.Model;

public class Restaurant {
    private String name,image,phoneNo;

    public Restaurant(String name, String image,String phoneNo) {
        this.name = name;
        this.image = image;
        this.phoneNo = phoneNo;
    }

    public Restaurant() {
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
