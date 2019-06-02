package com.foodcubo.foodcubo.android.Model;

import java.util.List;

public class AdminRequest {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<com.foodcubo.foodcubo.foodcubo.Model.Order> foods;
    private String comment;
    private String paymentMethod;
    private String paymentState;
    private boolean partial = false;
    private String latLng;
    private String restaurantId;
    private String restaurantPhone;
    private String tempShipper;

    public AdminRequest(String phone, String name, String address, String total,
                        String status, String comment, String paymentMethod, String paymentState,
                        String latLng, String restaurantId, List<com.foodcubo.foodcubo.foodcubo.Model.Order> foods, String tempShipper, String restaurantPhone) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.paymentMethod=paymentMethod;
        this.paymentState=paymentState;
        this.latLng = latLng;
        this.restaurantId = restaurantId;
        this.foods = foods;
        this.tempShipper = tempShipper;
        this.restaurantPhone = restaurantPhone;
    }

    public AdminRequest(String phone, String name, String address, String s, String s1, String s2, String cod, String unpaid, String format, String restaurantSelected, List<com.foodcubo.foodcubo.foodcubo.Model.Order> cart, String s3) {

    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<com.foodcubo.foodcubo.foodcubo.Model.Order> getFoods() {
        return foods;
    }

    public void setFoods(List<com.foodcubo.foodcubo.foodcubo.Model.Order> foods) {
        this.foods = foods;
    }

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getTempShipper() {
        return tempShipper;
    }

    public void setTempShipper(String tempShipper) {
        this.tempShipper = tempShipper;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }
}
