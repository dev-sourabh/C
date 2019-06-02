package com.foodcubo.foodcubo.foodcubo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodcubo.foodcubo.android.Adapter.FoodListAdapter;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.FavoritesActivity;
import com.foodcubo.foodcubo.android.FoodListNew;
import com.foodcubo.foodcubo.android.Interface.DataTransferInterface;
import com.foodcubo.foodcubo.android.OrderStatus;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.android.ScreenOneActivity;
import com.foodcubo.foodcubo.foodcubo.Model.Category;
import com.foodcubo.foodcubo.foodcubo.Model.Food;
import com.foodcubo.foodcubo.foodcubo.Model.Token;
import com.foodcubo.foodcubo.foodcubo.ViewHolder.MenuViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;

public class RestaurantFoodDetailLists extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DataTransferInterface {
    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);

        super.attachBaseContext(newBase);
    }

    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recycler_food_list;
    RecyclerView.LayoutManager layoutManager;
    FoodListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar mLoadingProgress;
    CounterFab fab;
    DatabaseReference foodList;
    HashMap<String, String> categoryList= new HashMap<String, String>();
    TextView restaurantName,restaurantNameSub,userAddress;
    ImageView nav_right;
    String categoryName="";
    private ArrayList<Food> foodFullList = new ArrayList<>();
    com.foodcubo.foodcubo.foodcubo.Database.Database localDB;
    Switch vegSwitch;
    Integer cartValues;
    Toolbar toolBar;

    public RestaurantFoodDetailLists(){

    }

    /*@Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_restaurant_details);
        restaurantName = findViewById(R.id.detail_restaurant_name);
        nav_right = findViewById(R.id.arrow_white);
        restaurantNameSub = findViewById(R.id.detail_restaurant_name_sub);
        userAddress = findViewById(R.id.user_address);
//        restaurantName.setText(getIntent().getStringExtra("restaurantName"));
        restaurantNameSub.setText(getIntent().getStringExtra("restaurantName"));
        userAddress.setText(getIntent().getStringExtra("userAddress"));
        mLoadingProgress = findViewById(R.id.loading_progress);
        vegSwitch = findViewById(R.id.vegSwitch);
        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        toolBar = findViewById(R.id.toolbar);
       final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        toolBar.setNavigationIcon(upArrow);

        toolBar.setTitle(getIntent().getStringExtra("restaurantName"));
       // toolBar.setTitleTextColor(getColor(R.color.white));

      //  toolBar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

  /*      nav_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
*/
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("details")
                .child("Food");

        category = database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("details")
                .child("Category");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadListFood(categoryList);
                else {
                    Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Default load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadListFood(categoryList);
                else {
                    Toast.makeText(RestaurantFoodDetailLists.this, "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();

        category.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String categoryId="";
                        String categoryName="";
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            categoryId=snapshot.getKey();
                            /*Category categoryDetails = snapshot.getValue(Category.class);
                            categoryList.put(categoryId,categoryDetails);*/
                            categoryName = snapshot.child("Name").getValue().toString();
                            categoryList.put(categoryId,categoryName);
                        }
                    }


            @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        recycler_food_list =  findViewById(R.id.recycler_food_list);

        layoutManager = new LinearLayoutManager(this);
        recycler_food_list.setLayoutManager(layoutManager);
        recycler_food_list.setHasFixedSize(true);





        if (Common.isConnectedToInternet(this))
            loadListFood(categoryList);
        else {
            Toast.makeText(RestaurantFoodDetailLists.this, "Please check your Connection!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Common.currentUser != null) {

            String token = FirebaseInstanceId.getInstance().getInstanceId().toString();
            updateToken(token);

        } else {
            Common.reopenApp(RestaurantFoodDetailLists.this);
        }
        vegSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.setSearchType("vegtypetrue");
                    adapter.getFilter().filter("true");
                } else {
                    adapter.setSearchType("vegtypefalse");
                    adapter.getFilter().filter("");
                }
            }
        });
        Paper.init(this);

        fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab.getCount() == 0) {
                    Toast.makeText(RestaurantFoodDetailLists.this,"Cart is empty,Please add items to order!!",Toast.LENGTH_LONG).show();

                } else {
                    Intent cartIntent = new Intent(RestaurantFoodDetailLists.this, com.foodcubo.foodcubo.foodcubo.Cart.class);
                    startActivity(cartIntent);

                }
            }
        });
       /* if(Common.currentUser!=null){
            fab.setCount(new com.foodcubo.foodcubo.foodcubo.Database.Database(this).getCountCart(Common.currentUser.getPhone()));


        }else{
            Common.reopenApp(RestaurantFoodDetailLists.this);
        }
    */}
        @Override
        protected void onResume() {
            super.onResume();


            if(Common.currentUser!=null){
                setAdapter();
                fab.setCount(new com.foodcubo.foodcubo.foodcubo.Database.Database(this).getFullOrderQuantity(Common.currentUser.getPhone()));

            }else{
                Common.reopenApp(RestaurantFoodDetailLists.this);
            }

        }

        private void updateToken(String token) {
            FirebaseDatabase db=FirebaseDatabase.getInstance();
            DatabaseReference tokens=db.getReference("Tokens");
            Token data = new Token(token,false);
            String curUser = Common.currentUser.getPhone();
            tokens.child(curUser).setValue(data);
        }

        private void loadListFood(HashMap<String, String> categoryLists) {
            String categoryId="";

            //vegSwitch.setChecked(false);
            foodFullList.removeAll(foodFullList);
            foodFullList.clear();
            Iterator it = categoryLists.entrySet().iterator();
            while (it.hasNext())
            {
                final Map.Entry pair = (Map.Entry)it.next();
                categoryId = pair.getKey().toString();
                categoryName = pair.getValue().toString();


                foodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       // foodFullList.removeAll(foodFullList);
                        //foodFullList.clear();
                        String category="";

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Food f = new Food(ds.getKey(), ds.child("name").getValue(String.class),
                                    ds.child("image").getValue(String.class),
                                    ds.child("description").getValue(String.class),
                                    ds.child("pieceType").getValue(String.class),
                                    ds.child("fullprice").getValue(String.class),
                                    ds.child("halfprice").getValue(String.class),
                                    ds.child("discount").getValue(String.class),
                                    ds.child("menuId").getValue(String.class),
                                    "",
                                    "",
                                    ds.child("vegType").getValue(String.class),
                                    category= categoryList.get(ds.child("menuId").getValue(String.class))
                            );
                            foodFullList.add(f);

                        }
                        setAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }

        }

    private void setAdapter(){
        if(adapter == null) {

            adapter = new FoodListAdapter(RestaurantFoodDetailLists.this, foodFullList, localDB,this);
            //set adapter
            recycler_food_list.setAdapter(adapter);
            recycler_food_list.setNestedScrollingEnabled(true);
            adapter.notifyDataSetChanged();
        }else{
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

        @Override
        protected void onStop() {
            super.onStop();
        }

    @Override
    public void finish() {
        super.finish();
    }

        @Override
        public void onBackPressed() {

                finish();

        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_menu) {
                return true;
            }else if(id == R.id.nav_cart){
                Intent cartIntent = new Intent(RestaurantFoodDetailLists.this, com.foodcubo.foodcubo.foodcubo.Cart.class);
                startActivity(cartIntent);

            }else if(id == R.id.nav_orders){
                Intent orderIntent = new Intent(RestaurantFoodDetailLists.this, OrderStatus.class);
                startActivity(orderIntent);

            }else if(id == R.id.nav_log_out){
                AccountKit.logOut();
                Intent signIn = new Intent(RestaurantFoodDetailLists.this, ScreenOneActivity.class);
                signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signIn);
            }
            else if(id == R.id.nav_update_name){
                showChangePasswordDialog();

            }
            else if(id == R.id.nav_nearby_store){
                startActivity(new Intent(RestaurantFoodDetailLists.this, com.foodcubo.foodcubo.foodcubo.NearbyStore.class));
            }
            else if(id == R.id.nav_home_address){
                showHomeAddressDialog();

            }
            else if(id == R.id.nav_settings){
                showHomeSettingDialog();
            }
            else if(id == R.id.nav_fav){
                startActivity(new Intent(RestaurantFoodDetailLists.this,FavoritesActivity.class));
            }
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        private void showHomeAddressDialog() {
            AlertDialog.Builder alertDialog =new AlertDialog.Builder(RestaurantFoodDetailLists.this);
            alertDialog.setTitle("CHANGE HOME ADDRESS");
            alertDialog.setMessage("Please fill all information");

            LayoutInflater inflater=LayoutInflater.from(this);
            View layout_home=inflater.inflate(R.layout.home_address_layout,null);

            final MaterialEditText edtHomeAddress=layout_home.findViewById(R.id.edtHomeAddress);

            alertDialog.setView(layout_home);

            alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(Common.currentUser!=null){
                        dialogInterface.dismiss();
                        Common.currentUser.setHomeAddress(Objects.requireNonNull(edtHomeAddress.getText()).toString());
                        FirebaseDatabase.getInstance().getReference("User")
                                .child(Common.currentUser.getPhone())
                                .setValue(Common.currentUser)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(RestaurantFoodDetailLists.this,"Updated successfully",Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }else{
                        Common.reopenApp(RestaurantFoodDetailLists.this);
                    }



                }
            });
            alertDialog.show();
        }

        private void showHomeSettingDialog() {
            final AlertDialog.Builder alertDialog =new AlertDialog.Builder(RestaurantFoodDetailLists.this);
            alertDialog.setTitle("SETTINGS");

            LayoutInflater inflater=LayoutInflater.from(this);
            View layout_setting=inflater.inflate(R.layout.setting_layout,null);

            final CheckBox ckb_subscribe_news=layout_setting.findViewById(R.id.ckb_sub_new);
            Paper.init(this);

            String isSubscribe=Paper.book().read("sub_new");

            if(isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false"))
                ckb_subscribe_news.setChecked(false);
            else
                ckb_subscribe_news.setChecked(true);

            alertDialog.setView(layout_setting);

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();

                    if (ckb_subscribe_news.isChecked()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);

                        Paper.book().write("sub_new", true);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);

                        Paper.book().write("sub_new", false);
                    }

                }
            });
            alertDialog.show();


        }
        private void showChangePasswordDialog() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(RestaurantFoodDetailLists.this);
            alertDialog.setTitle("UPDATE NAME");
            alertDialog.setMessage("Please fill all information");

            LayoutInflater inflater = LayoutInflater.from(this);
            View layout_pwd = inflater.inflate(R.layout.update_name_layout, null);

            alertDialog.setView(layout_pwd);

            //button

            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            alertDialog.show();
        }

    @Override
    public void setCartValues(Integer cartValue) {
        cartValues = cartValue;

        fab.setCount(cartValue);
    }


}










