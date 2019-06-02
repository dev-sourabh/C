package com.foodcubo.foodcubo.android.Model;

import com.foodcubo.foodcubo.foodcubo.Model.Order;

import java.util.List;

/**
 * Created by 123456 on 2017/11/20.
 */

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String comment;
    private String cod;
    private  String unpaid;
    private String format;
    private String restaurantSelected;
    private List<com.foodcubo.foodcubo.foodcubo.Model.Order> cart;
    private String dateTime;
    private String paymentMethod;
    private String paymentState;
    private boolean partial = false;
    private String latLng;
    private String restaurantId;
    private String orderDateTime;
    private String tempShipper;
    private List<com.foodcubo.foodcubo.foodcubo.Model.Order> foods;

    public Request(String phone, String name, String address, String total, String status, String comment, String cod, String unpaid, String format, String restaurantSelected, List<com.foodcubo.foodcubo.foodcubo.Model.Order> cart, String dateTime) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.cod = cod;
        this.unpaid = unpaid;
        this.format = format;
        this.restaurantSelected = restaurantSelected;
        this.cart = cart;
        this.dateTime = dateTime;
    }
    public Request(){}

    public Request(String phone, String name, String address, String total, String status,
                   String comment, String cod, List<Order> cart, String dateTime, String paymentMethod, String paymentState, String latLng,
                   String restaurantId, List<Order> foods, String orderDateTime) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.cod = cod;
        this.cart = cart;
        this.dateTime = dateTime;
        this.paymentMethod = paymentMethod;
        this.paymentState = paymentState;
        this.latLng = latLng;
        this.orderDateTime = orderDateTime;
        this.restaurantId = restaurantId;
        this.foods = foods;
        unpaid = null;
    }

    public Request(String phone, String address, String s, String s1, String s2, String cod, String unpaid, String format, String restaurantSelected, List<Order> cart, String dateTime, String unpaid1) {

        this.unpaid = unpaid1;
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

    public List<com.foodcubo.foodcubo.foodcubo.Model.Order> getFoods() {
        return foods;
    }

    public void setFoods(List<com.foodcubo.foodcubo.foodcubo.Model.Order> foods) {
        this.foods = foods;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getTempShipper() {
        return tempShipper;
    }

    public void setTempShipper(String tempShipper) {
        this.tempShipper = tempShipper;
    }
}
