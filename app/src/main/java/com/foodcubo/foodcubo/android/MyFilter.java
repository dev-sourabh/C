package com.foodcubo.foodcubo.android;

import android.widget.Filter;

import com.foodcubo.foodcubo.android.Adapter.FoodListAdapter;
import com.foodcubo.foodcubo.foodcubo.Model.Food;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyFilter extends Filter {

    private final FoodListAdapter adapter;
    private final List<Food> originalList;
    private final ArrayList<Food> filteredList;

    public MyFilter(FoodListAdapter adapter, ArrayList<Food> originalList) {
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
            final String filterPattern = charSequence.toString().toLowerCase().trim();
            for (Food item : originalList) {
                if(adapter.searchType.equals("vegtypetrue")) {
                    if (item.getVegType().toLowerCase().contains("true"))
                        filteredList.add(item);
                }else if(adapter.searchType.equals("search")){
                    if (item.getName().toLowerCase().contains(filterPattern))
                        filteredList.add(item);
                }


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
        adapter.mDataset.addAll((ArrayList<Food>) filterResults.values);
        adapter.notifyDataSetChanged();
    }
}