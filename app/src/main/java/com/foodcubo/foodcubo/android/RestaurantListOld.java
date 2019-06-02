package com.foodcubo.foodcubo.android;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.ViewHolder.RestaurantViewHolder;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;
import com.foodcubo.foodcubo.foodcubo.Model.Rating;
import com.foodcubo.foodcubo.foodcubo.Model.Restaurant;
import com.foodcubo.foodcubo.foodcubo.RestaurantDetailsWithFoodLists;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

//import com.foodcubo.foodcubo.foodcubo.Common.Common;

public class RestaurantListOld extends AppCompatActivity implements RatingDialogListener {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    String tempRestaurantId;
    DatabaseReference ratingTbl;
    FirebaseDatabase database;


    FirebaseRecyclerOptions<Restaurant> options=new FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(FirebaseDatabase.getInstance().getReference().child("Restaurants"),Restaurant.class)
            .build();

    FirebaseRecyclerAdapter<Restaurant,RestaurantViewHolder> adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(options) {
        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_item, parent, false);
            return new RestaurantViewHolder(itemView);
        }

        @Override
        protected void onBindViewHolder(@NonNull final RestaurantViewHolder holder, int position, @NonNull final Restaurant model) {
            holder.txtRestaurantName.setText(model.getName());
           Picasso.with(getBaseContext()).load(model.getImage()).into(holder.restaurantImage);
            //changed made by sonal back to old version Picasso.get().load(model.getImage()).into(holder.restaurantImage);
            final Restaurant clickItem = model;
           // holder.btnRestaurantRating.setOnClickListener(new View.OnClickListener() {
            holder.imgRestaurantRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempRestaurantId = adapter.getRef(holder.getAdapterPosition()).getKey();
                    showRatingDialog();
                }
            });
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    //Get CategoryId and send to new Activity
                    Intent foodList = new Intent(RestaurantListOld.this, RestaurantDetailsWithFoodLists.class);
                    foodList.putExtra("phoneNo",model.getPhoneNo());
                    //Because CategoryId is key, so we just get the key of this item
                    Common.restaurantSelected = adapter.getRef(position).getKey();
                    startActivity(foodList);
                }
            });
        }



    };



    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Food")
                .setDescription("Please Select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(RestaurantListOld.this)
                .show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list_old);

        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        database = FirebaseDatabase.getInstance();

        ratingTbl = database.getReference("Rating");


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadRestaurant();
                else{
                    Toast.makeText(getBaseContext(),"Please check your connection!!!!",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        //Default load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadRestaurant();
                else{
                    Toast.makeText(RestaurantListOld.this,"Please check your connection!!!!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        //Load menu
        recyclerView = findViewById(R.id.recycler_restaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

}

    private void loadRestaurant() {

        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);


        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        adapter.stopListening();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        final Rating rating=new Rating(Common.currentUser.getPhone(),
                tempRestaurantId,
                String.valueOf(i),
                s);

        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RestaurantListOld.this,"Thank you for submit rating !!!",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}