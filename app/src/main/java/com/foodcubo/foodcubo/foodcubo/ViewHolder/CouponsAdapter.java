package com.foodcubo.foodcubo.foodcubo.ViewHolder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.android.ViewHolder.CouponViewHolder;
import com.foodcubo.foodcubo.foodcubo.ApplyCouponsActivity;

import static android.support.v7.app.AppCompatActivity.RESULT_OK;


public class CouponsAdapter extends RecyclerView.Adapter<CouponViewHolder>{

    /**
     *
     */
    private ApplyCouponsActivity applyCouponsContext;
    public CouponsAdapter(ApplyCouponsActivity applyCouponsContext){
       this.applyCouponsContext=applyCouponsContext;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(applyCouponsContext);
        View itemView = inflater.inflate(R.layout.coupon_item, parent, false);
        return new CouponViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CouponViewHolder holder, final int position) {
        holder.apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("couponcode","FIRSTCUBO60");
                applyCouponsContext.setResult(RESULT_OK,i);
                applyCouponsContext.finish();
            }
        });
    }

    @Override
    public int getItemCount() {

        return 1;
    }


}
