package com.foodcubo.foodcubo.foodcubo.ViewHolder;

import android.app.MediaRouteButton;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.R;

import java.text.BreakIterator;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

   // public MediaRouteButton cart_item_type;
    public TextView txt_price;
    public ElegantNumberButton btn_quantity;
    public ImageView cart_image,image_cart_isveg;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;
   // public BreakIterator txt_cart_name;
   public TextView txt_cart_name,cart_item_type;

    public CartViewHolder(View itemView){
        super(itemView);
        cart_item_type = itemView.findViewById(R.id.cart_item_type);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_Price);
        btn_quantity = itemView.findViewById(R.id.btn_quantity);
        cart_image = itemView.findViewById(R.id.cart_image);
        image_cart_isveg = itemView.findViewById(R.id.image_cart_isveg);
        view_background=itemView.findViewById(R.id.view_background);
        view_foreground=itemView.findViewById(R.id.view_foreground);
        itemView.setOnCreateContextMenuListener(this);


    }

    @Override
    public void onClick(View view){

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");

        menu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}