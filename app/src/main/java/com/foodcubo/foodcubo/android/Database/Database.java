package com.foodcubo.foodcubo.foodcubo.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.foodcubo.foodcubo.foodcubo.Model.Favorites;
import com.foodcubo.foodcubo.foodcubo.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "foodcubo4.db";
    private static final int DB_VER = 1;

    public Database(Context context){
        super(context, DB_NAME,null, DB_VER);
    }

    public int checkFoodExists(String foodId,String userPhone,String priceType){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor;
        String SQL=String.format("SELECT * From OrderDetail WHERE " +
                "UserPhone='%s' AND ProductId='%s' AND PriceType='%s'",userPhone,foodId,priceType);
        cursor=db.rawQuery(SQL,null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getFullOrderQuantity(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        int count = 0, tempCount;
        String SQL = String.format("SELECT Quantity From OrderDetail WHERE " +
                "UserPhone='%s'", userPhone);
        cursor = db.rawQuery(SQL, null);
       // cursor.moveToFirst();
        try {
            while (cursor.moveToNext()) {
                tempCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Quantity")));
                count = count+tempCount;
            }
        } finally {
            cursor.close();
        }
        return count;
       /* try {
             count = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Quantity")));
            cursor.close();
            return count;
        }catch (Exception e){
            return 0;
        }*/

    }
    public int getOrderQuantity(String foodId,String userPhone,String priceType){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor;
        String SQL=String.format("SELECT Quantity From OrderDetail WHERE " +
                "UserPhone='%s' AND ProductId='%s' AND PriceType='%s'",userPhone,foodId,priceType);
        cursor=db.rawQuery(SQL,null);
        cursor.moveToFirst();
        try {
            int count = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Quantity")));
            cursor.close();
            return count;
        }catch (Exception e){
            return 0;
        }
    }

    public String getCartRestaurant(){
        String flag="";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor;
        String SQL= "SELECT * From OrderDetail";
        cursor=db.rawQuery(SQL,null);
        if(cursor.getCount()>0) {
            if (cursor.moveToFirst()) {
                    flag = cursor.getString(cursor.getColumnIndex("RestaurantName"));
            }
        }
        cursor.close();
        return flag;
    }

    public String getCartRestaurantPhone(){
        String flag="";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor;
        String SQL= "SELECT * From OrderDetail";
        cursor=db.rawQuery(SQL,null);
        if(cursor.getCount()>0) {
            if (cursor.moveToFirst()) {
                    flag = cursor.getString(cursor.getColumnIndex("RestaurantPhone"));
            }
        }
        cursor.close();
        return flag;
    }

    public List<Order> getCarts(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ProductName", "ProductId",
                "Quantity", "Price", "Discount","Image","VegType","RestaurantName","PriceType","PieceType","RestaurantPhone"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, "UserPhone=?", new String[]{userPhone},
                null, null, null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image")),
                        c.getString(c.getColumnIndex("VegType")),
                        c.getString(c.getColumnIndex("RestaurantName")),
                        c.getString(c.getColumnIndex("PriceType")),
                        c.getString(c.getColumnIndex("PieceType")),
                        c.getString(c.getColumnIndex("RestaurantPhone"))
                        ));
            }while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail" +
                        "(UserPhone,ProductId, ProductName, Quantity, Price, Discount,Image,VegType" +
                        ",RestaurantName,PriceType,PieceType,RestaurantPhone) " +
                        "VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage(),
                order.getVegType(),
                order.getRestaurantName(),
                order.getPriceType(),
                order.getPieceType(),
                order.getRestaurantPhone());

        db.execSQL(query);
    }

    public void removeFromCart(String order){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE ProductId='"+order+"'");
        db.execSQL(query);

    }

    public void cleanCart(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);

    }

    public int getCountCart(String userPhone) {

        int count=0;
        SQLiteDatabase db=getReadableDatabase();
        String Query=String.format("SELECT COUNT(*) FROM OrderDetail Where UserPhone='%s'",userPhone);
        Cursor cursor=db.rawQuery(Query,null);
        if(cursor.moveToFirst()){
            do {
                count=cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;

    }

    public void updateCart(Order order) {

        SQLiteDatabase db=getReadableDatabase();
        String Query=String.format("UPDATE OrderDetail SET Quantity = '%s'" +
                " WHERE UserPhone = '%s' AND ProductId='%s' AND PriceType='%s'"
                ,order.getQuantity(),order.getUserPhone(),order.getProductId(),order.getPriceType());
        db.execSQL(Query);

    }

    public void increaseCart(String userPhone,String foodId,String priceType) {
        SQLiteDatabase db=getReadableDatabase();
        String Query=String.format("UPDATE OrderDetail SET Quantity = Quantity+1 WHERE " +
                "UserPhone = '%s' AND ProductId='%s' AND PriceType='%s'",userPhone,foodId,priceType);
        db.execSQL(Query);

    }

    public void removeFromCart(String productId, String phone,String priceType) {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId= '%s' and PriceType= '%s'"
                ,phone,productId,priceType);
        db.execSQL(query);
    }

    public void decreaseQuantityFromCart(String productId, String phone,String priceType) {
        SQLiteDatabase db=getReadableDatabase();
        String Query=String.format("UPDATE OrderDetail SET Quantity = Quantity-1 WHERE " +
                "UserPhone = '%s' AND ProductId='%s' AND PriceType='%s'",phone,productId,priceType);
        db.execSQL(Query);
    }

    public void addToFavorites(Favorites food){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO Favorites(" +
                "FoodId,FoodName,FoodFullPrice,FoodHalfPrice,FoodPieceType,FoodMenuId" +
                        ",FoodImage,FoodDiscount,FoodDescription,UserPhone,FoodVegType,FoodRestaurantName,FoodRestaurantPhone) " +
                "VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                food.getFoodId(),
                food.getFoodName(),
                food.getFoodfullprice(),
                food.getFoodhalfprice(),
                food.getFoodPieceType(),
                food.getFoodMenuId(),
                food.getFoodImage(),
                food.getFoodDiscount(),
                food.getFoodDescription(),
                food.getUserPhone(),
                food.getVegType(),
                food.getFoodRestaurantName(),
                food.getFoodRestaurantPhone());
        db.execSQL(query);
    }
    public void removeFromFavorites(String foodId,String userPhone){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM Favorites WHERE FoodId ='%s' and UserPhone ='%s';",foodId,userPhone);
        db.execSQL(query);
    }

    public boolean isFavorite(String foodId,String userPhone){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT * FROM Favorites WHERE FoodId ='%s' and UserPhone ='%s';",foodId,userPhone);
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.getCount()<=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Favorites> getAllFavorites(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","FoodId", "FoodName", "FoodFullPrice", "FoodHalfPrice", "FoodPieceType", "FoodMenuId",
                "FoodImage","FoodDiscount","FoodDescription","FoodVegType","FoodRestaurantName","FoodRestaurantPhone"};
        String sqlTable = "Favorites";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, "UserPhone=?",
                new String[]{userPhone}, null, null, null);

        final List<Favorites> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
              result.add(new Favorites(
                      c.getString(c.getColumnIndex("FoodId")),
                      c.getString(c.getColumnIndex("FoodName")),
                      c.getString(c.getColumnIndex("FoodFullPrice")),
                      c.getString(c.getColumnIndex("FoodHalfPrice")),
                      c.getString(c.getColumnIndex("FoodPieceType")),
                      c.getString(c.getColumnIndex("FoodMenuId")),
                      c.getString(c.getColumnIndex("FoodImage")),
                      c.getString(c.getColumnIndex("FoodDiscount")),
                      c.getString(c.getColumnIndex("FoodDescription")),
                      c.getString(c.getColumnIndex("UserPhone")),
                      c.getString(c.getColumnIndex("FoodVegType")),
                      c.getString(c.getColumnIndex("FoodRestaurantName")),
                      c.getString(c.getColumnIndex("FoodRestaurantPhone"))
              ));
            }while (c.moveToNext());
        }
        return result;
    }

}
