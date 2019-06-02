package com.foodcubo.foodcubo.foodcubo.Model;

public class Favorites {
    private String FoodId,FoodName, Foodfullprice, FoodPieceType, Foodhalfprice, FoodMenuId,
            FoodImage, FoodDiscount, FoodDescription,
            UserPhone, VegType, FoodRestaurantName, FoodRestaurantPhone;

    public Favorites() {
    }


    public Favorites(String foodId, String foodName, String fullprice, String halfprice, String pieceType, String foodMenuId,
                     String foodImage, String foodDiscount, String foodDescription,
                     String userPhone, String vegType, String foodRestaurantName, String foodRestaurantPhone) {
        FoodId = foodId;
        FoodName = foodName;
        Foodfullprice = fullprice;
        Foodhalfprice = halfprice;
        FoodPieceType = pieceType;
        FoodMenuId = foodMenuId;
        FoodImage = foodImage;
        FoodDiscount = foodDiscount;
        FoodDescription = foodDescription;
        UserPhone = userPhone;
        VegType = vegType;
        FoodRestaurantName = foodRestaurantName;
        FoodRestaurantPhone = foodRestaurantPhone;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodImage() {
        return FoodImage;
    }

    public void setFoodImage(String foodImage) {
        FoodImage = foodImage;
    }

    public String getFoodDescription() {
        return FoodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        FoodDescription = foodDescription;
    }

    public String getFoodDiscount() {
        return FoodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        FoodDiscount = foodDiscount;
    }

    public String getFoodMenuId() {
        return FoodMenuId;
    }

    public void setFoodMenuId(String foodMenuId) {
        FoodMenuId = foodMenuId;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }


    public String getVegType() {
        return VegType;
    }

    public void setVegType(String vegType) {
        VegType = vegType;
    }

    public String getFoodRestaurantName() {
        return FoodRestaurantName;
    }

    public void setFoodRestaurantName(String foodRestaurantName) {
        FoodRestaurantName = foodRestaurantName;
    }

    public String getFoodfullprice() {
        return Foodfullprice;
    }

    public void setFoodfullprice(String foodfullprice) {
        Foodfullprice = foodfullprice;
    }

    public String getFoodhalfprice() {
        return Foodhalfprice;
    }

    public void setFoodhalfprice(String foodhalfprice) {
        Foodhalfprice = foodhalfprice;
    }

    public String getFoodPieceType() {
        return FoodPieceType;
    }

    public void setFoodPieceType(String foodPieceType) {
        FoodPieceType = foodPieceType;
    }

    public String getFoodRestaurantPhone() {
        return FoodRestaurantPhone;
    }

    public void setFoodRestaurantPhone(String foodRestaurantPhone) {
        FoodRestaurantPhone = foodRestaurantPhone;
    }
}
