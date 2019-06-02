package com.foodcubo.foodcubo.android;

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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foodcubo.foodcubo.android.Common.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ChangeAddressActivity extends AppCompatActivity {

    //parent layout
    LinearLayout layout_currentlocation, layout_manuallocation, layout_homelocation;
    //selection image
    ImageView currentlocation_selection, manuallocation_selection, homelocation_selection;
    //click to change images
    TextView get_current_location, add_homeaddress;
    ImageView add_manuallocation;
    //middle views
    //address scrolls
    HorizontalScrollView scroll_currentlocation, scroll_manuallocation, scroll_homelocation;
    //address textview
    TextView tv_current_location, tv_manual_location, tv_home_location;
    //add address textview
    TextView tv_addaddress;
    //currentlocation_loading
    ProgressBar currentlocation_loading;
    // present value
    String finalAddress = "";
    ImageButton imgBackToMainActivity;

    private static final int MY_PERMISION_CODE = 10;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static final String PREFS_FILE_NAME = "sharedPreferences";
    private double latString, longstring;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        currentlocation_loading = findViewById(R.id.currentlocation_loading);
        layout_currentlocation = findViewById(R.id.layout_currentlocation);
        layout_manuallocation = findViewById(R.id.layout_manuallocation);
        layout_homelocation = findViewById(R.id.layout_homelocation);
        currentlocation_selection = findViewById(R.id.currentlocation_selection);
        manuallocation_selection = findViewById(R.id.manuallocation_selection);
        homelocation_selection = findViewById(R.id.homelocation_selection);
        get_current_location = findViewById(R.id.get_current_location);
        add_homeaddress = findViewById(R.id.add_homeaddress);
        add_manuallocation = findViewById(R.id.add_manuallocation);
        scroll_currentlocation = findViewById(R.id.scroll_currentlocation);
        scroll_manuallocation = findViewById(R.id.scroll_manuallocation);
        scroll_homelocation = findViewById(R.id.scroll_homelocation);
        tv_current_location = findViewById(R.id.tv_current_location);
        tv_manual_location = findViewById(R.id.tv_manual_location);
        tv_home_location = findViewById(R.id.tv_home_location);
        tv_addaddress = findViewById(R.id.tv_addaddress);
        imgBackToMainActivity = findViewById(R.id.img_backToMain);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Common.isConnectedToInternet(getBaseContext()))
            getUserLocation();
        else {
            Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Common.currentUser != null) {
            if (Common.currentUser.getHomeAddress() == null || TextUtils.isEmpty(Common.currentUser.getHomeAddress()) ||
                    Common.currentUser.getHomeAddress().equals("") || Common.currentUser.getHomeAddress().trim().equals("")) {
                scroll_homelocation.setVisibility(View.GONE);
                add_homeaddress.setText("ADD ADDRESS");
            } else {
                scroll_homelocation.setVisibility(View.VISIBLE);
                add_homeaddress.setText("CHANGE ADDRESS");
                tv_home_location.setText(Common.currentUser.getHomeAddress());
            }
        } else {
            Common.reopenApp(ChangeAddressActivity.this);
        }


        add_homeaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHomeAddressDialog();
            }
        });
        add_manuallocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChangeAddressActivity.this, com.foodcubo.foodcubo.foodcubo.SearchLocationActivity.class);
                i.putExtra("add_address", true);
                startActivityForResult(i, 400);
            }
        });
        get_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserLocation();
            }
        });

        currentlocation_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAddress = tv_current_location.getText().toString();
                if (tempAddress == null || tempAddress.equals("") || TextUtils.isEmpty(tempAddress)) {
                    getUserLocation();
                } else {
                    currentlocation_selection.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    manuallocation_selection.setImageResource(R.drawable.ic_circle);
                    homelocation_selection.setImageResource(R.drawable.ic_circle);
                    finalAddress = tempAddress;
                }
            }
        });
        manuallocation_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAddress = tv_manual_location.getText().toString();
                if (tempAddress == null || tempAddress.equals("") || TextUtils.isEmpty(tempAddress)) {
                    Intent i = new Intent(ChangeAddressActivity.this, com.foodcubo.foodcubo.foodcubo.SearchLocationActivity.class);
                    i.putExtra("add_address", true);
                    startActivityForResult(i, 400);
                } else {
                    manuallocation_selection.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    currentlocation_selection.setImageResource(R.drawable.ic_circle);
                    homelocation_selection.setImageResource(R.drawable.ic_circle);
                    finalAddress = tempAddress;
                }
            }
        });
        homelocation_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAddress = tv_home_location.getText().toString();
                if (tempAddress == null || tempAddress.equals("") || TextUtils.isEmpty(tempAddress)) {
                    showHomeAddressDialog();
                } else {
                    homelocation_selection.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    currentlocation_selection.setImageResource(R.drawable.ic_circle);
                    manuallocation_selection.setImageResource(R.drawable.ic_circle);
                    finalAddress = tempAddress;
                }
            }
        });
        tv_addaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalAddress == null || finalAddress.equals("") || TextUtils.isEmpty(finalAddress)) {
                    Toast.makeText(getApplicationContext(), "Address Not Found!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent();
                    data.putExtra("address_value", finalAddress);
                    setResult(com.foodcubo.foodcubo.foodcubo.SearchLocationActivity.RESULT_OK, data);
                    finish();
                }
            }
        });
        imgBackToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(com.foodcubo.foodcubo.foodcubo.SearchLocationActivity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISION_CODE: {

                currentlocation_loading.setVisibility(View.VISIBLE);
                get_current_location.setVisibility(View.GONE);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            latString = location.getLatitude();
                            longstring = location.getLongitude();
                            tv_current_location.setText(getCompleteAddressString(latString, longstring, false));
                            currentlocation_loading.setVisibility(View.GONE);
                            get_current_location.setVisibility(View.VISIBLE);
                            scroll_currentlocation.setVisibility(View.VISIBLE);
                            currentlocation_selection.setImageResource(R.drawable.ic_check_circle_black_24dp);
                            homelocation_selection.setImageResource(R.drawable.ic_circle);
                            manuallocation_selection.setImageResource(R.drawable.ic_circle);
                            finalAddress = tv_current_location.getText().toString();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error in fetching the location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE, boolean searched) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null) {

                Address returnedAddress = addresses.get(0);

                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }


    private void showAlert() {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings are OFF \nPlease Enable Location")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                        ActivityCompat.requestPermissions(ChangeAddressActivity.this,
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


    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                currentlocation_loading.setVisibility(View.GONE);
                get_current_location.setVisibility(View.VISIBLE);

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


            currentlocation_loading.setVisibility(View.VISIBLE);
            get_current_location.setVisibility(View.GONE);

            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        latString = location.getLatitude();
                        longstring = location.getLongitude();
                        tv_current_location.setText(getCompleteAddressString(latString, longstring, false));
                        currentlocation_loading.setVisibility(View.GONE);
                        get_current_location.setVisibility(View.VISIBLE);
                        scroll_currentlocation.setVisibility(View.VISIBLE);
                        currentlocation_selection.setImageResource(R.drawable.ic_check_circle_black_24dp);
                        homelocation_selection.setImageResource(R.drawable.ic_circle);
                        manuallocation_selection.setImageResource(R.drawable.ic_circle);
                        finalAddress=tv_current_location.getText().toString();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error in fetching the location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 400) {
            if (resultCode == RESULT_OK) {

                tv_manual_location.setText(data.getStringExtra("address"));
                scroll_manuallocation.setVisibility(View.VISIBLE);
                manuallocation_selection.setImageResource(R.drawable.ic_check_circle_black_24dp);
                homelocation_selection.setImageResource(R.drawable.ic_circle);
                currentlocation_selection.setImageResource(R.drawable.ic_circle);
                finalAddress=tv_manual_location.getText().toString();


            }
        }
    }
    private void showHomeAddressDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog =
                new android.support.v7.app.AlertDialog.Builder(ChangeAddressActivity.this);
        alertDialog.setTitle("ADD/CHANGE HOME ADDRESS");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.home_address_layout, null);

        final MaterialEditText edtHomeAddress = layout_home.findViewById(R.id.edtHomeAddress);

        alertDialog.setView(layout_home);

        if(Common.currentUser!=null){
        if(Common.currentUser.getHomeAddress()==null || TextUtils.isEmpty(Common.currentUser.getHomeAddress()) ||
                Common.currentUser.getHomeAddress().equals("") || Common.currentUser.getHomeAddress().trim().equals("")) {
            alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());
                    tv_home_location.setText(Common.currentUser.getHomeAddress());
                    scroll_homelocation.setVisibility(View.VISIBLE);
                    add_homeaddress.setText("CHANGE ADDRESS");
                    homelocation_selection.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    manuallocation_selection.setImageResource(R.drawable.ic_circle);
                    currentlocation_selection.setImageResource(R.drawable.ic_circle);
                    finalAddress=tv_home_location.getText().toString();
                    FirebaseDatabase.getInstance().getReference("User")
                            .child(Common.currentUser.getPhone())
                            .setValue(Common.currentUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ChangeAddressActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }else{
            edtHomeAddress.setText(Common.currentUser.getHomeAddress());
            alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());
                    tv_home_location.setText(Common.currentUser.getHomeAddress());
                    scroll_homelocation.setVisibility(View.VISIBLE);
                    add_homeaddress.setText("CHANGE ADDRESS");
                    homelocation_selection.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    manuallocation_selection.setImageResource(R.drawable.ic_circle);
                    currentlocation_selection.setImageResource(R.drawable.ic_circle);
                    finalAddress=tv_home_location.getText().toString();
                    FirebaseDatabase.getInstance().getReference("User")
                            .child(Common.currentUser.getPhone())
                            .setValue(Common.currentUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ChangeAddressActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }

        }else{
            Common.reopenApp(ChangeAddressActivity.this);
        }




        alertDialog.show();
    }


}
