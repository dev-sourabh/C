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
import android.widget.EditText;
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

public class ChangeUserDetailsActivity extends AppCompatActivity {

  EditText edt_user_name,edt_phone_number;
  TextView tv_update_user_details;
  String finalUserName = "";
  String finalPhoneNumber = "";
  ImageButton imgBackToMainActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_details);

        tv_update_user_details = findViewById(R.id.tv_updateuserdetails);
        edt_user_name = findViewById(R.id.edt_user_name);
        edt_phone_number = findViewById(R.id.edt_user_phone_number);
        imgBackToMainActivity = findViewById(R.id.img_backToMain);




        if (Common.currentUser != null) {
            if (Common.currentUser.getName() == null || TextUtils.isEmpty(Common.currentUser.getName()) ||
                    Common.currentUser.getPhone().equals("") || Common.currentUser.getPhone().trim().equals("")) {
                edt_user_name.setText(" ");
                edt_phone_number.setText(" ");
            } else {
                edt_user_name.setText(Common.currentUser.getName());
                edt_phone_number.setText(Common.currentUser.getPhone());

            }
        } else {
            Common.reopenApp(ChangeUserDetailsActivity.this);
        }
        tv_update_user_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalPhoneNumber = edt_phone_number.getText().toString();
                finalUserName = edt_user_name.getText().toString();
                if (finalUserName == null || finalUserName.equals("") || TextUtils.isEmpty(finalUserName )
                        || finalPhoneNumber == null || finalPhoneNumber.equals("") || TextUtils.isEmpty(finalPhoneNumber)) {
                    Toast.makeText(getApplicationContext(), "Name and Phone Number cannot be blank,please enter for us to serve you better !!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if(Common.currentUser!=null){
                                    Common.currentUser.setName(edt_user_name.getText().toString());
                                    Common.currentUser.setSecondaryPhoneNumber(edt_phone_number.getText().toString());

                                    FirebaseDatabase.getInstance().getReference("User")
                                            .child(Common.currentUser.getPhone())
                                            .setValue(Common.currentUser)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(ChangeUserDetailsActivity.this, "User Details updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    Intent data = new Intent();
                                    data.putExtra("user_name", finalUserName);
                                    data.putExtra("user_phone_number", finalPhoneNumber);
                                    setResult(RESULT_OK, data);
                                    finish();

                                }


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
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
    //    setResult(com.foodcubo.foodcubo.foodcubo.SearchLocationActivity.RESULT_CANCELED);
        finish();
    }



}
