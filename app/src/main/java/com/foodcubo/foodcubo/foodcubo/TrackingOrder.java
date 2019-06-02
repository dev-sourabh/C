package com.foodcubo.foodcubo.foodcubo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.Model.Request;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Helper.DirectionJSONParser;
import com.foodcubo.foodcubo.foodcubo.Model.ShippingInformation;
import com.foodcubo.foodcubo.foodcubo.Remote.IGoogleService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.app.ProgressDialog;

public class TrackingOrder extends AppCompatActivity implements OnMapReadyCallback,ValueEventListener {

    private GoogleMap mMap;

    FirebaseDatabase database;
    DatabaseReference requests,shippingOrder;

    Request currentOrder;
    IGoogleService mService;
    Marker shippingMarker;
    TextView btn_call_shipper;
    ProgressBar mLoadingProgress;

    Polyline polyline;
    private String shipperNumber;
    private static final int CALL_PHONE_REQUEST_CODE=9999;

    @Override
    public void onBackPressed() {
        if(mLoadingProgress.getVisibility()==View.VISIBLE){
            mLoadingProgress.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);
        shipperNumber = getIntent().getStringExtra("phonenumber");
        mLoadingProgress = findViewById(R.id.loading_progress);
        btn_call_shipper = findViewById(R.id.btn_call_shipper);
        btn_call_shipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(TrackingOrder.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED
                        ){
                    ActivityCompat.requestPermissions(TrackingOrder.this,new String[]{
                            Manifest.permission.CALL_PHONE
                    },CALL_PHONE_REQUEST_CODE);

                }
                else {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + shipperNumber));
                    startActivityForResult(intent,0);
                }
            }
        });
        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        shippingOrder=database.getReference("ShippingOrders");

        mService=Common.getGoogleMapAPI();

        shippingOrder.addValueEventListener(this);



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case  CALL_PHONE_REQUEST_CODE:
            {
                if(grantResults.length >0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + shipperNumber));
                    startActivityForResult(intent,0);
                }
            }
        }
    }
    @Override
    protected void onStop() {
        shippingOrder.removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        trackingLocation();

    }

    private void trackingLocation() {
        requests.child(Common.currentKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentOrder = dataSnapshot.getValue(Request.class);
                        assert currentOrder != null;
                        String[] array = currentOrder.getLatLng().split(",");

                        LatLng location=new LatLng(Double.parseDouble(array[0]),
                                Double.parseDouble(array[1]));

                        System.out.println("kiki....22222"+location);
                        mMap.addMarker(new MarkerOptions().position(location)
                                .title("order destination")
                                .icon(BitmapDescriptorFactory.defaultMarker()));

                        shippingOrder.child(Common.currentKey);
                        shippingOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ShippingInformation shippingInformation = dataSnapshot.getValue(ShippingInformation.class);

                                assert shippingInformation != null;
                                LatLng shipperLocation = new LatLng(shippingInformation.getLat(), shippingInformation.getLng());
                                if (shippingMarker == null) {
                                    System.out.println("kiki...." + shippingInformation.getLat() + "...." + shippingInformation.getLng());
                                    shippingMarker = mMap.addMarker(
                                            new MarkerOptions()
                                                    .position(shipperLocation)
                                                    .title("Shipper #" + shippingInformation.getOrderId())
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                    );
                                } else {
                                    shippingMarker.setPosition(shipperLocation);
                                }

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(shipperLocation)
                                        .zoom(16)
                                        .bearing(0)
                                        .tilt(45)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                if (polyline != null)
                                    polyline.remove();
                                mService.getDirections(shipperLocation.latitude + "," + shipperLocation.longitude
                                        , currentOrder.getLatLng())
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                assert response.body() != null;
                                                new ParserTask().execute(response.body());
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                        /*if(currentOrder.getAddress() != null && !currentOrder.getAddress().isEmpty()){
                            mService.getLocationFromAddress(new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?address=")
                            .append(currentOrder.getAddress()).toString())
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            try {
                                                JSONObject jsonObject=new JSONObject(response.body());

                                                String lat=((JSONArray) jsonObject.get("results"))
                                                        .getJSONObject(0)
                                                        .getJSONObject("geometry")
                                                        .getJSONObject("location")
                                                        .get("lat").toString();
                                                String lng=((JSONArray) jsonObject.get("results"))
                                                        .getJSONObject(0)
                                                        .getJSONObject("geometry")
                                                        .getJSONObject("location")
                                                        .get("lng").toString();

                                                LatLng location=new LatLng(Double.parseDouble(lat),
                                                        Double.parseDouble(lng));

                                                mMap.addMarker(new MarkerOptions().position(location)
                                                .title("order destination")
                                                .icon(BitmapDescriptorFactory.defaultMarker()));

                                                shippingOrder.child(Common.currentKey)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                ShippingInformation shippingInformation=dataSnapshot.getValue(ShippingInformation.class);

                                                                LatLng shipperLocation=new LatLng(shippingInformation.getLat(),shippingInformation.getLng());
                                                                if(shippingMarker == null){
                                                                    shippingMarker=mMap.addMarker(
                                                                            new MarkerOptions()
                                                                            .position(shipperLocation)
                                                                            .title("Shipper #"+shippingInformation.getOrderId())
                                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                                    );
                                                                }
                                                                else {
                                                                    shippingMarker.setPosition(shipperLocation);
                                                                }

                                                                CameraPosition cameraPosition=new CameraPosition.Builder()
                                                                        .target(shipperLocation)
                                                                        .zoom(16)
                                                                        .bearing(0)
                                                                        .tilt(45)
                                                                        .build();
                                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                                                if(polyline != null)
                                                                    polyline.remove();
                                                                mService.getDirections(shipperLocation.latitude+","+shipperLocation.longitude
                                                                ,currentOrder.getAddress())
                                                                        .enqueue(new Callback<String>() {
                                                                            @Override
                                                                            public void onResponse(Call<String> call, Response<String> response) {
                                                                                new ParserTask().execute(response.body().toString());
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<String> call, Throwable t) {

                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                            catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });
                        }
                        else if(currentOrder.getLatLng() != null && !currentOrder.getLatLng().isEmpty()){
                            mService.getLocationFromAddress(new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?latlng=")
                                    .append(currentOrder.getLatLng()).toString())
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            try {
                                                JSONObject jsonObject=new JSONObject(response.body());

                                                String lat=((JSONArray) jsonObject.get("results"))
                                                        .getJSONObject(0)
                                                        .getJSONObject("geometry")
                                                        .getJSONObject("location")
                                                        .get("lat").toString();
                                                String lng=((JSONArray) jsonObject.get("results"))
                                                        .getJSONObject(0)
                                                        .getJSONObject("geometry")
                                                        .getJSONObject("location")
                                                        .get("lng").toString();

                                                LatLng location=new LatLng(Double.parseDouble(lat),
                                                        Double.parseDouble(lng));

                                                mMap.addMarker(new MarkerOptions().position(location)
                                                        .title("order destination")
                                                        .icon(BitmapDescriptorFactory.defaultMarker()));

                                                shippingOrder.child(Common.currentKey)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                ShippingInformation shippingInformation=dataSnapshot.getValue(ShippingInformation.class);

                                                                LatLng shipperLocation=new LatLng(shippingInformation.getLat(),shippingInformation.getLng());
                                                                if(shippingMarker == null){
                                                                    shippingMarker=mMap.addMarker(
                                                                            new MarkerOptions()
                                                                                    .position(shipperLocation)
                                                                                    .title("Shipper #"+shippingInformation.getOrderId())
                                                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                                    );
                                                                }
                                                                else {
                                                                    shippingMarker.setPosition(shipperLocation);
                                                                }

                                                                CameraPosition cameraPosition=new CameraPosition.Builder()
                                                                        .target(shipperLocation)
                                                                        .zoom(16)
                                                                        .bearing(0)
                                                                        .tilt(45)
                                                                        .build();
                                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                                                if(polyline != null)
                                                                    polyline.remove();
                                                                mService.getDirections(shipperLocation.latitude+","+shipperLocation.longitude
                                                                        ,currentOrder.getLatLng())
                                                                        .enqueue(new Callback<String>() {
                                                                            @Override
                                                                            public void onResponse(Call<String> call, Response<String> response) {
                                                                                new ParserTask().execute(response.body().toString());
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<String> call, Throwable t) {

                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                            catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });
                        }*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
        SpotsDialog mDialog= new SpotsDialog(TrackingOrder.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mDialog.setMessage("Please Wait.... ");
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String,String>>> routes=null;
            try{
                jObject=new JSONObject(strings[0]);
                DirectionJSONParser parser=new DirectionJSONParser();
                routes=parser.parse(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();
            ArrayList<LatLng> points;
            PolylineOptions lineOptions=null;
            for(int i=0;i<lists.size();i++){
                points=new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String,String>> path=lists.get(i);
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point=path.get(j);
                    double lat=Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng=Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position=new LatLng(lat,lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
            }
            if(lineOptions!=null)
           polyline= mMap.addPolyline(lineOptions);

        }
    }


    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        trackingLocation();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
