package com.foodcubo.foodcubo.android.Common;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import com.foodcubo.foodcubo.android.Model.User;
import com.foodcubo.foodcubo.android.Remote.APIService;
import com.foodcubo.foodcubo.foodcubo.MainActivity;
import com.foodcubo.foodcubo.android.Model.User;
import com.foodcubo.foodcubo.foodcubo.Remote.GoogleRetrofitClient;
import com.foodcubo.foodcubo.foodcubo.Remote.IGoogleService;
import com.foodcubo.foodcubo.foodcubo.Remote.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Common  {
    public static String topicName="News";
    public static User currentUser;
    public static final String DELETE;

    static {
        DELETE = "Delete";
    }

    public static String PHONE_TEXT="userPhone";

    public static String currentKey;

    public static final String INTENT_FOOD_ID="FoodId";

    public static String restaurantSelected="";
    public static String restaurantSelectedName="";
    public static String restaurantSelectedPhone="";
    public static String restaurantCartName="";
    public static String restaurantCartPhone="";



    private static final String BASE_URL="https://fcm.googleapis.com/";

    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";


    public static APIService getFCMService(){
        return
                RetrofitClient.getClient(BASE_URL).create(APIService.class);

    }

    public static void reopenApp(AppCompatActivity context){
        context.finish();
        context.startActivity(new Intent(context,MainActivity.class));
    }
    public static IGoogleService getGoogleMapAPI(){
        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }


    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager=
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null){
            NetworkInfo[] info =connectivityManager.getAllNetworkInfo();
            if(info!=null){
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;

    }
    public static String convertCodeToStatus(String status) {
        switch (status) {
            case "0":
                return "Placed";
            case "1":
                return "Shipper is on the way to Restaurant";
            case "2":
                return "Shipper ";
            default:
                return "Shipped";
        }
    }
    public  static BigDecimal formatCurrency(String amount, Locale locale) throws ParseException {
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        if(format instanceof DecimalFormat)
            ((DecimalFormat)format).setParseBigDecimal(true);
        return  (BigDecimal)format.parse(amount.replace("[^\\d.,]",""));
    }

}

