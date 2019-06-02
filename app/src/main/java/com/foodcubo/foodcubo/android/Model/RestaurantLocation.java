package com.foodcubo.foodcubo.foodcubo.Model;

public class RestaurantLocation {
    private String latitude, longitude;

    public RestaurantLocation() {
    }

    public RestaurantLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
