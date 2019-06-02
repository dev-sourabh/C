package com.foodcubo.foodcubo.foodcubo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;

public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView food_name;
    public ImageView food_image,fav_image;
    public TextView food_price;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FavoritesViewHolder(View itemView) {
        super(itemView);

        food_name = itemView.findViewById(R.id.food_name);
        food_image = itemView.findViewById(R.id.food_image);
        food_price=itemView.findViewById(R.id.food_price);
        fav_image=itemView.findViewById(R.id.fav_image);
        view_background=itemView.findViewById(R.id.view_background);
        view_foreground=itemView.findViewById(R.id.view_foreground);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

}
