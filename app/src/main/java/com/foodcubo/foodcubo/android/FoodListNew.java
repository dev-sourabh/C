package com.foodcubo.foodcubo.android;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.foodcubo.foodcubo.android.Adapter.FoodListAdapter;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("Registered")
public class FoodListNew extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference foodList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";
    FoodListAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;
    Switch vegSwitch;
    Database localDB;


    List<String> suggestList =new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    private ArrayList<Food> foodFullList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("details")
                .child("Food");

        localDB=new Database(this);

        vegSwitch =  findViewById(R.id.vegSwitch);
        recyclerView =  findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(getIntent()!=null)
                    categoryId=getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId!=null) {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListFood();
                    else {
                        Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent()!=null)
                    categoryId=getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListFood();
                    else {
                        Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                materialSearchBar=findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter your Food");
                loadSuggest();
                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        List<String> suggest=new ArrayList<>();
                        for(String search:suggestList){
                            if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }

                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if(!enabled){
                            adapter.setSearchType("clearsearch");
                            adapter.getFilter().filter("");
                        }
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {

                        adapter.setSearchType("search");
                        adapter.getFilter().filter(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });


            }
        });

        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");



        if(!categoryId.isEmpty() && categoryId != null){
            if(Common.isConnectedToInternet(getBaseContext()))
                loadListFood();
            else {
                Toast.makeText(FoodListNew.this, "Please check your Connection!!!", Toast.LENGTH_SHORT).show();
                return;
            }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food item=postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(){
        vegSwitch.setChecked(false);

            foodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    foodFullList.removeAll(foodFullList);
                    foodFullList.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Food f=new Food(ds.getKey(),ds.child("name").getValue(String.class),
                                ds.child("image").getValue(String.class),
                                ds.child("description").getValue(String.class),
                                ds.child("pieceType").getValue(String.class),
                                ds.child("fullprice").getValue(String.class),
                                ds.child("halfprice").getValue(String.class),
                                ds.child("discount").getValue(String.class),
                                ds.child("menuId").getValue(String.class),
                                "",
                                "",
                                ds.child("vegType").getValue(String.class)
                        );
                        foodFullList.add(f);
                    }

                  //  adapter = new FoodListAdapter(FoodListNew.this,foodFullList,localDB);
                    //set adapter
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
