package com.foodcubo.foodcubo.android.Model;

/**
 * Created by 123456 on 2017/11/16.
 */

public class User {
    private String Name;
    private String Password;
    private String FirstOrderApplied;
    private String Phone;
    private String IsUser;
    private String IsStaff;
    private String secureCode;
    private String homeAddress;
    private Object balance;
    private String SecondaryPhoneNumber;

    public String getSecondaryPhoneNumber() {
        return SecondaryPhoneNumber;
    }

    public void setSecondaryPhoneNumber(String secondaryPhoneNumber) {
        SecondaryPhoneNumber = secondaryPhoneNumber;
    }



    public User() {
    }

    public User(String name, String password,String secureCode) {
        Name = name;
        Password = password;
        IsStaff="false";
        this.secureCode=secureCode;
    }

    public Object getBalance() {
        return balance;
    }

    public void setBalance(Object balance) {
        this.balance = balance;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPhone() {

        return Phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getFirstOrderApplied() {
        return FirstOrderApplied;
    }

    public void setFirstOrderApplied(String firstOrderApplied) {
        FirstOrderApplied = firstOrderApplied;
    }

    public String getIsUser() {
        return IsUser;
    }

    public void setIsUser(String isUser) {
        IsUser = isUser;
    }
}
