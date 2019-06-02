package com.foodcubo.foodcubo.foodcubo;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SearchLocationActivity extends AppCompatActivity implements
        com.foodcubo.foodcubo.foodcubo.PlaceAutocompleteAdapter.PlaceAutoCompleteInterface,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnClickListener {
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    RelativeLayout current_location_row;

    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    com.foodcubo.foodcubo.foodcubo.PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    EditText mSearchEdittext;
    ImageView mClear, mBack,mAddaddress;

    @Override
    public void onStart() {
        if (this.mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();

    }

    @Override
    public void onStop() {
        if (this.mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        mContext = SearchLocationActivity.this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
        initViews();
    }

    /*
   Initialize Views
    */
    private void initViews() {
        current_location_row = findViewById(R.id.current_location_row);
        mAddaddress = findViewById(R.id.addaddress);

        if(getIntent().getBooleanExtra("add_address",false)){
            mAddaddress.setVisibility(View.VISIBLE);
            mAddaddress.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent data = new Intent();
                    data.putExtra("address", mSearchEdittext.getText().toString());
                    setResult(SearchLocationActivity.RESULT_OK, data);
                    finish();
                }
            });
        }else{
            mAddaddress.setVisibility(View.GONE);
        }

        mRecyclerView = findViewById(R.id.list_search);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);

        mSearchEdittext = findViewById(R.id.search_et);
        mBack = findViewById(R.id.back);
        mClear = findViewById(R.id.clear);
        mClear.setOnClickListener(this);
        mBack.setOnClickListener(this);
        current_location_row.setOnClickListener(this);

        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch,
                mGoogleApiClient, BOUNDS_INDIA, null);
        mRecyclerView.setAdapter(mAdapter);

        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    mClear.setVisibility(View.VISIBLE);
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    mClear.setVisibility(View.GONE);
                }
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Log.e("", "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == mClear) {
            mSearchEdittext.setText("");
            if (mAdapter != null) {
                mAdapter.clearList();
            }

        } else if (v == mBack) {
            setResult(SearchLocationActivity.RESULT_CANCELED);
            finish();
        } else if (v == current_location_row) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (Common.isConnectedToInternet(getBaseContext()))
                getUserLocation();
            else {
                Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private static final int MY_PERMISION_CODE = 10;

    private void showAlert() {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings are OFF \nPlease Enable Location")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                        ActivityCompat.requestPermissions(SearchLocationActivity.this,
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

    public static final String PREFS_FILE_NAME = "sharedPreferences";

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_COARSE_LOCATION)) {
                    showAlert();

                } else {

                    if (isFirstTimeAskingPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        firstTimeAskingPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        ActivityCompat.requestPermissions(this,
                                new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION},
                                MY_PERMISION_CODE);
                    } else {
                        Toast.makeText(this, "You won't be able to access the features of this App", Toast.LENGTH_LONG).show();
                    }


                }


            }
        } else {

            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        Intent data = new Intent();
                        data.putExtra("lat", location.getLatitude());
                        data.putExtra("lng", location.getLongitude());
                        data.putExtra("address", getCompleteAddressString(location.getLatitude(), location.getLongitude()));
                        setResult(SearchLocationActivity.RESULT_OK, data);
                       // Toast.makeText(getApplicationContext(),location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error in fetching the location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                if(getIntent().getBooleanExtra("add_address",false)) {
                    strAdd = getFullLocationAddress(addresses);
                }else {
                    String address1 = addresses.get(0).getSubLocality() + " , " + addresses.get(0).getLocality();

                    if (!address1.equals("") && !TextUtils.isEmpty(address1) && !address1.equals("null")) {

                        if (addresses.get(0).getSubLocality().equals("") || TextUtils.isEmpty(addresses.get(0).getSubLocality()))
                            strAdd = addresses.get(1).getLocality();
                        else if (addresses.get(1).getSubLocality().equals("") || TextUtils.isEmpty(addresses.get(1).getSubLocality()))
                            strAdd = addresses.get(0).getLocality();
                        else
                            strAdd = addresses.get(0).getSubLocality() + " , " + addresses.get(0).getLocality();

                    } else {

                        String addresfs1 = addresses.get(0).getFeatureName();
                        if (!addresfs1.equals("") && !TextUtils.isEmpty(addresfs1) && !addresfs1.equals("null"))
                            strAdd = addresfs1;
                        else
                            strAdd = getFullLocationAddress(addresses);

                    }

                    if (strAdd.equals("") || TextUtils.isEmpty(strAdd) || strAdd.equals("null"))
                        strAdd = getFullLocationAddress(addresses);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceClick(final ArrayList<com.foodcubo.foodcubo.foodcubo.PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, final int position) {
        if (mResultList != null) {
            try {
                final String placeId = String.valueOf(mResultList.get(position).placeId);
                final String placeAddress = mResultList.get(position).fullDescription.toString();

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getCount() == 1) {

                            Intent data = new Intent();
                            data.putExtra("lat", places.get(0).getLatLng().latitude);
                            data.putExtra("lng", places.get(0).getLatLng().longitude);
                            data.putExtra("address", placeAddress);
                            setResult(SearchLocationActivity.RESULT_OK, data);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

 
