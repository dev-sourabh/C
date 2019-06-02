package com.foodcubo.foodcubo.android.Model;

//import android.app.Notification;
import com.foodcubo.foodcubo.android.Model.Notification;

public class Sender {
    public String to;
    public Notification notification;

    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    /*public Sender(String token, com.foodcubo.foodcubo.foodcubo.Model.Notification notification) {

    }
commented by sonal */
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
