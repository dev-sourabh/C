/*
package com.foodcubo.foodcubo.android.Adapter;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.MyFilter;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.android.ViewHolder.FoodViewHolder;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;
import com.foodcubo.foodcubo.foodcubo.Model.Favorites;
import com.foodcubo.foodcubo.foodcubo.Model.Food;
import com.foodcubo.foodcubo.foodcubo.Model.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.foodcubo.foodcubo.android.Common.Common.currentUser;
import static com.foodcubo.foodcubo.android.Common.Common.reopenApp;
import static com.foodcubo.foodcubo.android.Common.Common.restaurantCartName;
import static com.foodcubo.foodcubo.android.Common.Common.restaurantCartPhone;
import static com.foodcubo.foodcubo.android.Common.Common.restaurantSelectedName;
import static com.foodcubo.foodcubo.android.Common.Common.restaurantSelectedPhone;

public class FoodListAdapter_org extends RecyclerView.Adapter<FoodViewHolder> implements Filterable {

    private final AppCompatActivity mContext;
    private final ArrayList<Food> foodFullList;
    private final Database localDB;
    public ArrayList<Food> mDataset;
    private MyFilter filter;
    public String searchType;

    public FoodListAdapter_org(AppCompatActivity context, ArrayList<Food> foodlist, Database localDB) {
        mDataset = foodlist;

        this.mContext = context;
        this.foodFullList = foodlist;
        this.localDB = localDB;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position) {
        final Food model = mDataset.get(viewHolder.getAdapterPosition());
        viewHolder.food_name.setText(model.getName());
        viewHolder.full_food_price.setText(String.format("Full Plate   -   Rs. %s", model.getFullprice()));
        viewHolder.half_food_price.setText(String.format("Half Plate   -  Rs. %s", model.getHalfprice()));
        Picasso.with(mContext).load(model.getImage()).into(viewHolder.food_image);
        //changed by sonal back to old version Picasso.get().load(model.getImage()).into(viewHolder.food_image);

        if (model.getVegType() != null)
            if (model.getVegType().equals("true"))
                viewHolder.isveg_image.setColorFilter(
                        ContextCompat.getColor(mContext, android.R.color.holo_green_dark),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            else
                viewHolder.isveg_image.setColorFilter(
                        ContextCompat.getColor(mContext, android.R.color.holo_red_dark),
                        android.graphics.PorterDuff.Mode.SRC_IN);
        if (model.getPieceType().equals("true"))
            viewHolder.halfprice_layout.setVisibility(View.VISIBLE);
        else
            viewHolder.halfprice_layout.setVisibility(View.GONE);

        int fullcount = new Database(mContext).getOrderQuantity(
                model.getKey(), Common.currentUser.getPhone(), "full");
        if (fullcount > 0) {
            viewHolder.btn_full_quick_add.setVisibility(View.GONE);
            viewHolder.btn_full_quick_cart.setVisibility(View.VISIBLE);
            viewHolder.btn_full_quick_cart.setNumber(String.valueOf(fullcount));
        } else {
            viewHolder.btn_full_quick_add.setVisibility(View.VISIBLE);
            viewHolder.btn_full_quick_cart.setVisibility(View.GONE);
        }

        viewHolder.btn_full_quick_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    addToCart(model, model.getFullprice(), viewHolder, "full");

                } else {
                    reopenApp(mContext);
                }
            }
        });

        int halfcount = new Database(mContext).getOrderQuantity(
                model.getKey(), currentUser.getPhone(), "half");
        if (halfcount > 0) {
            viewHolder.btn_half_quick_add.setVisibility(View.GONE);
            viewHolder.btn_half_quick_cart.setVisibility(View.VISIBLE);
            viewHolder.btn_half_quick_cart.setNumber(String.valueOf(halfcount));
        } else {
            viewHolder.btn_half_quick_add.setVisibility(View.VISIBLE);
            viewHolder.btn_half_quick_cart.setVisibility(View.GONE);
        }

        viewHolder.btn_half_quick_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    addToCart(model, model.getHalfprice(), viewHolder, "half");

                } else {
                    reopenApp(mContext);
                }
            }
        });
        viewHolder.btn_full_quick_cart.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                if (currentUser != null) {
                    if (newValue > oldValue)
                        addToCart(model, model.getFullprice(), viewHolder, "full");
                    else if(newValue ==0) {
                        new Database(mContext).removeFromCart(model.getKey()
                                , currentUser.getPhone(), "full");

                        int fullcount = new Database(mContext).getOrderQuantity(
                                model.getKey(), currentUser.getPhone(), "full");
                        System.out.println("removed............."+fullcount);
                        if (fullcount > 0) {
                            viewHolder.btn_full_quick_add.setVisibility(View.GONE);
                            viewHolder.btn_full_quick_cart.setVisibility(View.VISIBLE);
                            viewHolder.btn_full_quick_cart.setNumber(String.valueOf(fullcount));
                        } else {
                            viewHolder.btn_full_quick_add.setVisibility(View.VISIBLE);
                            viewHolder.btn_full_quick_cart.setVisibility(View.GONE);
                        }
                    }else{
                        new Database(mContext).decreaseQuantityFromCart(model.getKey()
                                , currentUser.getPhone(), "full");

                        int fullcount = new Database(mContext).getOrderQuantity(
                                model.getKey(), currentUser.getPhone(), "full");
                        System.out.println("decreased............."+fullcount);
                        if (fullcount > 0) {
                            viewHolder.btn_full_quick_add.setVisibility(View.GONE);
                            viewHolder.btn_full_quick_cart.setVisibility(View.VISIBLE);
                            viewHolder.btn_full_quick_cart.setNumber(String.valueOf(fullcount));
                        } else {
                            viewHolder.btn_full_quick_add.setVisibility(View.VISIBLE);
                            viewHolder.btn_full_quick_cart.setVisibility(View.GONE);
                        }
                    }
                } else {
                    reopenApp(mContext);
                }

            }
        });

        viewHolder.btn_half_quick_cart.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                if (currentUser != null) {
                    if (newValue > oldValue)
                        addToCart(model, model.getHalfprice(), viewHolder, "half");
                    else if(newValue ==0) {
                        new Database(mContext).removeFromCart(model.getKey()
                                , currentUser.getPhone(), "half");

                        int halfcount = new Database(mContext).getOrderQuantity(
                                model.getKey(), currentUser.getPhone(), "half");
                        if (halfcount > 0) {
                            viewHolder.btn_half_quick_add.setVisibility(View.GONE);
                            viewHolder.btn_half_quick_cart.setVisibility(View.VISIBLE);
                            viewHolder.btn_half_quick_cart.setNumber(String.valueOf(halfcount));
                        } else {
                            viewHolder.btn_half_quick_add.setVisibility(View.VISIBLE);
                            viewHolder.btn_half_quick_cart.setVisibility(View.GONE);
                        }
                    }else
                    {
                        new Database(mContext).decreaseQuantityFromCart(model.getKey()
                                , currentUser.getPhone(), "half");

                        int halfcount = new Database(mContext).getOrderQuantity(
                                model.getKey(), currentUser.getPhone(), "half");
                        System.out.println("decreased............."+halfcount);
                        if (halfcount > 0) {
                            viewHolder.btn_half_quick_add.setVisibility(View.GONE);
                            viewHolder.btn_half_quick_cart.setVisibility(View.VISIBLE);
                            viewHolder.btn_half_quick_cart.setNumber(String.valueOf(halfcount));
                        } else {
                            viewHolder.btn_half_quick_add.setVisibility(View.VISIBLE);
                            viewHolder.btn_half_quick_cart.setVisibility(View.GONE);
                        }
                    }

                } else {
                    reopenApp(mContext);
                }

            }
        });

        if (currentUser != null) {
            //add to fav
            if (localDB.isFavorite(model.getKey(), currentUser.getPhone()))
                viewHolder.fav_image.setColorFilter(
                        ContextCompat.getColor(mContext, R.color.colorAccent),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            else
                viewHolder.fav_image.setColorFilter(
                        ContextCompat.getColor(mContext, R.color.grey),
                        android.graphics.PorterDuff.Mode.SRC_IN);

        } else {
            reopenApp(mContext);
        }


        //click to change
        viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    Favorites favorites = new Favorites();
                    favorites.setFoodId(model.getKey());
                    favorites.setFoodName(model.getName());
                    favorites.setFoodDescription(model.getDescription());
                    favorites.setFoodDiscount(model.getDiscount());
                    favorites.setFoodImage(model.getImage());
                    favorites.setFoodMenuId(model.getMenuId());
                    favorites.setUserPhone(currentUser.getPhone());
                    favorites.setFoodPieceType(model.getPieceType());
                    favorites.setFoodfullprice(model.getFullprice());
                    favorites.setFoodhalfprice(model.getHalfprice());
                    favorites.setVegType(model.getVegType());
                    favorites.setFoodRestaurantName(restaurantCartName);
                    favorites.setFoodRestaurantPhone(restaurantCartPhone);
                    if (!localDB.isFavorite(model.getKey(), currentUser.getPhone())) {
                        localDB.addToFavorites(favorites);
                        viewHolder.fav_image.setColorFilter(
                                ContextCompat.getColor(mContext, R.color.colorAccent),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                        Toast.makeText(mContext, "" + model.getName() + " is added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        localDB.removeFromFavorites(model.getKey(), currentUser.getPhone());
                        viewHolder.fav_image.setColorFilter(
                                ContextCompat.getColor(mContext, R.color.grey),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                        Toast.makeText(mContext, "" + model.getName() + " is removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    reopenApp(mContext);
                }


            }
        });

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //start new activity
                */
/*Intent foodDetail =  new Intent(mContext, FoodDetail.class);
                foodDetail.putExtra("FoodId", model.getKey());
                mContext.startActivity(foodDetail);*//*

            }
        });
    }

    private void addToCart(final Food model, final String price, final FoodViewHolder viewHolder, final String pricetype) {
        if (restaurantCartName.equals("") || restaurantCartName.equals(restaurantSelectedName)) {

            int count = new Database(mContext).checkFoodExists(
                    model.getKey(), currentUser.getPhone(), pricetype);


            restaurantCartName = restaurantSelectedName;
            restaurantCartPhone = restaurantSelectedPhone;
            boolean isExists = count > 0;
            System.out.println("exits value............" + isExists + "...." + model.getPieceType());
            if (!isExists) {
                new Database(mContext).addToCart(new Order(
                        currentUser.getPhone(),
                        model.getKey(),
                        model.getName(),
                        "1",
                        price,
                        model.getDiscount(),
                        model.getImage(),
                        model.getVegType(),
                        restaurantCartName,
                        pricetype,
                        model.getPieceType(),
                        restaurantCartPhone
                ));
            } else {
                new Database(mContext).increaseCart(currentUser.getPhone(),
                        model.getKey(), pricetype);
            }
            count = new Database(mContext).getOrderQuantity(
                    model.getKey(), currentUser.getPhone(), pricetype);
            Toast.makeText(mContext, "Added to Cart", Toast.LENGTH_SHORT).show();

            if (pricetype.equals("full")) {
                if (count > 0) {
                    viewHolder.btn_full_quick_add.setVisibility(View.GONE);
                    viewHolder.btn_full_quick_cart.setVisibility(View.VISIBLE);
                    viewHolder.btn_full_quick_cart.setNumber(String.valueOf(count));
                } else {
                    viewHolder.btn_full_quick_add.setVisibility(View.VISIBLE);
                    viewHolder.btn_full_quick_cart.setVisibility(View.GONE);
                }
            }

            if (pricetype.equals("half")) {
                if (count > 0) {
                    viewHolder.btn_half_quick_add.setVisibility(View.GONE);
                    viewHolder.btn_half_quick_cart.setVisibility(View.VISIBLE);
                    viewHolder.btn_half_quick_cart.setNumber(String.valueOf(count));
                } else {
                    viewHolder.btn_half_quick_add.setVisibility(View.VISIBLE);
                    viewHolder.btn_half_quick_cart.setVisibility(View.GONE);
                }
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Replace cart item?");
            alertDialog.setMessage("Your cart contains dishes from " + restaurantCartName + ". Do you want" +
                    " to remove items from cart and add dishes from " + restaurantSelectedName + "?");
            alertDialog.setIcon(R.drawable.location);
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    restaurantCartName = restaurantSelectedName;
                    restaurantCartPhone = restaurantSelectedPhone;
                    new Database(mContext).cleanCart(currentUser.getPhone());

                    int count = new Database(mContext).checkFoodExists(
                            model.getKey(), currentUser.getPhone(), pricetype);


                    boolean isExists = count > 0;
                    if (!isExists) {
                        new Database(mContext).addToCart(new Order(
                                currentUser.getPhone(),
                                model.getKey(),
                                model.getName(),
                                "1",
                                price,
                                model.getDiscount(),
                                model.getImage(),
                                model.getVegType(),
                                restaurantCartName,
                                pricetype,
                                model.getPieceType(),
                                restaurantCartPhone
                        ));
                    } else {
                        new Database(mContext).increaseCart(currentUser.getPhone(),
                                model.getKey(), pricetype);
                    }
                    count = new Database(mContext).getOrderQuantity(
                            model.getKey(), currentUser.getPhone(), pricetype);

                    Toast.makeText(mContext, "Added to Cart", Toast.LENGTH_SHORT).show();

                    if (pricetype.equals("full")) {
                        if (count > 0) {
                            viewHolder.btn_full_quick_add.setVisibility(View.GONE);
                            viewHolder.btn_full_quick_cart.setVisibility(View.VISIBLE);
                            viewHolder.btn_full_quick_cart.setNumber(String.valueOf(count));
                        } else {
                            viewHolder.btn_full_quick_add.setVisibility(View.VISIBLE);
                            viewHolder.btn_full_quick_cart.setVisibility(View.GONE);
                        }
                    }

                    if (pricetype.equals("half")) {
                        if (count > 0) {
                            viewHolder.btn_half_quick_add.setVisibility(View.GONE);
                            viewHolder.btn_half_quick_cart.setVisibility(View.VISIBLE);
                            viewHolder.btn_half_quick_cart.setNumber(String.valueOf(count));
                        } else {
                            viewHolder.btn_half_quick_add.setVisibility(View.VISIBLE);
                            viewHolder.btn_half_quick_cart.setVisibility(View.GONE);
                        }
                    }


                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertDialog.show();
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
     //       filter = new MyFilter(this, foodFullList);
        }
        return filter;
    }
}
*/
