package com.foodcubo.foodcubo.foodcubo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;

/**
 * Created by 123456 on 2017/11/17.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtMenuName;
        public ImageView imageView;

        private ItemClickListener itemClickListener;

        public MenuViewHolder(View itemView){
            super(itemView);

            txtMenuName = itemView.findViewById(R.id.menu_name);
            imageView = itemView.findViewById(R.id.menu_image);

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
