package com.foodcubo.foodcubo.android;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.facebook.accountkit.AccountKit;
import com.foodcubo.foodcubo.android.Adapter.RestaurantListAdapter;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.foodcubo.Model.Rating;
import com.foodcubo.foodcubo.foodcubo.Model.RestaurantLocation;
import com.foodcubo.foodcubo.foodcubo.Model.RestaurantNear;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

@SuppressLint("Registered")
public class RestaurantList extends AppCompatActivity implements RestaurantListAdapter.RestaurantListClickInterface
        , RatingDialogListener, View.OnClickListener {

    CounterFab fab;
    private AdView mAdView;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    String tempRestaurantId;
    DatabaseReference ratingTbl;
    FirebaseDatabase database;
    private TextView tvTitle;
    private CardView offer_layout;
    TextView change;
    ImageView navigationnn;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout drawerll;
    LinearLayout nav_menu, nav_cart, nav_orders, nav_log_out, nav_update_name,
            nav_nearby_store, nav_home_address, nav_settings, nav_fav;

    private ArrayList<Float> restaurantsDistances = new ArrayList<>();
    private ArrayList<RestaurantNear> restaurantsSorted = new ArrayList<>();
    private RestaurantListAdapter adapter;
    DatabaseReference restaurantList;
    ImageView imageView;


 /*   FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(options) {
        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_item, parent, false);
            return new RestaurantViewHolder(itemView);
        }

        @Override
        protected void onBindViewHolder(@NonNull final RestaurantViewHolder holder, int position, @NonNull final Restaurant model) {
            holder.txtRestaurantName.setText(model.getName());
            Picasso.with(getBaseContext()).load(model.getImage()).into(holder.restaurantImage);
            holder.btnRestaurantRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempRestaurantId = adapter.getRef(holder.getAdapterPosition()).getKey();
                    showRatingDialog();
                }
            });
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    //Get CategoryId and send to new Activity
                    Intent foodList = new Intent(RestaurantList.this, RestaurantDetailsWithFoodLists.class);
                    foodList.putExtra("phoneNo", model.getPhoneNo());
                    foodList.putExtra("restaurantName", model.getName());
                    //Because CategoryId is key, so we just get the key of this item
                    Common.restaurantSelected = adapter.getRef(position).getKey();
                    startActivity(foodList);
                }
            });
        }


    };
*/

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Food")
                .setDescription("Please Select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(RestaurantList.this)
                .show();
    }

    private String getFullLocationAddress(List<Address> addresses) {

        Address returnedAddress = addresses.get(0);

        StringBuilder strReturnedAddress = new StringBuilder();

        for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
        }
        return strReturnedAddress.toString();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {


            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null) {
                String address1 = addresses.get(0).getSubLocality() + " , " + addresses.get(0).getLocality();
                if (!address1.equals("") && !TextUtils.isEmpty(address1) && address1.equals("null")) {
                    if (addresses.get(0).getSubLocality().equals("") && TextUtils.isEmpty(addresses.get(0).getSubLocality()))
                        strAdd = addresses.get(1).getLocality();
                    else if (addresses.get(1).getSubLocality().equals("") && TextUtils.isEmpty(addresses.get(1).getSubLocality()))
                        strAdd = addresses.get(0).getLocality();
                    else
                        strAdd = addresses.get(0).getSubLocality() + " , " + addresses.get(0).getLocality();

                } else {

                    String addresfs1 = addresses.get(0).getFeatureName();
                    if (!addresfs1.equals("")) {
                        TextUtils.isEmpty(addresfs1);
                    }
                    if (false) {
                        strAdd = addresfs1;
                    } else
                        strAdd = getFullLocationAddress(addresses);

                }
                if (strAdd.equals("") && TextUtils.isEmpty(strAdd) && strAdd.equals("null"))
                    strAdd = getFullLocationAddress(addresses);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private static final int MY_PERMISION_CODE = 10;

    private void showAlert() {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings are OFF \nPlease Enable Location")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                        ActivityCompat.requestPermissions(RestaurantList.this,
                                new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION},
                                MY_PERMISION_CODE);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }


    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double latString, longstring;

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_COARSE_LOCATION)) {
                    showAlert();

                } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION},
                                MY_PERMISION_CODE);
                }


            }
        } else {

            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        latString = location.getLatitude();
                        longstring = location.getLongitude();
                        tvTitle.setText(getCompleteAddressString(latString, longstring));
                        loadRestaurant();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error in fetching the location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 400) {
            if (resultCode == RESULT_OK) {

                latString = data.getDoubleExtra("lat", latString);
                longstring = data.getDoubleExtra("lng", longstring);
                tvTitle.setText(data.getStringExtra("address"));
                loadRestaurant();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISION_CODE: {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                latString = location.getLatitude();
                                longstring = location.getLongitude();
                                tvTitle.setText(getCompleteAddressString(latString, longstring));
                                loadRestaurant();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in fetching the location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RestaurantList.this, "Enable Permissions to access the features of this App", Toast.LENGTH_LONG).show();

                    if (Common.isConnectedToInternet(getBaseContext()))
                        getUserLocation();
                    else {
                        Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_restaurant_list);

        offer_layout = findViewById(R.id.offer_layout);
        if(Common.currentUser!=null){
        if(Common.currentUser.getFirstOrderApplied().equals("false"))
            offer_layout.setVisibility(View.VISIBLE);
        else
            offer_layout.setVisibility(View.GONE);

        }else{
            Common.reopenApp(RestaurantList.this);
        }

        /*AdMob code here..
        * original app id ca-app-pub-7597723136432333~7110139032*/
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");


        mAdView = findViewById(R.id.adView);
      AdRequest adRequest = new AdRequest.Builder().build();
       /* AdRequest request = new AdRequest.Builder()
                .addTestDevice("744D97B303CBFE15FEB7CD25D7E744B1")  // An example device ID
                .build();*/

      mAdView.loadAd(adRequest);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //Toast.makeText(getBaseContext(), "Add click loaded", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
               // Toast.makeText(getBaseContext(), "Add click loaded Failed", Toast.LENGTH_SHORT).show();
                Log.e("RestaurantList","Error in loading add"+errorCode);
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                //Toast.makeText(getBaseContext(), "Add Click Opened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                //Toast.makeText(getBaseContext(), "Add clicked when user left the app", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                //Toast.makeText(getBaseContext(), "Code to be executed when when the user is about to return\n" +
                  //      "                // to the app after tapping on an ad.", Toast.LENGTH_SHORT).show();
            }
        });

        tvTitle = findViewById(R.id.tv_title);
        change = findViewById(R.id.change);
        navigationnn = findViewById(R.id.navigationnn);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerll = findViewById(R.id.drawerll);
        nav_menu = findViewById(R.id.nav_menu);
        nav_cart = findViewById(R.id.nav_cart);
        nav_orders = findViewById(R.id.nav_orders);
        nav_log_out = findViewById(R.id.nav_log_out);
        nav_update_name = findViewById(R.id.nav_update_name);
        nav_nearby_store = findViewById(R.id.nav_nearby_store);
        nav_home_address = findViewById(R.id.nav_home_address);
        nav_settings = findViewById(R.id.nav_settings);
        nav_fav = findViewById(R.id.nav_fav);

//        imageView = findViewById(R.id.cart);


        navigationnn.setOnClickListener(this);
        nav_menu.setOnClickListener(this);
        nav_cart.setOnClickListener(this);
        nav_orders.setOnClickListener(this);
        nav_log_out.setOnClickListener(this);
        nav_update_name.setOnClickListener(this);
        nav_nearby_store.setOnClickListener(this);
        nav_home_address.setOnClickListener(this);
        nav_settings.setOnClickListener(this);
        nav_fav.setOnClickListener(this);



        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RestaurantList.this, com.foodcubo.foodcubo.foodcubo.SearchLocationActivity.class);
                startActivityForResult(i, 400);
            }
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Common.isConnectedToInternet(getBaseContext()))
            getUserLocation();
        else {
            Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        database = FirebaseDatabase.getInstance();

        restaurantList = database.getReference("Restaurants");
        ratingTbl = database.getReference("Rating");


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    database.getReference("User").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(Common.currentUser!=null){
                            if(dataSnapshot.child(Common.currentUser.getPhone()).exists()){
                                Common.currentUser.setFirstOrderApplied(
                                        dataSnapshot.child(Common.currentUser.getPhone())
                                        .child("firstOrderApplied").getValue(String.class));

                                if(Common.currentUser.getFirstOrderApplied().equals("false"))
                                    offer_layout.setVisibility(View.VISIBLE);
                                else
                                    offer_layout.setVisibility(View.GONE);

                            }
                            }else{
                                Common.reopenApp(RestaurantList.this);
                            }



                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    loadRestaurant();
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
      /*  //Default load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadRestaurant();
                else {
                    Toast.makeText(RestaurantList.this, "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });*/
        //Load menu
        recyclerView = findViewById(R.id.recycler_restaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //fab = findViewById(R.id.btnCart);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(RestaurantList.this, com.foodcubo.foodcubo.foodcubo.Cart.class);
                startActivity(cartIntent);

            }
        });
        if(Common.currentUser!=null){
            fab.setCount(new com.foodcubo.foodcubo.foodcubo.Database.Database(RestaurantList.this).getCountCart(Common.currentUser.getPhone()));

        }else{
            Common.reopenApp(RestaurantList.this);
        }*/

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
       /* if(Common.currentUser!=null){
            fab.setCount(new com.foodcubo.foodcubo.foodcubo.Database.Database(RestaurantList.this).getCountCart(Common.currentUser.getPhone()));
        }else{
            Common.reopenApp(RestaurantList.this);
        }*/

    }

    private void loadRestaurant()
    {
        restaurantList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    restaurantsSorted.removeAll(restaurantsSorted);
                    restaurantsSorted.clear();

                    restaurantsDistances.removeAll(restaurantsDistances);
                    restaurantsDistances.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RestaurantLocation restaurantLocation = snapshot
                                .child("RestaurantLocation")
                                .getValue(RestaurantLocation.class);

                        double resLatString = Double.parseDouble(restaurantLocation.getLatitude());
                        double resLongstring = Double.parseDouble(restaurantLocation.getLongitude());


                        float[] results = new float[1];
                        Location.distanceBetween(latString, longstring,
                                resLatString, resLongstring, results);

                        float distanceInMeters = results[0];
                        restaurantsDistances.add(distanceInMeters);
                        //restaurantsDistances.get(0);
                        //for(int i = 0;i<restaurantsDistances.size();i++){
                            if(distanceInMeters< 5000.00){
                                RestaurantNear f = new RestaurantNear("",
                                        snapshot.getKey(),
                                        snapshot.child("name").getValue(String.class),
                                        snapshot.child("image").getValue(String.class),
                                        snapshot.child("phoneNo").getValue(String.class)
                                );
                                restaurantsSorted.add(f);
                            }
                        //}


                    }
                    /*for (int i = 0; i < restaurantsDistances.size(); i++) {

                        for (int j = restaurantsDistances.size() - 1; j > i; j--) {
                            if (restaurantsDistances.get(i) > restaurantsDistances.get(j)) {

                                RestaurantNear tmp1 = restaurantsSorted.get(i);
                                restaurantsSorted.set(i, restaurantsSorted.get(j));
                                restaurantsSorted.set(j, tmp1);

                                float tmp = restaurantsDistances.get(i);
                                restaurantsDistances.set(i, restaurantsDistances.get(j));
                                restaurantsDistances.set(j, tmp);

                            }

                        }

                    }*/
                    adapter = new RestaurantListAdapter(RestaurantList.this, restaurantsSorted);
                    //set adapter
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        if(Common.currentUser!=null){
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                tempRestaurantId,
                String.valueOf(i),
                s);

        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RestaurantList.this, "Thank you for submit rating !!!", Toast.LENGTH_SHORT).show();
                    }
                });
        }else{
            Common.reopenApp(RestaurantList.this);
        }


        }

    @Override
    public void onClick(View v) {
        if (v == navigationnn)
            mDrawerLayout.openDrawer(drawerll);
        else if (v == nav_cart) {

            Intent cartIntent = new Intent(RestaurantList.this, com.foodcubo.foodcubo.foodcubo.Cart.class);
            startActivity(cartIntent);

        } else if (v == nav_orders) {
            Intent orderIntent = new Intent(RestaurantList.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (v == nav_log_out) {
            AccountKit.logOut();
            Intent signIn = new Intent(RestaurantList.this, ScreenOneActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signIn);
        } else if (v == nav_update_name) {
            showChangePasswordDialog();

        } else if (v == nav_nearby_store) {
            startActivity(new Intent(RestaurantList.this, com.foodcubo.foodcubo.foodcubo.NearbyStores.class));
        } else if (v == nav_home_address) {
            showHomeAddressDialog();

        } else if (v == nav_settings) {
            showHomeSettingDialog();
        } else if (v == nav_fav) {
            startActivity(new Intent(RestaurantList.this, FavoritesActivity.class));
        }
        if (v != navigationnn)
            mDrawerLayout.closeDrawer(drawerll);
    }

    private void showHomeSettingDialog() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(RestaurantList.this);
        String msgTitle = getString(R.string.nav_settings_header);
        alertDialog.setTitle(msgTitle);

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View layout_setting = inflater.inflate(R.layout.setting_layout, null);

        final CheckBox ckb_subscribe_news = layout_setting.findViewById(R.id.ckb_sub_new);
        Paper.init(this);
        System.out.println("koko...."+Paper.book());
        try {
            boolean isSubscribe = Paper.book().read("sub_new");

            if (!isSubscribe)
                ckb_subscribe_news.setChecked(false);
            else
                ckb_subscribe_news.setChecked(true);
        }catch (Exception e){
            ckb_subscribe_news.setChecked(false);
        }
        alertDialog.setView(layout_setting);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                if (ckb_subscribe_news.isChecked()) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "subscribed";
                            if (!task.isSuccessful()) {
                                msg = "subscribe_failed";
                            }
                            Log.d("subscribed", msg);
                            Toast.makeText(RestaurantList.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Paper.book().write("sub_new", true);
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);

                    Paper.book().write("sub_new", false);
                }

            }
        });
        alertDialog.show();


    }

    private void showHomeAddressDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(RestaurantList.this);
        String msgTitle = getString(R.string.nav_change_home_address);
        String msgTitleString = getString(R.string.nav_change_home_address_msg);
        alertDialog.setTitle(msgTitle);
        alertDialog.setMessage(msgTitleString);

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View layout_home = inflater.inflate(R.layout.home_address_layout, null);

        final MaterialEditText edtHomeAddress = layout_home.findViewById(R.id.edtHomeAddress);

        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(Common.currentUser!=null){
                dialogInterface.dismiss();
                Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());
                FirebaseDatabase.getInstance().getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RestaurantList.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                }else{
                    Common.reopenApp(RestaurantList.this);
                }





                }
        });
        alertDialog.show();
    }

    private void showChangePasswordDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(RestaurantList.this);
        String msgTitle = getString(R.string.nav_update_name);
        String msgTitleString = getString(R.string.nav_update_name_msg);
        alertDialog.setTitle(msgTitle);
        alertDialog.setMessage(msgTitleString);

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View layout_pwd = inflater.inflate(R.layout.update_name_layout, null);

        final MaterialEditText edtName = layout_pwd.findViewById(R.id.edtName);

        alertDialog.setView(layout_pwd);

        //button
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(Common.currentUser!=null){
                final SpotsDialog waitingDialog = new SpotsDialog(RestaurantList.this);
                waitingDialog.show();

                Map<String, Object> update_name = new HashMap<>();
                update_name.put("name", edtName.getText().toString());
                FirebaseDatabase.getInstance()
                        .getReference("User")
                        .child(Common.currentUser.getPhone())
                        .updateChildren(update_name)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if (task.isSuccessful())
                                    Toast.makeText(RestaurantList.this, "Name was updated", Toast.LENGTH_SHORT).show();
                            }
                        });

                }else{
                    Common.reopenApp(RestaurantList.this);
                }


            }


        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        alertDialog.show();
    }

    @Override
    public void onRestaurantListClick(String key) {
        tempRestaurantId = key;
        showRatingDialog();
    }



/*public void cart(View view){
        Intent cartIntent = new Intent(RestaurantList.this, com.foodcubo.foodcubo.foodcubo.Cart.class);
        startActivity(cartIntent);
    }*/
         /*if(Common.currentUser!=null){
            setCount(new com.foodcubo.foodcubo.foodcubo.Database.Database(this).getCountCart(Common.currentUser.getPhone()));

        }else{
            Common.reopenApp(RestaurantDetailsWithFoodLists.this);
        }*/


    /*public void change(View view){
        Intent cartIntent = new Intent(RestaurantList.this, SearchActivity.class);
        startActivity(cartIntent);
    }*/

}