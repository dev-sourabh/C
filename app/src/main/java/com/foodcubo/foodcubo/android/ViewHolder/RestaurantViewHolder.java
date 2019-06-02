package com.foodcubo.foodcubo.android.ViewHolder;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtRestaurantName;
    public ImageView restaurantImage;
    public ImageView imgRestaurantRating;
   // public FloatingActionButton btnRestaurantRating;

    private ItemClickListener itemClickListener;

    public RestaurantViewHolder(View itemView){
        super(itemView);

        txtRestaurantName= itemView.findViewById(R.id.restaurant_name);
        restaurantImage= itemView.findViewById(R.id.restaurant_image);
       // btnRestaurantRating = itemView.findViewById(R.id.btn_restaurant_rating);
        imgRestaurantRating = itemView.findViewById(R.id.img_restaurant_rating);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
