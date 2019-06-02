package com.foodcubo.foodcubo.foodcubo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodcubo.foodcubo.android.Adapter.RestaurantListAdapter;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Model.Rating;
import com.foodcubo.foodcubo.foodcubo.Model.RestaurantLocation;
import com.foodcubo.foodcubo.foodcubo.Model.RestaurantNear;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NearbyStores extends AppCompatActivity implements RestaurantListAdapter.RestaurantListClickInterface,RatingDialogListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    public String tempRestaurantId;
    DatabaseReference ratingTbl;
    FirebaseDatabase database;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int MY_PERMISION_CODE = 10;
    private boolean Permission_is_granted = false;
    ProgressBar progress;
    private GoogleApiClient mGoogleApiClient;

    private double latString,longstring;
    DatabaseReference restaurantList;


    private ArrayList<Float> restaurantsDistances = new ArrayList<>();
    private ArrayList<RestaurantNear> restaurantsNearBy = new ArrayList<>();
    private RestaurantListAdapter adapter;

    public void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Restaurant")
                .setDescription("Please Select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(NearbyStores.this)
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_stores);

        database = FirebaseDatabase.getInstance();
        progress =  findViewById(R.id.progress);

        restaurantList = database.getReference("Restaurants");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progress.setProgress(0,true);
        }
        else progress.setProgress(0);

        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);



        ratingTbl = database.getReference("Rating");


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    getUserLocation();
                else{
                    Toast.makeText(getBaseContext(),"Please check your connection!!!!",Toast.LENGTH_SHORT).show();
                }

            }
        });


        recyclerView = findViewById(R.id.recycler_restaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Common.isConnectedToInternet(NearbyStores.this))
            getUserLocation();
        else
            Toast.makeText(NearbyStores.this,"Please check your connection!!!!",Toast.LENGTH_SHORT).show();




        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();



    }

    private void getUserLocation()
    {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_COARSE_LOCATION) )
                {
                    showAlert();

                }
                else
                {

                    if(isFirstTimeAskingPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        firstTimeAskingPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        ActivityCompat.requestPermissions(this,
                                new String[]{ACCESS_COARSE_LOCATION,ACCESS_FINE_LOCATION},
                                MY_PERMISION_CODE);
                    } else {

                        Toast.makeText(this,"You won't be able to access the features of this App",Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.GONE);
                        //Permission disable by device policy or user denied permanently. Show proper error message
                    }



                }


            }
            else Permission_is_granted = true;
        }
        else
        {

            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {

                        latString = location.getLatitude() ;
                        longstring = location.getLongitude();

                        restaurantList.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                restaurantsNearBy.removeAll(restaurantsNearBy);
                                restaurantsNearBy.clear();

                                restaurantsDistances.removeAll(restaurantsDistances);
                                restaurantsDistances.clear();

                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    RestaurantLocation restaurantLocation = snapshot
                                            .child("RestaurantLocation")
                                            .getValue(RestaurantLocation.class);

                                    double resLatString = Double.parseDouble(restaurantLocation.getLatitude());
                                    double resLongstring = Double.parseDouble(restaurantLocation.getLongitude());


                                    float[] results = new float[1];
                                    Location.distanceBetween(latString, longstring,
                                            resLatString, resLongstring, results);
                                    float distanceInMeters = results[0];
                                    if(distanceInMeters < 5000){
                                        restaurantsDistances.add(distanceInMeters);


                                            RestaurantNear f = new RestaurantNear("",
                                                    snapshot.getKey(),
                                                    snapshot.child("name").getValue(String.class),
                                                    snapshot.child("image").getValue(String.class),
                                                    snapshot.child("phoneNo").getValue(String.class));
                                            restaurantsNearBy.add(f);


                                    }
                                }
                                for (int i = 0; i < restaurantsDistances.size(); i++) {

                                    for (int j = restaurantsDistances.size() - 1; j > i; j--) {
                                        if (restaurantsDistances.get(i) > restaurantsDistances.get(j)) {

                                            RestaurantNear tmp1 = restaurantsNearBy.get(i);
                                            restaurantsNearBy.set(i,restaurantsNearBy.get(j)) ;
                                            restaurantsNearBy.set(j,tmp1);

                                            float tmp = restaurantsDistances.get(i);
                                            restaurantsDistances.set(i,restaurantsDistances.get(j)) ;
                                            restaurantsDistances.set(j,tmp);

                                        }

                                    }

                                }
                                adapter = new RestaurantListAdapter(NearbyStores.this, restaurantsNearBy);
                                recyclerView.setAdapter(adapter);
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Error in fetching the location",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public static final String  PREFS_FILE_NAME = "sharedPreferences";
    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime){
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission){
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings are OFF \nPlease Enable Location")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                        ActivityCompat.requestPermissions(NearbyStores.this,
                                new String[]{ACCESS_COARSE_LOCATION,ACCESS_FINE_LOCATION},
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
        final Rating rating=new Rating(Common.currentUser.getPhone(),
                tempRestaurantId,
                String.valueOf(i),
                s);

        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(NearbyStores.this,"Thank you for submit rating !!!",Toast.LENGTH_SHORT).show();
                    }
                });
        }else{
            Common.reopenApp(NearbyStores.this);
        }


    }

    @Override
    public void onConnected(Bundle bundle) {

        if(Permission_is_granted)
            getUserLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (Permission_is_granted && mGoogleApiClient.isConnected()) getUserLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRestaurantListClick(String key) {
        tempRestaurantId = key;
        showRatingDialog();
    }

}
