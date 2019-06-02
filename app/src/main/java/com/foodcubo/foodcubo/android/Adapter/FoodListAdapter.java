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
import android.widget.ImageView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.Interface.DataTransferInterface;
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

import static android.widget.RelativeLayout.TRUE;
import static com.foodcubo.foodcubo.android.Common.Common.currentUser;
import static com.foodcubo.foodcubo.android.Common.Common.reopenApp;
import static com.foodcubo.foodcubo.android.Common.Common.restaurantCartName;
import static com.foodcubo.foodcubo.android.Common.Common.restaurantCartPhone;
import static com.foodcubo.foodcubo.android.Common.Common.restaurantSelectedName;
import static com.foodcubo.foodcubo.android.Common.Common.restaurantSelectedPhone;

public class FoodListAdapter extends RecyclerView.Adapter<FoodViewHolder> implements Filterable {

    private final AppCompatActivity mContext;
    private final ArrayList<Food> foodFullList;
    private final Database localDB;
    public ArrayList<Food> mDataset;
    private MyFilter filter;
    public String searchType,categoryName,vegType ="";
    DataTransferInterface cartValues;
    Integer tempCartValues;
    int fullcount;



   /* public FoodListAdapter(AppCompatActivity context, ArrayList<Food> foodlist, Database localDB) {
        mDataset = foodlist;

        this.mContext = context;
        this.foodFullList = foodlist;
        this.localDB = localDB;
        this.categoryName = categoryName;
    }
*/
   public FoodListAdapter(AppCompatActivity context, ArrayList<Food> foodlist, Database localDB ,DataTransferInterface cartValues){
       mDataset = foodlist;

       this.mContext = context;
       this.foodFullList = foodlist;
       this.localDB = localDB;
       this.cartValues = cartValues;

   }


    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_bestsellers_lists, parent, false);
                //.inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position) {
        final Food model = mDataset.get(viewHolder.getAdapterPosition());
        viewHolder.food_name.setText(model.getName());
        viewHolder.full_food_price.setText(String.format("Full Plate   -   Rs. %s", model.getFullprice()));
        viewHolder.categoryName.setText(model.getCategoryName());

         fullcount = new Database(mContext).getOrderQuantity(
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
                    tempCartValues=new Database(mContext).getFullOrderQuantity(Common.currentUser.getPhone());
                    cartValues.setCartValues(tempCartValues);
                    viewHolder.btn_full_quick_cart.setVisibility(View.VISIBLE);
                    viewHolder.btn_full_quick_add.setVisibility(View.GONE);
                    fullcount = new Database(mContext).getOrderQuantity(
                            model.getKey(), Common.currentUser.getPhone(), "full");
                    viewHolder.btn_full_quick_cart.setNumber(String.valueOf(fullcount));

                } else {
                    reopenApp(mContext);
                }
            }
        });


        viewHolder.btn_full_quick_cart.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                if (currentUser != null) {

                    if (newValue > oldValue) {
                        addToCart(model, model.getFullprice(), viewHolder, "full");
                        tempCartValues=new Database(mContext).getFullOrderQuantity(Common.currentUser.getPhone());
                        cartValues.setCartValues(tempCartValues);
                    }
                    else if(newValue ==0) {
                        new Database(mContext).removeFromCart(model.getKey()
                                , currentUser.getPhone(), "full");

                        fullcount = new Database(mContext).getOrderQuantity(
                                model.getKey(), currentUser.getPhone(), "full");

                        if (fullcount > 0) {
                            viewHolder.btn_full_quick_add.setVisibility(View.GONE);
                            viewHolder.btn_full_quick_cart.setVisibility(View.VISIBLE);
                            viewHolder.btn_full_quick_cart.setNumber(String.valueOf(fullcount));
                        } else {
                            viewHolder.btn_full_quick_add.setVisibility(View.VISIBLE);
                            viewHolder.btn_full_quick_cart.setVisibility(View.GONE);
                        }
                        tempCartValues=new Database(mContext).getFullOrderQuantity(Common.currentUser.getPhone());
                        cartValues.setCartValues(tempCartValues);
                    }else{
                        new Database(mContext).decreaseQuantityFromCart(model.getKey()
                                , currentUser.getPhone(), "full");

                        fullcount = new Database(mContext).getOrderQuantity(
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
                        tempCartValues=new Database(mContext).getFullOrderQuantity(Common.currentUser.getPhone());
                        cartValues.setCartValues(tempCartValues);
                    }
                } else {
                    reopenApp(mContext);
                }

            }
        });

        /* viewHolder.btn_add_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer tempCartValues;
                addToCart(model, model.getFullprice(), viewHolder, "full");
                tempCartValues=new Database(mContext).getFullOrderQuantity(Common.currentUser.getPhone());
                cartValues.setCartValues(tempCartValues);
                //Toast.makeText(mContext.getBaseContext(), "Add button clicked", Toast.LENGTH_SHORT).show();

            }
        });*/


        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //start new activity
                /*Intent foodDetail =  new Intent(mContext, FoodDetail.class);
                foodDetail.putExtra("FoodId", model.getKey());
                mContext.startActivity(foodDetail);*/
            }
        });
        vegType = model.getVegType().toString();
        String tempStr = "true";
        if(vegType.equals(tempStr))
        {
            viewHolder.imgVegtype.setImageResource(R.drawable.veg);}
        else {  viewHolder.imgVegtype.setImageResource(R.drawable.non_veg);}
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
            filter = new MyFilter(this, foodFullList);
        }
        return filter;
    }
    private void addToCart(final Food model, final String price, final FoodViewHolder viewHolder, final String pricetype) {
        if (restaurantCartName.equals("") || restaurantCartName.equals(restaurantSelectedName)) {

            int count = new Database(mContext).checkFoodExists(
                    model.getKey(), currentUser.getPhone(), pricetype);


            restaurantCartName = restaurantSelectedName;
            restaurantCartPhone = restaurantSelectedPhone;
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

}
