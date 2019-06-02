package com.foodcubo.foodcubo.android;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Helper.RecyclerItemTouchHelper;
import com.foodcubo.foodcubo.foodcubo.Interface.RecyclerItemTouchHelperListener;
import com.foodcubo.foodcubo.foodcubo.Model.Favorites;
import com.foodcubo.foodcubo.foodcubo.ViewHolder.FavoritesViewHolder;

import java.util.Objects;

@SuppressLint("Registered")
public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    FavoritesAdapter adapter = new FavoritesAdapter(new Database(this).getAllFavorites(Common.currentUser.getPhone()));
    RelativeLayout rootLayout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rootLayout=findViewById(R.id.root_layout);

        recyclerView = findViewById(R.id.recycler_fav);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe  delete item
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        if(Common.currentUser!=null){
        loadFavorites();
        }else{
            Common.reopenApp(FavoritesActivity.this);
        }



    }

    private void loadFavorites() {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(Common.currentUser!=null){
        if(viewHolder instanceof FavoritesViewHolder){
            @SuppressLint({"NewApi", "LocalSuppress"}) String name = ((FavoritesAdapter)Objects.requireNonNull(recyclerView.getAdapter())).getItem(viewHolder.getAdapterPosition()).getFoodName();
            final Favorites deleteItem;
            deleteItem = ((FavoritesAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();
            adapter.removeItem();
            new Database(getBaseContext()).removeFromFavorites(deleteItem.getFoodId(), Common.currentUser.getPhone());

            Snackbar snackBar=Snackbar.make(rootLayout,name + " removed from cart !!!!!!",Snackbar.LENGTH_LONG);

            snackBar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem();
                    new Database(getBaseContext()).addToFavorites(deleteItem);

                }
            });
            snackBar.setActionTextColor(Color.YELLOW);
            snackBar.show();
        }
        }else{
            Common.reopenApp(FavoritesActivity.this);
        }



    }
}
