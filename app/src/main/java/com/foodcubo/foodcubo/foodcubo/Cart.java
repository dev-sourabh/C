package com.foodcubo.foodcubo.foodcubo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodcubo.foodcubo.android.ChangeAddressActivity;
import com.foodcubo.foodcubo.android.ChangeUserDetailsActivity;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.android.Remote.APIService;
import com.foodcubo.foodcubo.android.RestaurantList;
import com.foodcubo.foodcubo.android.ViewHolder.CartAdapter;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Helper.RecyclerItemTouchHelper;
import com.foodcubo.foodcubo.foodcubo.Interface.RecyclerItemTouchHelperListener;
import com.foodcubo.foodcubo.foodcubo.Model.Order;
import com.foodcubo.foodcubo.foodcubo.Remote.IGoogleService;
import com.foodcubo.foodcubo.foodcubo.ViewHolder.CartViewHolder;
import com.foodcubo.foodcubo.foodcubo.Model.Token;
import com.foodcubo.foodcubo.android.Model.AdminRequest;
import com.foodcubo.foodcubo.android.Model.Request;
import com.foodcubo.foodcubo.android.Model.User;
import com.foodcubo.foodcubo.android.Model.Sender;
import com.foodcubo.foodcubo.android.Model.Notification;
import com.foodcubo.foodcubo.foodcubo.Model.MyResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmClientCertificate;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
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
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    String address, comment;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests, requests1;

    public TextView txtTotalPrice, tvListRestaurant, tvEmptyCart, tv_itemtotal,
            tv_deliverycharges, tv_gstcharges, tv_total, tv_couponcode, tv_couponamount, tv_billsaving, tv_addresstype,
            tv_fulladdress, change_add_address, tv_addresserror,change_user_details,tv_username,tv_user_phonenumber;
    public ImageView iv_deletecoupon,img_back;
    public LinearLayout rl_couponapplied, l_address, cartLayout, bill_bottom_layout;
    public RelativeLayout rl_apply_coupon, address_layout,rl_applied_coupon,billsaved_layout;
    Button btnPlace;
    boolean couponapplied=false;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    ProgressBar progress;

    static List<List<Order>> orderList = new ArrayList<>();
    static float total;

    RelativeLayout rootLayout;


    APIService mService;
    IGoogleService mGoogleMapService;

    //Location
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;
    private static final int LOCATION_REQUEST_CODE = 9999;
    private static final int PLAY_SERVICES_REQUEST = 9997;
    private NumberFormat fmt;
    private String smsMessage,smsPhoneNumber;

    PaytmPGService Service;
    private static final String TAG = "Cart";
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Locale locale = new Locale("en-IN", "IN");
        fmt = NumberFormat.getCurrencyInstance(locale);

        //permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_REQUEST_CODE);

        } else {
            if (checkPlayService()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }
        mGoogleMapService = Common.getGoogleMapAPI();
        mService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Restaurants").child(Common.restaurantSelected).child("Requests");
        requests1 = database.getReference("Requests");

        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = findViewById(R.id.rootLayout);
        //swipe  delete item
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        progress = findViewById(R.id.progress);
        tvEmptyCart = findViewById(R.id.tv_empty_cart);
        l_address = findViewById(R.id.l_address);
        rl_couponapplied = findViewById(R.id.rl_couponapplied);
        iv_deletecoupon = findViewById(R.id.iv_deletecoupon);
        cartLayout = findViewById(R.id.cart_layout);
        address_layout = findViewById(R.id.address_layout);
        bill_bottom_layout = findViewById(R.id.bill_bottom_layout);
        billsaved_layout = findViewById(R.id.billsaved_layout);
        rl_applied_coupon = findViewById(R.id.rl_applied_coupon);
        rl_apply_coupon = findViewById(R.id.rl_apply_coupon);
        tvListRestaurant = findViewById(R.id.listRestaurant);
        txtTotalPrice = findViewById(R.id.total);
        tv_addresserror = findViewById(R.id.tv_addresserror);
        change_add_address = findViewById(R.id.change_add_address);
        tv_username = findViewById(R.id.user_name);
        tv_user_phonenumber = findViewById(R.id.user_phone);
        change_user_details = findViewById(R.id.change_user_details);
        img_back = findViewById(R.id.img_backToMain);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_couponamount = findViewById(R.id.tv_couponamount);
        tv_gstcharges = findViewById(R.id.tv_gstcharges);
        tv_couponcode = findViewById(R.id.tv_couponcode);
        tv_fulladdress = findViewById(R.id.tv_fulladdress);
        tv_addresstype = findViewById(R.id.tv_addresstype);
        tv_total = findViewById(R.id.tv_total);
        tv_billsaving = findViewById(R.id.tv_billsaving);
        tv_itemtotal = findViewById(R.id.tv_itemtotal);
        tv_deliverycharges = findViewById(R.id.tv_deliverycharges);
        btnPlace = findViewById(R.id.btnPlaceOrder);
        rl_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cart.this, ApplyCouponsActivity.class);
                startActivityForResult(i, 120);
            }
        });
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.currentUser!=null){
                if (cart.size() > 0) {
                    //showPaymentDialog();
                    String taddress = tv_fulladdress.getText().toString();

                    if (taddress != null && !TextUtils.isEmpty(taddress) && !taddress.equals(""))
                        address = taddress;
                    else {
                        Toast.makeText(Cart.this, "Address is Empty!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Bundle args = new Bundle();
                    args.putSerializable("cart_list",(Serializable)cart);

                    args.putString("user_address",address);
                    args.putString("total_amt",txtTotalPrice.getText().toString());
                    //i.putExtra("edit_comment",edtComment.getText().toString());
                    args.putDouble("user_last_location_lat",mLastLocation.getLatitude());
                    args.putDouble("user_last_location_long",mLastLocation.getLongitude());
                    args.putBoolean("coupon_applied",couponapplied);
                    Intent i = new Intent(Cart.this, PaymentMethodActivity.class);
                    i.putExtras(args);
                    startActivityForResult(i, 133);


                }
                else
                    Toast.makeText(Cart.this, "Your cart is empty!!!", Toast.LENGTH_SHORT).show();

                }else{
                    Common.reopenApp(Cart.this);
                }
            }
        });
            if(Common.currentUser!=null){
                loadListFood();
            if (Common.currentUser.getHomeAddress() == null || TextUtils.isEmpty(Common.currentUser.getHomeAddress()) ||
                    Common.currentUser.getHomeAddress().equals("") || Common.currentUser.getHomeAddress().trim().equals("")) {
                tv_addresserror.setVisibility(View.VISIBLE);
                l_address.setVisibility(View.GONE);
                change_add_address.setText("ADD ADDRESS");
            } else {
                tv_addresstype.setText("Deliver to address");
                tv_fulladdress.setText(Common.currentUser.getHomeAddress());
                tv_addresserror.setVisibility(View.GONE);
                l_address.setVisibility(View.VISIBLE);
                change_add_address.setText("CHANGE ADDRESS");
            }
            }else{
            Common.reopenApp(Cart.this);
        }
        change_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cart.this, ChangeAddressActivity.class);
                startActivityForResult(i, 130);
            }
        });

        if (Common.currentUser.getName() == null || TextUtils.isEmpty(Common.currentUser.getName()) ||
                Common.currentUser.getName().equals("") || Common.currentUser.getName().trim().equals("") &&
                Common.currentUser.getPhone() == null || TextUtils.isEmpty(Common.currentUser.getPhone()) ||
                Common.currentUser.getPhone().equals("") || Common.currentUser.getPhone().trim().equals("")) {
            tv_username.setText("");
            tv_user_phonenumber.setText("");

        } else if(Common.currentUser.getPhone() != null && Common.currentUser.getName() == null){

            tv_username.setText("");
            tv_user_phonenumber.setText(Common.currentUser.getPhone());

        } else if(Common.currentUser.getPhone() == null && Common.currentUser.getName() != null){
            tv_username.setText(Common.currentUser.getName());
            tv_user_phonenumber.setText("");
        }else{
            tv_username.setText(Common.currentUser.getName());
            tv_user_phonenumber.setText(Common.currentUser.getPhone());
        }
        change_user_details.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Cart.this, ChangeUserDetailsActivity.class);
            //startActivity(i);
            startActivityForResult(i, 131);
        }
    });



    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Common.currentUser!=null) {
            if (requestCode == 120 && resultCode == RESULT_OK) {
                String couponcode = data.getStringExtra("couponcode");
                if (couponcode.equals("FIRSTCUBO60")) {
                    if (Common.currentUser.getFirstOrderApplied().equals("false")) {
                        rl_applied_coupon.setVisibility(View.VISIBLE);
                        rl_apply_coupon.setVisibility(View.GONE);
                        tv_couponcode.setText(couponcode);
                        couponapplied = true;

                        float total = 0;
                        List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());

                        for (Order item : orders)
                            total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                        float couponvalue = (float) (total * 0.60);
                        total = total - couponvalue;

                        billsaved_layout.setVisibility(View.VISIBLE);
                        tv_billsaving.setVisibility(View.VISIBLE);
                        tv_billsaving.setText("You have saved Rs." + couponvalue + " on the bill");

                        tv_itemtotal.setText(fmt.format(total));

                        float tax;
                        if (total > 0 && total <= 99) {
                            tax = 35;
                        } else if (total >= 100 && total <= 199) {
                            tax = 25;
                        } else if (total >= 200 && total <= 299) {
                            tax = 15;
                        } else {
                            tax = 0;
                        }
                        if (tax == 0)
                            tv_deliverycharges.setText("Free");
                        else
                            tv_deliverycharges.setText(fmt.format(tax));


                        float profit = (float) (total * 0.05);

                        tv_gstcharges.setText(fmt.format(profit));

                        total += tax + profit;

                        txtTotalPrice.setText(fmt.format(total));
                        tv_total.setText(fmt.format(total));
                        billsaved_layout.setVisibility(View.VISIBLE);
                        iv_deletecoupon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                rl_applied_coupon.setVisibility(View.GONE);
                                rl_apply_coupon.setVisibility(View.VISIBLE);
                                float total = 0;
                                List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());

                                for (Order item : orders)
                                    total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));


                                tv_itemtotal.setText(fmt.format(total));

                                float tax;
                                if (total > 0 && total <= 99) {
                                    tax = 35;
                                } else if (total >= 100 && total <= 199) {
                                    tax = 25;
                                } else if (total >= 200 && total <= 299) {
                                    tax = 15;
                                } else {
                                    tax = 0;
                                }
                                if (tax == 0)
                                    tv_deliverycharges.setText("Free");
                                else
                                    tv_deliverycharges.setText(fmt.format(tax));


                                float profit = (float) (total * 0.05);

                                tv_gstcharges.setText(fmt.format(profit));

                                total += tax + profit;

                                txtTotalPrice.setText(fmt.format(total));
                                tv_total.setText(fmt.format(total));

                                billsaved_layout.setVisibility(View.GONE);
                                couponapplied = false;
                                rl_applied_coupon.setVisibility(View.GONE);
                                rl_apply_coupon.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Coupon not valid!!", Toast.LENGTH_SHORT).show();
                        float total = 0;
                        List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());

                        for (Order item : orders)
                            total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));


                        tv_itemtotal.setText(fmt.format(total));

                        float tax;
                        if (total > 0 && total <= 99) {
                            tax = 35;
                        } else if (total >= 100 && total <= 199) {
                            tax = 25;
                        } else if (total >= 200 && total <= 299) {
                            tax = 15;
                        } else {
                            tax = 0;
                        }
                        if (tax == 0)
                            tv_deliverycharges.setText("Free");
                        else
                            tv_deliverycharges.setText(fmt.format(tax));


                        float profit = (float) (total * 0.05);

                        tv_gstcharges.setText(fmt.format(profit));

                        total += tax + profit;

                        txtTotalPrice.setText(fmt.format(total));
                        tv_total.setText(fmt.format(total));

                        billsaved_layout.setVisibility(View.GONE);
                        couponapplied = false;
                        rl_applied_coupon.setVisibility(View.GONE);
                        rl_apply_coupon.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Coupon not valid!!", Toast.LENGTH_SHORT).show();
                    billsaved_layout.setVisibility(View.GONE);
                    tv_billsaving.setVisibility(View.GONE);
                }
            } else if (requestCode == 130 && resultCode == RESULT_OK) {
                String finalAddress = data.getStringExtra("address_value");
                if (Common.currentUser.getHomeAddress() == null || TextUtils.isEmpty(Common.currentUser.getHomeAddress()) ||
                        Common.currentUser.getHomeAddress().equals("") || Common.currentUser.getHomeAddress().trim().equals("")) {

                    tv_addresstype.setText("Deliver to Other");
                    tv_fulladdress.setText(finalAddress);
                    tv_addresserror.setVisibility(View.GONE);
                    l_address.setVisibility(View.VISIBLE);
                    change_add_address.setText("CHANGE ADDRESS");
                } else {
                    if (finalAddress.equals(Common.currentUser.getHomeAddress())) {
                        tv_addresstype.setText("Deliver to RestaurantFoodDetails");
                    } else {
                        tv_addresstype.setText("Deliver to Other");
                    }
                    tv_fulladdress.setText(finalAddress);
                    tv_addresserror.setVisibility(View.GONE);
                    l_address.setVisibility(View.VISIBLE);
                    change_add_address.setText("CHANGE ADDRESS");
                }
            } else if (requestCode == 130 && resultCode == RESULT_OK) {
                String finalAddress = data.getStringExtra("address_value");


                if (Common.currentUser.getHomeAddress() == null || TextUtils.isEmpty(Common.currentUser.getHomeAddress()) ||
                        Common.currentUser.getHomeAddress().equals("") || Common.currentUser.getHomeAddress().trim().equals("")) {

                    tv_addresstype.setText("Deliver to Other");
                    tv_fulladdress.setText(finalAddress);
                    tv_addresserror.setVisibility(View.GONE);
                    l_address.setVisibility(View.VISIBLE);
                    change_add_address.setText("CHANGE ADDRESS");
                } else {
                    if (finalAddress.equals(Common.currentUser.getHomeAddress())) {
                        tv_addresstype.setText("Deliver to ");
                    } else {
                        tv_addresstype.setText("Deliver to Other");
                    }
                    tv_fulladdress.setText(finalAddress);
                    tv_addresserror.setVisibility(View.GONE);
                    l_address.setVisibility(View.VISIBLE);
                    change_add_address.setText("CHANGE ADDRESS");
                }
            } else if (requestCode == 131 && resultCode == RESULT_OK) {
                String finalUserName = data.getStringExtra("user_name");
                String finalUserPhoneNumber = data.getStringExtra("user_phone_number");
                tv_username.setText(finalUserName);
                tv_user_phonenumber.setText(finalUserPhoneNumber);
            }else if (requestCode == 133 ) {
                if(resultCode == RESULT_OK)
                {

                    progress.setVisibility(View.VISIBLE);
                    String finalOrderNumber = data.getStringExtra("orderNumber");
                    String paytmErrorResponse = data.getStringExtra("paytmCallBackErrorMessage");
                    String paytmSucessResponse = data.getStringExtra("paytmCallbackSuccessMessage");
                    if (finalOrderNumber != null && paytmSucessResponse != null && paytmErrorResponse == null) {
                        //if (finalOrderNumber != null){
                        //this is paytm sucess response
                        sendNotificationOrder(finalOrderNumber);

                    } else if (finalOrderNumber != null && paytmSucessResponse == null && paytmErrorResponse != null) {
                        //this is paytm error
                        Toast.makeText(Cart.this, "Sorry,Order not placed,please try again", Toast.LENGTH_LONG).show();
                        Common.restaurantCartName = "";
                        Common.restaurantCartPhone = "";
                        cart.clear();
                        progress.setVisibility(View.GONE);
                        finish();
                    } else if (finalOrderNumber != null && paytmSucessResponse == null && paytmErrorResponse == null) {
                        //this is cash on delivery order
                        sendNotificationOrder(finalOrderNumber);
                    } else {
                        //this may be back from cart
                        progress.setVisibility(View.GONE);
                        Common.restaurantCartName = "";
                        Common.restaurantCartPhone = "";
                        cart.clear();
                        finish();
                    }
                }else if(resultCode == 0){
                    progress.setVisibility(View.GONE);
                    Common.restaurantCartName = "";
                    Common.restaurantCartPhone = "";
                    cart.clear();
                    finish();
                }
            } else {
                Common.reopenApp(Cart.this);
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    private boolean checkPlayService() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported !!", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    /*private void showPaymentDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("PAYMENT");

        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View order_address_comment = inflater.inflate(R.layout.order_address_payment, null);

        *//*changes made by sonal*//*
        final MaterialEditText edtComment = order_address_comment.findViewById(R.id.edtComment);


        final RadioButton rdiCOD = order_address_comment.findViewById(R.id.rdiCOD);
        final RadioButton rdiPaypal = order_address_comment.findViewById(R.id.rdiPayPal);
        final RadioButton rdiBalance = order_address_comment.findViewById(R.id.rdiBalance);
   *//*chnages made by sonal*//*

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

    *//*chnages made by sonal*//*
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Common.currentUser!=null){
                    String taddress = tv_fulladdress.getText().toString();

                    if (taddress != null && !TextUtils.isEmpty(taddress) && !taddress.equals(""))
                        address = taddress;
                    else {
                        Toast.makeText(Cart.this, "Address is Empty!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    comment = edtComment.getText().toString();

                    if (!rdiCOD.isChecked() && !rdiPaypal.isChecked() && !rdiBalance.isChecked()) {
                        Toast.makeText(Cart.this, "Please Select Payment option", Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                .commit();
                        return;
                    } else if (rdiCOD.isChecked())
                    {
                        progress.setVisibility(View.VISIBLE);
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateTime = dateFormat.format(currentTime);
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                edtComment.getText().toString(),
                                "COD",
                                "Unpaid",
                                String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                Common.restaurantSelected,
                                cart, dateTime
                        );
                        AdminRequest adminRequest = new AdminRequest(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                edtComment.getText().toString(),
                                "COD",
                                "Unpaid",
                                String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                Common.restaurantSelected,
                                cart
                                , "0",
                                Common.restaurantCartPhone
                        );


                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number)
                                .setValue(request);
                        requests1.child(order_number)
                                .setValue(adminRequest);
                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                        if(couponapplied){
                            Map<String, Object> update_firstordercoupon = new HashMap<>();
                            update_firstordercoupon.put("firstOrderApplied", "true");

                            FirebaseDatabase.getInstance()
                                    .getReference("User")
                                    .child(Common.currentUser.getPhone())
                                    .updateChildren(update_firstordercoupon);
                            Common.currentUser.setFirstOrderApplied("true");

                        }
                        sendNotificationOrder(order_number);


                    } else if (rdiBalance.isChecked())
                    {

                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateTime = dateFormat.format(currentTime);
                        double amount = 0;
                        try {
                            amount = Common.formatCurrency(txtTotalPrice.getText().toString(), new Locale("hi","en")).doubleValue();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (Double.parseDouble(Common.currentUser.getBalance().toString()) >= amount) {
                            progress.setVisibility(View.VISIBLE);
                            Request request = new Request(
                                    Common.currentUser.getPhone(),
                                    Common.currentUser.getName(),
                                    address,
                                    txtTotalPrice.getText().toString(),
                                    "0",
                                    edtComment.getText().toString(),
                                    "FoodCubo Balance",
                                    "paid",
                                    String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                    Common.restaurantSelected,
                                    cart, dateTime
                            );

                            AdminRequest adminRequest = new AdminRequest(
                                    Common.currentUser.getPhone(),
                                    Common.currentUser.getName(),
                                    address,
                                    txtTotalPrice.getText().toString(),
                                    "0",
                                    edtComment.getText().toString(),
                                    "FoodCubo Balance",
                                    "paid",
                                    String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                    Common.restaurantSelected,
                                    cart
                                    , "0",
                                    Common.restaurantCartPhone
                            );

                            final String order_number = String.valueOf(System.currentTimeMillis());
                            requests.child(order_number)
                                    .setValue(request);
                            requests1.child(order_number)
                                    .setValue(adminRequest);
                            new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                            double balance = Double.parseDouble(Common.currentUser.getBalance().toString()) - amount;
                            Map<String, Object> update_balance = new HashMap<>();
                            update_balance.put("balance", balance);

                            if(couponapplied){
                                Map<String, Object> update_firstordercoupon = new HashMap<>();
                                update_firstordercoupon.put("firstOrderApplied", "true");

                                FirebaseDatabase.getInstance()
                                        .getReference("User")
                                        .child(Common.currentUser.getPhone())
                                        .updateChildren(update_firstordercoupon);
                                Common.currentUser.setFirstOrderApplied("true");

                            }
                            FirebaseDatabase.getInstance()
                                    .getReference("User")
                                    .child(Common.currentUser.getPhone())
                                    .updateChildren(update_balance)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance()
                                                        .getReference("User")
                                                        .child(Common.currentUser.getPhone())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                Common.currentUser = dataSnapshot.getValue(User.class);
                                                                sendNotificationOrder(order_number);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(Cart.this, "Your balance not enough ,Please choose anoother option", Toast.LENGTH_SHORT).show();
                        }

                    }


                }else{
                    Common.reopenApp(Cart.this);
                }


            }


        });
*//*changes made by sonal*//*



        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();


    }*/



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayService()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(progress.getVisibility()==View.VISIBLE)
            Toast.makeText(getApplicationContext(),"Please wait while order is placing",Toast.LENGTH_SHORT).show();
        else
            super.onBackPressed();
    }

    /*Putting send SMS code in Async task*/
    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = null;
            String msg = smsMessage;
            String phoneNumber = smsPhoneNumber;
            // Construct data
            String apiKey = "apikey=" + "uRlkT7rVE04-N0fmmUN5Q23xwENdH63iejlr4NO7k0";
            String message = "&message=" + "Message from FOOD CUBO "+msg;
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + phoneNumber;
            // Send data
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            try {
                conn.setRequestMethod("POST");
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            }
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            try {
                conn.getOutputStream().write(data.getBytes("UTF-8"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            BufferedReader rd = null;
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null) {
                    System.out.println("FOOD CUBO " + line);
                    stringBuffer.append(line);
                    rd.close();
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            responseString = stringBuffer.toString();
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }


    /*public String sendSms(String msg,String phonenumber) {
        try {
            // Construct data
            String apiKey = "apikey=" + "uRlkT7rVE04-N0fmmUN5Q23xwENdH63iejlr4NO7k0";
            String message = "&message=" + "Message from FOOD CUBO "+msg;
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + phonenumber;
            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                System.out.println("FOOD CUBO " + line);
                stringBuffer.append(line);
            }
            rd.close();

            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }
    }
 */   boolean showtoast=true;
    private void sendNotification(DataSnapshot postSnapshot, String finalToken, final String order_number) {
        if (postSnapshot.getKey().equals(finalToken)) {
            final Token serverToken = postSnapshot.getValue(Token.class);
            Notification notification = new Notification
                    ("Hi", "You have new order " + order_number);
            Sender content = new Sender(serverToken.getToken(), notification);
            smsMessage = "Hi You have new order "+ order_number;
            smsPhoneNumber = postSnapshot.getKey();
            new Cart.RequestTask().execute(smsMessage,smsPhoneNumber);
           // sendSms("Hi You have new order " + order_number,postSnapshot.getKey());
            mService.sendNotification(content)
                    .enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                            if (response.code() == 200) {

                                    if (showtoast)
                                        Toast.makeText(Cart.this, "Thank you , Order Placed", Toast.LENGTH_SHORT).show();
                                    showtoast=false;
                                    Common.restaurantCartName="";
                                    Common.restaurantCartPhone="";
                                    cart.clear();
                                    progress.setVisibility(View.GONE);
                                    finish();

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                            Log.e("ERROR", t.getMessage());
                        }
                    });
        }
    }

    public void sendNotificationOrder(final String order_number) {
        FirebaseDatabase fDatabase = FirebaseDatabase.getInstance();
        DatabaseReference tokens = fDatabase.getReference("Tokens");
        final Query data = tokens.orderByChild("serverToken").equalTo(true);   //servertoken

        DatabaseReference users = fDatabase.getReference("User");

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String restaurantToken = null;
                String adminRestaurantToken = null;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                            if (ds.child("isAdmin").exists()) {
                                try{
                                if (ds.child("isAdmin").getValue(String.class).equals("true")) {
                                    adminRestaurantToken = ds.getKey();
                                }
                                }catch (NullPointerException e) {
                                    e.printStackTrace();}
                            }

                    try {
                        if (ds.child("restaurantId").exists())
                            if (ds.child("restaurantId").getValue(String.class).equals(Common.restaurantSelected)) {
                                restaurantToken = ds.getKey();
                            }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
                final String finalRestaurantToken = restaurantToken;
                final String finalAdminRestaurantToken = adminRestaurantToken;
                data.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (finalRestaurantToken != null) {
                                sendNotification(postSnapshot, finalRestaurantToken, order_number);
                            }
                            if (finalAdminRestaurantToken != null) {
                                sendNotification(postSnapshot, finalAdminRestaurantToken, order_number);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intent = new Intent(this, RestaurantList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void loadListFood() {
        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        orderList.add(cart);

        if (cart.size() != 0) {
            tvEmptyCart.setVisibility(View.GONE);
            cartLayout.setVisibility(View.VISIBLE);
            address_layout.setVisibility(View.VISIBLE);
            bill_bottom_layout.setVisibility(View.VISIBLE);
            tvListRestaurant.setText(cart.get(0).getRestaurantName());
        } else {
            tvEmptyCart.setVisibility(View.VISIBLE);
            cartLayout.setVisibility(View.GONE);
            address_layout.setVisibility(View.GONE);
            bill_bottom_layout.setVisibility(View.GONE);
        }
        adapter = new CartAdapter(rootLayout, cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate total price
        total = 0;
        for (Order order : cart)
            total += (float) (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));


        if(couponapplied) {
            float couponvalue = (float) (total * 0.60);
            total = total - couponvalue;
            billsaved_layout.setVisibility(View.VISIBLE);
            tv_billsaving.setVisibility(View.VISIBLE);
            tv_billsaving.setText("You have saved Rs."+couponvalue+" on the bill");
        }else {
            billsaved_layout.setVisibility(View.GONE);
            tv_billsaving.setVisibility(View.GONE);
        }



        tv_itemtotal.setText(fmt.format(total));
        // add tax, profit to total, do we need to show the tax and profit on the app??
        float tax;
        if (total > 0 && total <= 99) {
            tax = 35;
        } else if (total >= 100 && total <= 199) {
            tax = 25;
        } else if (total >= 200 && total <= 299) {
            tax = 15;
        } else {
            tax = 0;
        }
        if (tax == 0)
            tv_deliverycharges.setText("Free");
        else
            tv_deliverycharges.setText(fmt.format(tax));

        float profit = (float) (total * 0.05);

        tv_gstcharges.setText(fmt.format(profit));

        total += tax + profit;


        txtTotalPrice.setText(fmt.format(total));
        tv_total.setText(fmt.format(total));

    }

    //Delete item

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int order) {


        if(Common.currentUser!=null){
        cart.remove(order);
        new Database(this).cleanCart(Common.currentUser.getPhone());

        for (Order item : cart)
            new Database(this).addToCart(item);

        loadListFood();
        }else{
            Common.reopenApp(Cart.this);
        }



        }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(Common.currentUser!=null){
        if (viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext())
                    .removeFromCart(deleteItem.getProductId(), Common.currentUser.getPhone(),deleteItem.getPriceType());

            float total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());

            for (Order item : orders)
                total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

            if(couponapplied) {
                float couponvalue = (float) (total * 0.60);
                total = total - couponvalue;
                billsaved_layout.setVisibility(View.VISIBLE);
                tv_billsaving.setVisibility(View.VISIBLE);
                tv_billsaving.setText("You have saved Rs."+couponvalue+" on the bill");
            }else {
                tv_billsaving.setVisibility(View.GONE);
                billsaved_layout.setVisibility(View.GONE);
            }
            tv_itemtotal.setText(fmt.format(total));

            float tax;
            if (total > 0 && total <= 99) {
                tax = 35;
            } else if (total >= 100 && total <= 199) {
                tax = 25;
            } else if (total >= 200 && total <= 299) {
                tax = 15;
            } else {
                tax = 0;
            }
            if (tax == 0)
                tv_deliverycharges.setText("Free");
            else
                tv_deliverycharges.setText(fmt.format(tax));

            float profit = (float) (total * 0.05);

            tv_gstcharges.setText(fmt.format(profit));

            total += tax + profit;

            txtTotalPrice.setText(fmt.format(total));
            tv_total.setText(fmt.format(total));

            Snackbar snackBar = Snackbar.make(rootLayout, name + " removed from cart !!!!!!", Snackbar.LENGTH_LONG);

            snackBar.setAction("UNDO", new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);
                    float total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());

                    for (Order item : orders)
                        total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                    if(couponapplied) {
                        float couponvalue = (float) (total * 0.60);
                        total = total - couponvalue;
                        billsaved_layout.setVisibility(View.VISIBLE);
                        tv_billsaving.setVisibility(View.VISIBLE);
                        tv_billsaving.setText("You have saved Rs."+couponvalue+" on the bill");
                    }else {
                        billsaved_layout.setVisibility(View.GONE);
                        tv_billsaving.setVisibility(View.GONE);
                    }

                    tv_itemtotal.setText(fmt.format(total));

                    float tax;
                    if (total > 0 && total <= 99) {
                        tax = 35;
                    } else if (total >= 100 && total <= 199) {
                        tax = 25;
                    } else if (total >= 200 && total <= 299) {
                        tax = 15;
                    } else {
                        tax = 0;
                    }
                    if (tax == 0)
                        tv_deliverycharges.setText("Free");
                    else
                        tv_deliverycharges.setText(fmt.format(tax));


                    float profit = (float) (total * 0.05);

                    tv_gstcharges.setText(fmt.format(profit));

                    total += tax + profit;

                    txtTotalPrice.setText(fmt.format(total));
                    tv_total.setText(fmt.format(total));

                    if (new Database(Cart.this).getCountCart(Common.currentUser.getPhone()) == 0) {
                        Common.restaurantCartName = "";
                        Common.restaurantCartPhone = "";
                        tvEmptyCart.setVisibility(View.VISIBLE);
                        cartLayout.setVisibility(View.GONE);
                        address_layout.setVisibility(View.GONE);
                        bill_bottom_layout.setVisibility(View.GONE);
                    } else {
                        tvEmptyCart.setVisibility(View.GONE);
                        cartLayout.setVisibility(View.VISIBLE);
                        address_layout.setVisibility(View.VISIBLE);
                        bill_bottom_layout.setVisibility(View.VISIBLE);
                    }
                }
            });
            snackBar.setActionTextColor(Color.YELLOW);
            snackBar.show();
            if (new Database(Cart.this).getCountCart(Common.currentUser.getPhone()) == 0) {
                Common.restaurantCartName = "";
                Common.restaurantCartPhone = "";
                tvEmptyCart.setVisibility(View.VISIBLE);
                cartLayout.setVisibility(View.GONE);
                address_layout.setVisibility(View.GONE);
                bill_bottom_layout.setVisibility(View.GONE);
            } else {
                tvEmptyCart.setVisibility(View.GONE);
                cartLayout.setVisibility(View.VISIBLE);
                address_layout.setVisibility(View.VISIBLE);
                bill_bottom_layout.setVisibility(View.VISIBLE);
            }
        }
        }else{
            Common.reopenApp(Cart.this);
        }


        }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.e("LOCATION", "your location : " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        } else {
            Log.e("LOCATION", "Could not get your location");
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }
}
