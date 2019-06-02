package com.foodcubo.foodcubo.android.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.android.ViewHolder.RestaurantViewHolder;
import com.foodcubo.foodcubo.foodcubo.RestaurantDetailsWithFoodLists;
//import com.foodcubo.foodcubo.foodcubo.RestaurantFoodDetails;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;
import com.foodcubo.foodcubo.foodcubo.Model.RestaurantNear;
import com.foodcubo.foodcubo.foodcubo.RestaurantFoodDetailLists;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantViewHolder> implements Filterable {

    private final AppCompatActivity mContext;
    private final ArrayList<RestaurantNear> restaurantlist;
    private ArrayList<RestaurantNear> mDataset;
    private MyFilter filter;

    public RestaurantListAdapter(AppCompatActivity context, ArrayList<RestaurantNear> restaurantlist){
        mDataset = restaurantlist;
        this.mContext= context;
        this.restaurantlist=restaurantlist;
        this.mListener = (RestaurantListClickInterface)mContext;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                //.inflate(R.layout.restaurant_item,parent,false);
                .inflate(R.layout.layout_restuarant_list,parent,false);
        return new RestaurantViewHolder(itemView);
    }

    private RestaurantListClickInterface mListener;
    public interface RestaurantListClickInterface{
        void onRestaurantListClick(String key);
    }

    @Override
    public void onBindViewHolder(@NonNull final RestaurantViewHolder holder, final int position) {
        final RestaurantNear model=mDataset.get(holder.getAdapterPosition());

        holder.txtRestaurantName.setText(model.getName());
        Picasso.with(mContext).load(model.getImage()).into(holder.restaurantImage);
       holder.imgRestaurantRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRestaurantListClick(model.getKey());

            }
        });
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
               // Intent foodList = new Intent(mContext, RestaurantDetailsWithFoodLists.class);
                String userAddress = Common.currentUser.getHomeAddress();
               Intent foodList = new Intent(mContext, RestaurantFoodDetailLists.class);
                foodList.putExtra("phoneNo",model.getPhoneNo());
                foodList.putExtra("restaurantName", model.getName());
                foodList.putExtra("userAddress", userAddress);
                Common.restaurantSelected = model.getKey();
                Common.restaurantSelectedName = model.getName();
                Common.restaurantCartPhone = model.getPhoneNo();

                mContext.startActivity(foodList);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MyFilter(RestaurantListAdapter.this, restaurantlist);
        }
        return filter;
    }

    public class MyFilter extends Filter {

        private final RestaurantListAdapter adapter;
        private final List<RestaurantNear> originalList;
        private final ArrayList<RestaurantNear> filteredList;

        MyFilter(RestaurantListAdapter adapter, ArrayList<RestaurantNear> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                for (RestaurantNear ignored : originalList) {
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.mDataset.clear();
            adapter.mDataset.addAll((ArrayList<RestaurantNear>) filterResults.values);
            adapter.notifyDataSetChanged();
        }
    }

}
