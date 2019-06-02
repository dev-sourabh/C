package com.foodcubo.foodcubo.foodcubo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.Model.AdminRequest;
import com.foodcubo.foodcubo.android.Model.Request;
import com.foodcubo.foodcubo.android.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentByCOD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_by_cod);

    }
}
