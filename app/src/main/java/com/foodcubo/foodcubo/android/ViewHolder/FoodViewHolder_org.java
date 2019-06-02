package com.foodcubo.foodcubo.android.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;


/**
 * Created by 123456 on 2017/11/17.
 */

public class FoodViewHolder_org extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView btn_full_quick_add,btn_half_quick_add;
    public TextView food_name;
    public ImageView food_image,fav_image,isveg_image;
    public TextView full_food_price,half_food_price;
    public ElegantNumberButton btn_full_quick_cart,btn_half_quick_cart;
    public RelativeLayout fullprice_layout,halfprice_layout;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder_org(View itemView) {
        super(itemView);

        btn_full_quick_add = itemView.findViewById(R.id.btn_full_quick_add);
        btn_half_quick_add = itemView.findViewById(R.id.btn_half_quick_add);
        food_name = itemView.findViewById(R.id.food_name);
        food_image = itemView.findViewById(R.id.food_image);
        isveg_image = itemView.findViewById(R.id.isveg_image);
        full_food_price=itemView.findViewById(R.id.full_food_price);
        half_food_price=itemView.findViewById(R.id.half_food_price);

        fullprice_layout=itemView.findViewById(R.id.fullprice_layout);
        halfprice_layout=itemView.findViewById(R.id.halfprice_layout);

        btn_full_quick_cart=itemView.findViewById(R.id.btn_full_quick_cart);
        btn_half_quick_cart=itemView.findViewById(R.id.btn_half_quick_cart);

        fav_image=itemView.findViewById(R.id.fav_image);
        isveg_image=itemView.findViewById(R.id.isveg_image);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

}
