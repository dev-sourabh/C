package com.foodcubo.foodcubo.foodcubo;

import android.Manifest;
import android.support.multidex.MultiDex;
import android.content.Context;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.FavoritesActivity;
import com.foodcubo.foodcubo.android.FoodListNew;
import com.foodcubo.foodcubo.android.OrderStatus;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.android.ScreenOneActivity;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;
import com.foodcubo.foodcubo.foodcubo.Model.Banner;
import com.foodcubo.foodcubo.foodcubo.Model.Category;
import com.foodcubo.foodcubo.foodcubo.Model.Food;
import com.foodcubo.foodcubo.foodcubo.Model.Token;
import com.foodcubo.foodcubo.foodcubo.ViewHolder.MenuViewHolder;
import com.foodcubo.foodcubo.foodcubo.materialsearchview.MaterialSearchView;
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
import java.util.Objects;

import io.paperdb.Paper;


public class RestaurantDetailsWithFoodLists extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);

        super.attachBaseContext(newBase);
    }

    FirebaseDatabase database;
    DatabaseReference category;
    final ThreadLocal<TextView> tvOpenCloseTimings = new ThreadLocal<>();
    RecyclerView recycler_menu;
    ImageView ivCallRestaurant;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private static final int CALL_PHONE_REQUEST_CODE = 9999;
    ProgressBar mLoadingProgress;

    CounterFab fab;

    HashMap<String, String> image_list;
    SliderLayout mSlider;
    private Dialog mCallDialog;

    public RestaurantDetailsWithFoodLists() {
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setCallDialog() {

        mCallDialog = new Dialog(RestaurantDetailsWithFoodLists.this);
        mCallDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCallDialog.setContentView(R.layout.dialog_call_restaurant);
        Objects.requireNonNull(mCallDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel = mCallDialog.findViewById(R.id.tv_cancel);
        TextView ok = mCallDialog.findViewById(R.id.tv_call);
        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mCallDialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {

            @TargetApi(16)
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(RestaurantDetailsWithFoodLists.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED
                        ) {
                    ActivityCompat.requestPermissions(RestaurantDetailsWithFoodLists.this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, CALL_PHONE_REQUEST_CODE);

                } else {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + getIntent().getStringExtra("phoneNo")));
                    startActivityForResult(intent, 0);
                }
                mCallDialog.dismiss();
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_PHONE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + getIntent().getStringExtra("phoneNo")));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivityForResult(intent, 0);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoadingProgress.setVisibility(View.GONE);
    }

    private void loadSuggest() {
        foodList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    final Food item = snapshot.getValue(Food.class);
                    if(item != null) {
                        // assert item != null;
                        suggestList.add(item.getName());
                    }

                }

                String[] stockArr = new String[suggestList.size()];
                stockArr = suggestList.toArray(stockArr);

                System.out.println("suggestions...................."+stockArr.length);
                search.setSuggestions(stockArr);

                //  search.addAllSearchables(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        search.setMenuItem(item);

        return true;
    }


    ArrayList<String> suggestList =new ArrayList<>();
    private MaterialSearchView search;
    DatabaseReference foodList;
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_home);

        Toolbar toolbar = findViewById(R.id.searchtoolbarBar);
        toolbar.setTitle(getIntent().getStringExtra("restaurantName"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("details")
                .child("Food");



        search = findViewById(R.id.searchBar);
        search.setMargin(45);
        search.setHint("Search Food");
        search.setTextColor(Color.WHITE);

           /*commented by sonal to remove deprecation issue if getDrawble(int)
           search.setBackIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            search.setCloseIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));*/

            search.setBackIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp,getTheme()));
            search.setCloseIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp,getTheme()));


        search.setHintTextColor(Color.WHITE);
       /*commented by sonal to remove deprecation issue getResources().getColor(R.color.colorPrimary)
        search.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
         */
        search.setBackgroundColor(getColor(R.color.colorPrimary));
        search.setVoiceSearch(false);
        search.setCursorDrawable(R.drawable.color_cursor_white);
        search.setEllipsize(true);


        search.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public void onQueryTextChange(String newText) {
                //Do some magic
                return;
            }
        });

        loadSuggest();

        setCallDialog();

        mLoadingProgress = findViewById(R.id.loading_progress);

        ivCallRestaurant=findViewById(R.id.iv_callRestaurant);
        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        ivCallRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallDialog.show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadMenu();
                else{
                    Toast.makeText(getBaseContext(),"Please check your connection!!!!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Default load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadMenu();
                else{
                    Toast.makeText(RestaurantDetailsWithFoodLists.this,"Please check your connection!!!!",Toast.LENGTH_SHORT).show();
                return;
                }
            }
        });


        //Init Firebase
        category = database.getReference("Restaurants").child(Common.restaurantSelected)
        .child("details")
        .child("Category");

        tvOpenCloseTimings.set((TextView) findViewById(R.id.tv_openclosetimings));

        database.getReference("Restaurants")
                .child(Common.restaurantSelected)
                .child("details")
                .child("Timings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String openTime = "";
                String closeTime = "";

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if(Objects.equals(ds.getKey(), "openTime"))
                        openTime=ds.getValue(String.class);

                    if(Objects.equals(ds.getKey(), "closeTime"))
                        closeTime=ds.getValue(String.class);

                    System.out.println("jiji......"+ds.getKey());

                }
                Objects.requireNonNull(tvOpenCloseTimings.get()).setText(openTime+" to "+ closeTime);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseRecyclerOptions<Category> options=new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                //changed by sonal back to old version Picasso.get().load(model.getImage()).into(viewHolder.imageView);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get CategoryId and send to new Activity
                        Intent foodList = new Intent(RestaurantDetailsWithFoodLists.this, FoodListNew.class);
                        //Because CategoryId is key, so we just get the key of this item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(itemView);
            }
        };

        Paper.init(this);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(RestaurantDetailsWithFoodLists.this, com.foodcubo.foodcubo.foodcubo.Cart.class);
                startActivity(cartIntent);

            }
        });
        if(Common.currentUser!=null){
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        }else{
            Common.reopenApp(RestaurantDetailsWithFoodLists.this);
        }



            //Load menu
        recycler_menu = findViewById(R.id.recycler_menu);
   //     recycler_menu.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recycler_menu.setLayoutManager(layoutManager);

        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));

        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recycler_menu.getContext()
        ,R.anim.layout_fall_down);
        recycler_menu.setLayoutAnimation(controller);



        if(Common.isConnectedToInternet(this))
            loadMenu();
        else {
            Toast.makeText(RestaurantDetailsWithFoodLists.this, "Please check your Connection!!!", Toast.LENGTH_SHORT).show();
            return;
        }


        //Register service
//        Intent service=new Intent(RestaurantDetailsWithFoodLists.this, ListenOrder.class);
//        startService(service);
        if(Common.currentUser!=null){
        /*Commented by Sonal to resolve issue of deprecated of getToken();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        */
            String token =FirebaseInstanceId.getInstance().getInstanceId().toString();
            updateToken(token);

        }else{
            Common.reopenApp(RestaurantDetailsWithFoodLists.this);
        }




            setupSlider();

    }

    private void setupSlider() {
        mSlider=findViewById(R.id.slider);

        image_list=new HashMap<>();

        final DatabaseReference banners= database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("details")
                .child("Banner");
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Banner banner=postSnapshot.getValue(Banner.class);
                    if(banner != null) {
                        //assert banner != null;
                        image_list.put(banner.getName() + "@@@" + banner.getId(), banner.getImage());
                    }
                }
                for(String key:image_list.keySet()){
                    String[] keySplit=key.split("@@@");
                    String nameOfFood=keySplit[0];
                    final String idOfFood=keySplit[1];


                    final TextSliderView textSliderView=new TextSliderView(getBaseContext());
                   // textSliderView.setPicasso(Picasso.get());
                    textSliderView
                            .image(image_list.get(key))
                            .description(nameOfFood)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            ;
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId",idOfFood);

                    mSlider.addSlider(textSliderView);

                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.currentUser!=null){
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
        }else{
            Common.reopenApp(RestaurantDetailsWithFoodLists.this);
        }





        if(adapter != null)
            adapter.startListening();
    }

    private void updateToken(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data = new Token(token,false);
        String curUser = Common.currentUser.getPhone();
        tokens.child(curUser).setValue(data);
    }

    private void loadMenu() {


        adapter.startListening();
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);


        Objects.requireNonNull(recycler_menu.getAdapter()).notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        adapter.stopListening();
        if(mSlider!=null)
        mSlider.startAutoCycle();
    }

    @Override
    public void onBackPressed() {
        if (search.isSearchOpen()) {
            search.closeSearch();
        } else if(mLoadingProgress.getVisibility()==View.VISIBLE){
            mLoadingProgress.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            return true;
        }else if(id == R.id.nav_cart){
            Intent cartIntent = new Intent(RestaurantDetailsWithFoodLists.this, com.foodcubo.foodcubo.foodcubo.Cart.class);
            startActivity(cartIntent);

        }else if(id == R.id.nav_orders){
            Intent orderIntent = new Intent(RestaurantDetailsWithFoodLists.this, OrderStatus.class);
            startActivity(orderIntent);

        }else if(id == R.id.nav_log_out){
            AccountKit.logOut();
            Intent signIn = new Intent(RestaurantDetailsWithFoodLists.this, ScreenOneActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signIn);
        }
        else if(id == R.id.nav_update_name){
           showChangePasswordDialog();

        }
        else if(id == R.id.nav_nearby_store){
           startActivity(new Intent(RestaurantDetailsWithFoodLists.this, com.foodcubo.foodcubo.foodcubo.NearbyStore.class));
        }
        else if(id == R.id.nav_home_address){
            showHomeAddressDialog();

        }
        else if(id == R.id.nav_settings){
            showHomeSettingDialog();
        }
        else if(id == R.id.nav_fav){
            startActivity(new Intent(RestaurantDetailsWithFoodLists.this,FavoritesActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHomeAddressDialog() {
        AlertDialog.Builder alertDialog =new AlertDialog.Builder(RestaurantDetailsWithFoodLists.this);
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
                                Toast.makeText(RestaurantDetailsWithFoodLists.this,"Updated successfully",Toast.LENGTH_SHORT).show();
                            }
                        });

                }else{
                    Common.reopenApp(RestaurantDetailsWithFoodLists.this);
                }



            }
        });
        alertDialog.show();
    }

    private void showHomeSettingDialog() {
        final AlertDialog.Builder alertDialog =new AlertDialog.Builder(RestaurantDetailsWithFoodLists.this);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RestaurantDetailsWithFoodLists.this);
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
}
