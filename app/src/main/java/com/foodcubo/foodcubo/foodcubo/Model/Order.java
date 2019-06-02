package com.foodcubo.foodcubo.foodcubo.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {
    private String restaurantPhone;
    private String UserPhone;
    private String ProductId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String Image;
    private String vegType;
    private String restaurantName;
    private String priceType;
    private String pieceType;

    public Order() {
    }



    public Order(String productId, String productName, String quantity, String price, String discount,String image) {
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image=image;
       // restName=RestName;

    }

    public Order(String userPhone, String productId, String productName, String quantity
            , String price, String discount, String image, String vegtype, String restaurantname
            ,String pricetype,String pieceType,String restaurantPhone ) {
        UserPhone = userPhone;
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
        vegType=vegtype;
        restaurantName=restaurantname;
        priceType=pricetype;
        this.pieceType=pieceType;
        this.restaurantPhone=restaurantPhone;
    }

    protected Order(Parcel in) {
        restaurantPhone = in.readString();
        UserPhone = in.readString();
        ProductId = in.readString();
        ProductName = in.readString();
        Quantity = in.readString();
        Price = in.readString();
        Discount = in.readString();
        Image = in.readString();
        vegType = in.readString();
        restaurantName = in.readString();
        priceType = in.readString();
        pieceType = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getVegType() {
        return vegType;
    }

    public void setVegType(String vegType) {
        this.vegType = vegType;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(restaurantPhone);
        dest.writeString(UserPhone);
        dest.writeString(ProductId);
        dest.writeString(ProductName);
        dest.writeString(Quantity);
        dest.writeString(Price);
        dest.writeString(Discount);
        dest.writeString(Image);
        dest.writeString(vegType);
        dest.writeString(restaurantName);
        dest.writeString(priceType);
        dest.writeString(pieceType);
    }
}
