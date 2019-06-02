package com.foodcubo.foodcubo.foodcubo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foodcubo.foodcubo.android.ChangeAddressActivity;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.Helper.CheckSumServiceHelper;
import com.foodcubo.foodcubo.android.Model.AdminRequest;
import com.foodcubo.foodcubo.android.Model.Request;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Model.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.http.Url;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class PaymentMethodActivity extends AppCompatActivity implements View.OnClickListener, PaytmPaymentTransactionCallback{
    private String user_address,user_cart_total_amt, order_number,paytmChecksumHashCode,
            paytmCallBackErrorMessage,paytmCallbackSuccessMessage,merchantKey,merchantMid,transactionURL,
            website,industryTypeId,callBackUrl,channelId;
    private Double user_last_Location_latitude,user_last_Location_longitude;
    List<Order> cart = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference requests,requests1;
    private Boolean couponapplied;
    ImageView imgBackToMainActivity,imgPaytmClick,imgCODClick,imgRupeesClick,imgPaytmIconClick;
    TextView tv_paytm,tv_cod,tv_total_amt;
    PaytmPGService Service;
    private NumberFormat fmt;
    //Boolean transactionDone = FALSE;
    //ProgressBar mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        //Paytm intialization
        Service = PaytmPGService.getStagingService();
        tv_total_amt = findViewById(R.id.pay_amount);
        Locale locale = new Locale("en-IN", "IN");
        fmt = NumberFormat.getCurrencyInstance(locale);
       // mLoadingProgress = findViewById(R.id.progress);
        merchantMid = "FOODCU25633701323470";
        merchantKey = "oGn8LbReRCAZBIpl";
        transactionURL = "https://securegw-stage.paytm.in/merchant-status/getTxnStatus";
        website = "APPSTAGING";
        industryTypeId = "Retail";
        callBackUrl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
        channelId = "WAP";

            Intent data = getIntent();
            Bundle extras = this.getIntent().getExtras();
            user_address = extras.getString("user_address");
            user_last_Location_latitude =extras.getDouble("user_last_location_lat");
            user_last_Location_longitude =extras.getDouble("user_last_location_long");
            user_cart_total_amt = extras.getString("total_amt");
            Number changeToNum  = null;
            try {
                changeToNum = fmt.parse(user_cart_total_amt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
             double changeToDoubleNo = changeToNum.doubleValue();

             BigDecimal cart_total = new BigDecimal(changeToDoubleNo).setScale(2,RoundingMode.HALF_UP);


            couponapplied = extras.getBoolean("couponapplied");
            cart =(List<Order>)extras.getSerializable("cart_list");

            tv_total_amt.setText(user_cart_total_amt);
             user_cart_total_amt =cart_total.toString();
        //Firebase
             database = FirebaseDatabase.getInstance();
            requests = database.getReference("Restaurants").child(Common.restaurantSelected).child("Requests");
            requests1 = database.getReference("Requests");


            imgBackToMainActivity =  findViewById(R.id.img_backToMain);
            imgPaytmClick = findViewById(R.id.img_paytm);
            imgCODClick=  findViewById(R.id.img_cod);
            imgRupeesClick =findViewById(R.id.img_rupees);
            imgPaytmIconClick =  findViewById(R.id.img_paytm_icon);

            tv_paytm =findViewById(R.id.tv_paytm);
            tv_cod =findViewById(R.id.tv_cod);

            imgBackToMainActivity.setOnClickListener(this);
            imgPaytmClick.setOnClickListener(this);
            imgCODClick.setOnClickListener(this);
            imgPaytmIconClick.setOnClickListener(this);
            imgRupeesClick.setOnClickListener(this);

            tv_paytm.setOnClickListener(this);
            tv_cod.setOnClickListener(this);


        }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        //    setResult(com.foodcubo.foodcubo.foodcubo.SearchLocationActivity.RESULT_CANCELED);
        finish();
    }


    @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_backToMain:
                    onBackPressed();
                    break;
                case R.id.img_paytm:
                {
                    String payment_method ="PayTm";
                    String payment_status = "Unpaid";
                    order_number = createOrder(payment_method,payment_status);
                    paytmChecksumHashCode=createPaytmChecksumHash(order_number);
                    createPaytmOrder(paytmChecksumHashCode,order_number);
                }
                break;
                case R.id.img_paytm_icon:
                {
                    String payment_method ="PayTm";
                    String payment_status = "Unpaid";
                    order_number = createOrder(payment_method,payment_status);
                    paytmChecksumHashCode=createPaytmChecksumHash(order_number);
                    createPaytmOrder(paytmChecksumHashCode,order_number);
                }
                break;
                case R.id.tv_paytm:
                {
                    String payment_method ="PayTm";
                    String payment_status = "Unpaid";
                    order_number = createOrder(payment_method,payment_status);
                    paytmChecksumHashCode=createPaytmChecksumHash(order_number);
                    createPaytmOrder(paytmChecksumHashCode,order_number);
                    //verifyPaytmChecksumHashCode(paytmChecksumHashCode);

                }
                break;
                case R.id.img_rupees:
                {
                    String payment_method ="COD";
                    String payment_status = "Unpaid";
                    order_number = createOrder(payment_method,payment_status);
                    Intent data = new Intent();
                    data.putExtra("orderNumber", order_number);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
                case R.id.img_cod:
                {
                    String payment_method ="COD";
                    String payment_status = "Unpaid";
                    order_number = createOrder(payment_method,payment_status);
                    Intent data = new Intent();
                    data.putExtra("orderNumber", order_number);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
                case R.id.tv_cod:
                {
                    String payment_method ="COD";
                    String payment_status = "Unpaid";
                    order_number = createOrder(payment_method,payment_status);
                    Intent data = new Intent();
                    data.putExtra("orderNumber", order_number);
                    setResult(RESULT_OK, data);
                    finish();

                }
                break;
            }
}

      /* private String verifyPaytmChecksumHashCode(String checksumCode){
                    String verifiedCheckSumCode = checksumCode;
                     String merchantKey = "oGn8LbReRCAZBIpl";
                    String paytmChecksum = null;
                     // Create a tree map from the form post param
                    TreeMap<String, String> paytmParams = new TreeMap<String, String>();
                  // Request is HttpServletRequest
                    for (Map.Entry<String, String[]> requestParamsEntry : request.getParameterMap().entrySet()) {
                        if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())){
                            paytmChecksum = requestParamsEntry.getValue()[0];
                        } else {
                            paytmParams.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
                        }
                    }
                    // Call the method for verification
                    boolean isValidChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(merchantKey, paytmParams, paytmChecksum);
                    // If isValidChecksum is false, then checksum is not valid
                    if(isValidChecksum){
                        System.out.append("Checksum Matched");
                    }else{
                        System.out.append("Checksum MisMatch");
                    }

            return verifiedCheckSumCode;
        }
*/

        private void createPaytmOrder(String checkSumHAshCode,String orderNo){

            String orderNumber = orderNo ;
            String custId = Common.currentUser.getPhone();

            custId  = custId .replaceAll("[\\+]","");
            String mobileNo = Common.currentUser.getPhone();
            String email = "sonal.singh2288@gmail.com";
            String txnAmount = user_cart_total_amt;

            String checkSumHashCodeLocal = checkSumHAshCode;

            Map<String, String> paramMap = new HashMap<String,String>();
            paramMap.put( "MID" , merchantMid);

            paramMap.put( "ORDER_ID" , order_number);
            paramMap.put( "CUST_ID" , custId);
            paramMap.put( "MOBILE_NO" , mobileNo);
            paramMap.put( "EMAIL" , email);
            paramMap.put( "CHANNEL_ID" , "WAP");
            paramMap.put( "TXN_AMOUNT" , txnAmount);
            paramMap.put( "WEBSITE" ,website );
            // This is the staging value. Production value is available in your dashboard
            paramMap.put( "INDUSTRY_TYPE_ID" , industryTypeId);
            // This is the staging value. Production value is available in your dashboard
            paramMap.put( "CALLBACK_URL", callBackUrl+orderNumber);
            paramMap.put( "CHECKSUMHASH" , checkSumHashCodeLocal);
            PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);

            // PaytmClientCertificate Certificate = new PaytmClientCertificate(String inPassword, String inFileName);
            // inPassword is the password for client side certificate
            //inFileName is the file name of client side certificate
            Service.initialize(Order,null);
            Service.startPaymentTransaction(this, true,
                    true, this);

        }
    class RequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = null;
            try {
                URL url = new URL(transactionURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    // Do normal input or output stream reading
                    responseString=verifyPaytmResponse(order_number,url);
                }
                else {
                    responseString = "FAILED"; //
                    conn.disconnect();// See documentation for more info on response handling
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Boolean responseCheckOrderNo = FALSE;
            Boolean responseCheckTotalAmt = FALSE;

            /*Trying to convert String into JSON object and get data*/
            JSONObject jObject  = null; // json
            String orderId =null;
            String txnAmt = null;
            try {
                jObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jObject != null) {
                try {
                    orderId = jObject.getString("ORDERID"); // get data object
                    txnAmt= jObject.getString("TXNAMOUNT"); // get data object
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(orderId.equals(order_number))
                responseCheckOrderNo = TRUE;

            if(txnAmt.equals(user_cart_total_amt))
                responseCheckTotalAmt = TRUE;


           /* String[] couple = result.split(",");

            for(int i =0; i < couple.length ; i++) {
                String[] items =couple[i].split(":");

               String name = items[0].replaceAll("[{}]","");
                 name = name.replace("\"","");
                String value = items[1].replace("\"","");

                if("ORDERID".equals(name))
                {
                    if(value.equals(order_number))
                        responseCheckOrderNo = TRUE;
                }
                 if("TXNAMOUNT".equals(name) ){
                     if (value.equals(user_cart_total_amt))
                         responseCheckTotalAmt = TRUE;
                 }
            }

           */ //Do anything with response..
            if(responseCheckOrderNo == FALSE && responseCheckTotalAmt == FALSE){
                Toast.makeText(PaymentMethodActivity.this, "Transaction Failed,please try again!!", Toast.LENGTH_LONG).show()  ;
                paytmCallBackErrorMessage = "";
                paytmCallbackSuccessMessage ="";
                order_number ="";
                callBackIntentToCart(paytmCallBackErrorMessage, paytmCallbackSuccessMessage);
            }else {

                callBackIntentToCart(paytmCallBackErrorMessage, paytmCallbackSuccessMessage);
            }

        }
    }
        private String verifyPaytmResponse(String orderId,URL url)
        {
            String responseData = "";
            URL getTransactionUrl = url;

            String orderNo = orderId;

            TreeMap<String, String> paytmParams = new TreeMap<String, String>();
            paytmParams.put("MID", merchantMid);
            paytmParams.put("ORDERID", orderId);
            try {
                String paytmChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(merchantKey, paytmParams);
                paytmParams.put("CHECKSUMHASH", paytmChecksum);
                JSONObject obj = new JSONObject(paytmParams);
                String postData = "JsonData=" + obj.toString();

                HttpURLConnection connection = (HttpURLConnection) getTransactionUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("contentType", "application/json");
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
                requestWriter.writeBytes( postData);
                requestWriter.close();

                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                if((responseData = responseReader.readLine()) != null) {
                    System.out.append("Response Json = " + responseData);
                }
                System.out.append("Requested Json = " + postData + " ");
                responseReader.close();
                return responseData;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return responseData;
        }

        private void callBackIntentToCart(final String errorMessageReturn, final String successMessageReturn){

            Handler mainHandler = new Handler(Looper.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                  // String jsonResponse = verifyPaytmResponse(order_number);

                    Intent data = getIntent();
                    data.putExtra("orderNumber", order_number);
                    data.putExtra("paytmCallBackErrorMessage", errorMessageReturn);
                    data.putExtra("paytmCallbackSuccessMessage",successMessageReturn);

                    setResult(RESULT_OK, data);

                   finish();
                } // This is your code


            };
            mainHandler.post(myRunnable);


        }

    public void someUIErrorOccurred(String inErrorMessage) {
        Log.e("TAG", "Paytm UI Error");
        /*Display the error message as below */
       // Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
        //transactionDone = TRUE;
        paytmCallBackErrorMessage = inErrorMessage;
        Log.e("TAG", "Paytm Transaction Error" + inErrorMessage.toString());

        //callBackIntentToCart(paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
        new RequestTask().execute(order_number,paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
       }
    public void onTransactionResponse(Bundle inResponse) {
        /*Display the message as below */
      //  Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();

        paytmCallbackSuccessMessage=inResponse.toString();
        Log.e("TAG","Paytm Transaction response"+inResponse.toString());

        //callBackIntentToCart(paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
        new RequestTask().execute(order_number,paytmCallBackErrorMessage,paytmCallbackSuccessMessage);

    }
    public void networkNotAvailable() {
        /*Display the message as below */
        //Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();

        paytmCallBackErrorMessage ="Paytm Network Error";
        Log.e("TAG","Paytm Network Error");

        //callBackIntentToCart(paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
        new RequestTask().execute(order_number,paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
     }
    public void clientAuthenticationFailed(String inErrorMessage) {
        /*Display the message as below */
        //Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG).show();

        paytmCallBackErrorMessage =inErrorMessage;
        Log.e("TAG","Paytm ClientAuth error"+ inErrorMessage);

        //callBackIntentToCart(paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
        new RequestTask().execute(order_number,paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
      }
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        /*Display the message as below */
        //Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();

        paytmCallBackErrorMessage =inErrorMessage;
        Log.e("TAG","Paytm Error on loading page"+inErrorMessage);

        //callBackIntentToCart(paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
        new RequestTask().execute(order_number,paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
        }
    public void onBackPressedCancelTransaction() {
        /*Display the message as below */
        //Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();

        paytmCallBackErrorMessage ="Transaction cancelled,Paytm BackPressed Error";
        Log.e("TAG","Paytm BackPressed Error");

       // callBackIntentToCart(paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
        new RequestTask().execute(order_number,paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
      }
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        /*Display the message as below */
        //Toast.makeText(getApplicationContext(), "Transaction Cancelled " + inErrorMessage, Toast.LENGTH_LONG).show();

        paytmCallBackErrorMessage = "Transaction Cancelled "+inErrorMessage;
        Log.e("TAG","Paytm Transaction Cancel Error"+inErrorMessage);

       // callBackIntentToCart(paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
        new RequestTask().execute(order_number,paytmCallBackErrorMessage,paytmCallbackSuccessMessage);
      }


    private String createOrder(String PaymentMethod,String PaymentStatus){
             String payment_method = PaymentMethod;
            String payment_status = PaymentStatus;
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(currentTime);
            Request request = new Request(
                    Common.currentUser.getPhone(),
                    Common.currentUser.getName(),
                    user_address,
                    user_cart_total_amt,
                    "0",
                    "",
                    payment_method,
                    payment_status,
                    String.format("%s,%s", user_last_Location_latitude, user_last_Location_longitude),
                    Common.restaurantSelected,
                    cart, dateTime
            );
            AdminRequest adminRequest = new AdminRequest(
                    Common.currentUser.getPhone(),
                    Common.currentUser.getName(),
                    user_address,
                    user_cart_total_amt,
                    "0",
                    "",
                    payment_method,
                    payment_status,
                    String.format("%s,%s", user_last_Location_latitude, user_last_Location_longitude),
                    Common.restaurantSelected,
                    cart
                    , "0",
                    Common.restaurantCartPhone
            );


            String orderNo = String.valueOf(System.currentTimeMillis());
            requests.child(orderNo)
                    .setValue(request);
            requests1.child(orderNo)
                    .setValue(adminRequest);
            new com.foodcubo.foodcubo.foodcubo.Database.Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
            if(couponapplied){
                Map<String, Object> update_firstordercoupon = new HashMap<>();
                update_firstordercoupon.put("firstOrderApplied", "true");

                FirebaseDatabase.getInstance()
                        .getReference("User")
                        .child(Common.currentUser.getPhone())
                        .updateChildren(update_firstordercoupon);
                Common.currentUser.setFirstOrderApplied("true");

            };
        return orderNo;

        }

    private String createPaytmChecksumHash(String orderNumber) {

        String orderId = orderNumber;

        String custId = Common.currentUser.getPhone();
        custId = custId.replaceAll("[\\+]", "");
        String mobileNo = Common.currentUser.getPhone();

        String email = "sonal.singh2288@gmail.com";
        String txnAmount = user_cart_total_amt;

        TreeMap<String, String> paytmParams = new TreeMap<String, String>();
        paytmParams.put("MID", merchantMid);
        paytmParams.put("ORDER_ID", orderId);
        paytmParams.put("CHANNEL_ID", channelId);
        paytmParams.put("CUST_ID", custId);
        paytmParams.put("MOBILE_NO", mobileNo);
        paytmParams.put("EMAIL", email);
        paytmParams.put("TXN_AMOUNT", txnAmount);
        paytmParams.put("WEBSITE", website);
        paytmParams.put("INDUSTRY_TYPE_ID", industryTypeId);
        paytmParams.put("CALLBACK_URL", callBackUrl + orderId);
        String paytmChecksum = null;
        try {
            paytmChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(merchantKey, paytmParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paytmChecksum;
    }
}



