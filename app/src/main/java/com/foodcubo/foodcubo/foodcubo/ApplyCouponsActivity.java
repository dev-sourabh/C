package com.foodcubo.foodcubo.foodcubo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.ViewHolder.CouponsAdapter;

public class ApplyCouponsActivity extends AppCompatActivity {
    EditText apply_coupon_edit;
    TextView tv_apply_coupon,tv_available_coupons;
    RecyclerView listCoupons;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_coupons);

        apply_coupon_edit=findViewById(R.id.apply_coupon_edit);
        tv_apply_coupon=findViewById(R.id.tv_apply_coupon);
        tv_available_coupons=findViewById(R.id.tv_available_coupons);
        listCoupons=findViewById(R.id.listCoupons);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listCoupons.setLayoutManager(layoutManager);
        if(Common.currentUser!=null){
            if (Common.currentUser.getFirstOrderApplied().equals("false")) {
                tv_available_coupons.setVisibility(View.VISIBLE);
                CouponsAdapter couponsAdapter = new CouponsAdapter(ApplyCouponsActivity.this);
                listCoupons.setAdapter(couponsAdapter);
            } else {
                tv_available_coupons.setVisibility(View.GONE);
            }
        }else
            Common.reopenApp(ApplyCouponsActivity.this);

        apply_coupon_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String couponvalue = apply_coupon_edit.getText().toString();
                if(TextUtils.isEmpty(couponvalue) || couponvalue.equals("")){
                    tv_apply_coupon.setTextColor(Color.parseColor("#d9b182"));
                    tv_apply_coupon.setEnabled(false);
                }else{
                    tv_apply_coupon.setTextColor(Color.parseColor("#d99240"));
                    tv_apply_coupon.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tv_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("couponcode",apply_coupon_edit.getText().toString());
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }
}
