package com.foodcubo.foodcubo.foodcubo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.android.ViewHolder.FoodViewHolder;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Model.Food;
import com.foodcubo.foodcubo.foodcubo.materialsearchview.MaterialSearchView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    ArrayList<String> suggestList =new ArrayList<>();
    private MaterialSearchView search;

    FirebaseDatabase database;
    DatabaseReference foodList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    Database localDB;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        search.setMenuItem(item);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.searchtoolbarBar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("details")
                .child("Food");
        localDB=new Database(this);

        recyclerView = findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        search = findViewById(R.id.searchBar);
        search.setHint("Search Food");
        search.setTextColor(Color.WHITE);
        search.setBackIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        search.setCloseIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        search.setHintTextColor(Color.WHITE);
        search.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        search.setVoiceSearch(false);
        search.setCursorDrawable(R.drawable.color_cursor_white);
        search.setEllipsize(true);
        search.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();
                return false;
            }

            @Override
            public void onQueryTextChange(String newText) {
                //Do some magic
            }
        });


        loadSuggest();


    }

    @Override
    public void onBackPressed() {
        if (search.isSearchOpen()) {
            search.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void loadSuggest() {
        foodList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    final Food item = snapshot.getValue(Food.class);
                    assert item != null;
                    suggestList.add(item.getName());

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
    protected void onStop() {
        if(adapter != null)
            adapter.stopListening();
        if(searchAdapter != null)
            searchAdapter.stopListening();
        super.onStop();
    }
}
