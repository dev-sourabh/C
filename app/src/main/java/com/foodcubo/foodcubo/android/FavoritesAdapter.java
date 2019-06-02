package com.foodcubo.foodcubo.android;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.foodcubo.foodcubo.foodcubo.Model.Favorites;

import java.util.List;

class FavoritesAdapter extends RecyclerView.Adapter {

    FavoritesAdapter(List<Favorites> allFavorites) {

    }

    void restoreItem() {

    }

    void removeItem() {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    Favorites getItem(int adapterPosition) {
        return null;
    }
}
