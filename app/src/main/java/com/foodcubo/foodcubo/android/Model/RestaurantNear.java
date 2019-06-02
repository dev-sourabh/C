package com.foodcubo.foodcubo.foodcubo.Model;

public class RestaurantNear {
    private String id,key,name,image,phoneNo;

    public RestaurantNear(String id, String key, String name, String image, String phoneNo) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.image = image;
        this.phoneNo = phoneNo;
    }


    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }


    public String getPhoneNo() {
        return phoneNo;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }
}
