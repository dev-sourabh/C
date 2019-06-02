package com.foodcubo.foodcubo.android.ViewHolder;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.R;
import com.foodcubo.foodcubo.foodcubo.Cart;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Model.Order;
import com.foodcubo.foodcubo.foodcubo.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;




public class CartAdapter extends RecyclerView.Adapter<com.foodcubo.foodcubo.foodcubo.ViewHolder.CartViewHolder>{

    private final NumberFormat fmt;
    private List<Order> listData;
    private Cart cart;
    private RelativeLayout rootLayout;

    public CartAdapter(RelativeLayout rootLayout,List<Order> listData, Cart cart){
        this.listData = listData;
        this.cart= cart;
        this.rootLayout= rootLayout;
        Locale locale = new Locale("en-IN","IN");
        fmt = NumberFormat.getCurrencyInstance(locale);
    }

    @NonNull
    @Override
    public com.foodcubo.foodcubo.foodcubo.ViewHolder.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new com.foodcubo.foodcubo.foodcubo.ViewHolder.CartViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, @SuppressLint("RecyclerView") final int position) {
//
        final int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        Picasso.with(cart.getBaseContext())
                //changed by sonal back to old version Picasso.get()
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(holder.cart_image);
        System.out.println("PPPPPPPPPPPPPPPPP..............."+listData.get(position).getPieceType());
        if(listData.get(position).getPieceType().equals("true")){
            holder.cart_item_type.setVisibility(View.VISIBLE);
            if(listData.get(position).getPriceType().equals("full"))
                holder.cart_item_type.setText("Full Plate");
            if(listData.get(position).getPriceType().equals("half"))
                holder.cart_item_type.setText("Half Plate");
        }else{
            holder.cart_item_type.setVisibility(View.INVISIBLE);
        }

        holder.btn_quantity.setNumber(listData.get(position).getQuantity());

        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                if(Common.currentUser!=null){
                    final Order order = listData.get(position);
                    if (newValue == 0) {
                        removeItem(position);
                        new Database(cart)
                                .removeFromCart(order.getProductId(), Common.currentUser.getPhone(),order.getPriceType());

                        holder.txt_price.setText(fmt.format((Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()))));

                        int total = 0;

                        for (Order item : listData)
                            total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                        cart.tv_itemtotal.setText(fmt.format(total));
                        // add tax, profit to total, do we need to show the tax and profit on the app??
                        float tax;
                        if (total > 0 && total <= 99) {
                            tax = 35;
                        } else if (total >= 100 && total <= 199) {
                            tax = 25;
                        } else if (total >= 200 && total <= 299) {
                            tax = 15;
                        } else {
                            tax = 0;
                        }
                        if (tax == 0)
                            cart.tv_deliverycharges.setText("Free");
                        else
                            cart.tv_deliverycharges.setText(fmt.format(tax));

                        float profit = (float) (total * 0.05);

                        cart.tv_gstcharges.setText(fmt.format(profit));

                        total += tax + profit;


                        cart.txtTotalPrice.setText(fmt.format(total));
                        cart.tv_total.setText(fmt.format(total));

                        Snackbar snackBar = Snackbar.make(rootLayout, order.getProductName() + " removed from cart !!!!!!", Snackbar.LENGTH_LONG);

                        snackBar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                restoreItem(order, position);

                                holder.txt_price.setText(fmt.format((Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()))));

                                listData.set(position, order);
                                new Database(cart).addToCart(order);
                                int total = 0;

                                for (Order item : listData)
                                    total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                                cart.tv_itemtotal.setText(fmt.format(total));

                                // add tax, profit to total, do we need to show the tax and profit on the app??
                                float tax;
                                if (total > 0 && total <= 99) {
                                    tax = 35;
                                } else if (total >= 100 && total <= 199) {
                                    tax = 25;
                                } else if (total >= 200 && total <= 299) {
                                    tax = 15;
                                } else {
                                    tax = 0;
                                }
                                if (tax == 0)
                                    cart.tv_deliverycharges.setText("Free");
                                else
                                    cart.tv_deliverycharges.setText(fmt.format(tax));

                                float profit = (float) (total * 0.05);

                                cart.tv_gstcharges.setText(fmt.format(profit));

                                total += tax + profit;

                                cart.txtTotalPrice.setText(fmt.format(total));
                                cart.tv_total.setText(fmt.format(total));

                                if (new Database(cart).getCountCart(Common.currentUser.getPhone()) == 0) {
                                    Common.restaurantCartName = "";
                                    Common.restaurantCartPhone = "";
                                    cart.tvEmptyCart.setVisibility(View.VISIBLE);
                                    cart.cartLayout.setVisibility(View.GONE);
                                    cart.address_layout.setVisibility(View.GONE);
                                    cart.bill_bottom_layout.setVisibility(View.GONE);
                                } else {
                                    cart.tvEmptyCart.setVisibility(View.GONE);
                                    cart.cartLayout.setVisibility(View.VISIBLE);
                                    cart.address_layout.setVisibility(View.VISIBLE);
                                    cart.bill_bottom_layout.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        snackBar.setActionTextColor(Color.YELLOW);
                        snackBar.show();
                        if (new Database(cart).getCountCart(Common.currentUser.getPhone()) == 0) {
                            Common.restaurantCartName = "";
                            Common.restaurantCartPhone = "";
                            cart.tvEmptyCart.setVisibility(View.VISIBLE);
                            cart.cartLayout.setVisibility(View.GONE);
                            cart.address_layout.setVisibility(View.GONE);
                            cart.bill_bottom_layout.setVisibility(View.GONE);
                        } else {
                            cart.tvEmptyCart.setVisibility(View.GONE);
                            cart.cartLayout.setVisibility(View.VISIBLE);
                            cart.address_layout.setVisibility(View.VISIBLE);
                            cart.bill_bottom_layout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        order.setQuantity(String.valueOf(newValue));
                        listData.set(position, order);
                        holder.txt_price.setText(fmt.format((Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()))));
                        new Database(cart).updateCart(order);

                        int total = 0;
                        for (Order item : listData)
                            total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));


                        cart.tv_itemtotal.setText(fmt.format(total));
                        // add tax, profit to total, do we need to show the tax and profit on the app??
                        float tax;
                        if (total > 0 && total <= 99) {
                            tax = 35;
                        } else if (total >= 100 && total <= 199) {
                            tax = 25;
                        } else if (total >= 200 && total <= 299) {
                            tax = 15;
                        } else {
                            tax = 0;
                        }
                        if (tax == 0)
                            cart.tv_deliverycharges.setText("Free");
                        else
                            cart.tv_deliverycharges.setText(fmt.format(tax));

                        float profit = (float) (total * 0.05);

                        cart.tv_gstcharges.setText(fmt.format(profit));

                        total += tax + profit;

                        cart.txtTotalPrice.setText(fmt.format(total));
                        cart.tv_total.setText(fmt.format(total));

                    }
                }else{
                    Common.reopenApp(cart);
                }




            }
        });

        holder.txt_price.setText(fmt.format(price));
        String prodName = listData.get(position).getProductName();
        holder.txt_cart_name.setText(prodName);

        if(listData.get(position).getVegType()!=null)
            if(listData.get(position).getVegType().equals("true"))
                holder.image_cart_isveg.setColorFilter(
                        ContextCompat.getColor(cart, android.R.color.holo_green_dark),
                        PorterDuff.Mode.SRC_IN);
            else
                holder.image_cart_isveg.setColorFilter(
                        ContextCompat.getColor(cart, android.R.color.holo_red_dark),
                        PorterDuff.Mode.SRC_IN);

    }

    @Override
    public int getItemCount() {

        return listData.size();
    }

    public Order getItem(int position){
        return  listData.get(position);
    }

    public  void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public  void restoreItem(Order item,int position){
        Common.restaurantCartName=item.getRestaurantName();
        Common.restaurantCartPhone=item.getRestaurantPhone();
        listData.add(position,item);
        notifyItemInserted(position);
    }
}
