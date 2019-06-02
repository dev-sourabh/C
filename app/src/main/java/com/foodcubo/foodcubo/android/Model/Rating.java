package com.foodcubo.foodcubo.foodcubo.Model;

public class Rating {
   private String userPhone;
   private String restaurantId;
   private String rateValue;
   private String comment;

    public Rating() {
    }

    public Rating(String userPhone, String foodId, String rateValue, String comment) {
        this.userPhone = userPhone;
        this.restaurantId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void getRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
