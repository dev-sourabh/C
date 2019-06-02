package com.foodcubo.foodcubo.android.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;


/**
 * Created by 123456 on 2017/11/17.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView food_name,categoryName,full_food_price,btn_full_quick_add;
    public ImageView imgVegtype;

    public ElegantNumberButton btn_full_quick_cart;


    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(View itemView) {
        super(itemView);

        food_name = itemView.findViewById(R.id.list_food_name);
        categoryName=itemView.findViewById(R.id.list_menu_name);
        full_food_price=itemView.findViewById(R.id.list_full_food_price);
        //btn_add_food = itemView.findViewById(R.id.list_btn_add_food);
        btn_full_quick_add = itemView.findViewById(R.id.btn_full_quick_add);
        btn_full_quick_cart = itemView.findViewById(R.id.btn_full_quick_cart);
        imgVegtype = itemView.findViewById(R.id.img_veg_type);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

}
