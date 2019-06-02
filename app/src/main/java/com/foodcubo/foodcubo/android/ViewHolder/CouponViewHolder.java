package com.foodcubo.foodcubo.android.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;


/**
 * Created by 123456 on 2017/11/17.
 */

public class CouponViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView coupon_code;
    public ImageView coupon_icon;
    public TextView apply_coupon,coupon_title,coupon_desc;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CouponViewHolder(View itemView) {
        super(itemView);

        coupon_desc =itemView.findViewById(R.id.coupon_desc);
        coupon_code =itemView.findViewById(R.id.coupon_code);
        coupon_icon = itemView.findViewById(R.id.coupon_icon);
        apply_coupon=itemView.findViewById(R.id.apply_coupon);
        coupon_title=itemView.findViewById(R.id.coupon_title);

    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

}
