package com.foodcubo.foodcubo.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.ViewHolder.FoodViewHolder;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Model.Food;
import com.foodcubo.foodcubo.foodcubo.Model.Order;
import com.foodcubo.foodcubo.foodcubo.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

@SuppressLint("Registered")
public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    FirebaseDatabase database;
    DatabaseReference foods;
    Food currentFood;


    Button btnShowComment;

    RatingBar ratingBar;
    DatabaseReference ratingTbl;

    String foodId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("details")
                .child("Food");

        ratingTbl = database.getReference("Rating");

        //Init View
        numberButton = findViewById(R.id.number_button);
        btnCart =  findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);
        btnShowComment=findViewById(R.id.btnShowComment);
        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FoodDetail.this, com.foodcubo.foodcubo.foodcubo.Showcomment.class);
                intent.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(intent);
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getFullprice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                ));

                Toast.makeText(FoodDetail.this,"Added to Cart",Toast.LENGTH_SHORT).show();


            }
        });
        if(Common.currentUser!=null){
        btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        }else{
            Common.reopenApp(FoodDetail.this);
        }


        food_description = findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_image = findViewById(R.id.img_food);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if(!foodId.isEmpty()){
            if(Common.isConnectedToInternet(getBaseContext())) {
                getDetailFood(foodId);
                getRatingFood(foodId);
            }
        }
        else{
            Toast.makeText(FoodDetail.this,"Please check your connection!!!!",Toast.LENGTH_SHORT).show();
        }

    }

    private void getRatingFood(String foodId) {
        Query foodRating=ratingTbl.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Rating item=postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if(count!=0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                currentFood = dataSnapshot.getValue(Food.class);

                //Set Image

               Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                /*changed by sonal back to old version
                Picasso.get().load(currentFood.getImage()).into(food_image);*/

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getFullprice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });

    }

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
                .create(FoodDetail.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int i, @NonNull String s) {
        //Get Rating and upload to firebase
        if(Common.currentUser!=null){
        final Rating rating=new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(i),
                s);

        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   Toast.makeText(FoodDetail.this,"Thank you for submit rating !!!",Toast.LENGTH_SHORT).show();
                    }
                });

        }else{
        Common.reopenApp(FoodDetail.this);
    }




        /*
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists()){
                    //Removing old value
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    //Update new value
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else {
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this,"Thank you for submit rating !!!!",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        */
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
