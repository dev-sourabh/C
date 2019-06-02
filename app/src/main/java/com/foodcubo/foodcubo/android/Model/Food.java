package com.foodcubo.foodcubo.foodcubo.Model;

public class Food {
    private String Key, Name, Image, Description, pieceType, fullprice
            , halfprice, Discount, MenuId, FoodId, AvailabilityFlag, vegType,categoryName;

    public Food() {
    }

    public Food(String key,String name, String image, String description, String pieceType, String fullprice, String halfprice, String discount,
                String menuId, String foodId, String availabilityFlag, String vegType) {
        Key = key;
        Name = name;
        Image = image;
        Description = description;
        this.pieceType = pieceType;
        this.fullprice = fullprice;
        this.halfprice = halfprice;
        Discount = discount;
        MenuId = menuId;
        FoodId = foodId;
        AvailabilityFlag = availabilityFlag;
        this.vegType = vegType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Food(String key, String name, String image, String description, String pieceType, String fullprice,
                String halfprice, String discount,
                String menuId, String foodId, String availabilityFlag, String vegType, String categoryName) {
        this(key,name,image,description,pieceType,fullprice,halfprice,discount,menuId,foodId,availabilityFlag,vegType);
        this.categoryName=categoryName;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getAvailabilityFlag() {
        return AvailabilityFlag;
    }

    public void setAvailabilityFlag(String availabilityFlag) {
        AvailabilityFlag = availabilityFlag;
    }

    public String getVegType() {
        return vegType;
    }

    public void setVegType(String vegType) {
        this.vegType = vegType;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getFullprice() {
        return fullprice;
    }

    public void setFullprice(String fullprice) {
        this.fullprice = fullprice;
    }

    public String getHalfprice() {
        return halfprice;
    }

    public void setHalfprice(String halfprice) {
        this.halfprice = halfprice;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }
}
