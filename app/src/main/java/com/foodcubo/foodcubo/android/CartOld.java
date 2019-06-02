package com.foodcubo.foodcubo.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.Remote.APIService;
import com.foodcubo.foodcubo.android.ViewHolder.CartAdapter;
import com.foodcubo.foodcubo.foodcubo.Database.Database;
import com.foodcubo.foodcubo.foodcubo.Helper.RecyclerItemTouchHelper;
import com.foodcubo.foodcubo.foodcubo.Interface.RecyclerItemTouchHelperListener;
import com.foodcubo.foodcubo.foodcubo.Model.Order;
import com.foodcubo.foodcubo.foodcubo.Remote.IGoogleService;
import com.foodcubo.foodcubo.foodcubo.ViewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.foodcubo.foodcubo.android.R.drawable;
import static com.foodcubo.foodcubo.android.R.id;
import static com.foodcubo.foodcubo.android.R.layout;
import static com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog;
import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;
import static com.google.android.gms.common.GooglePlayServicesUtil.isUserRecoverableError;

// import com.foodcubo.foodcubo.focus.Common.Common;


@SuppressLint("Registered")
public class CartOld extends AppCompatActivity implements RecyclerItemTouchHelperListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    String address = " sdpt ";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests, requests1;

    public TextView txtTotalPrice, tvListRestaurant, tvEmptyCart;
    public RelativeLayout cartLayout;
    Button btnPlace;
    float totalPrice;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    static List<List<Order>> orderList = new ArrayList<>();
    static float total;

    RelativeLayout rootLayout;

    APIService mService;
    IGoogleService mGoogleMapService;

    //Location
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;
    private static final int LOCATION_REQUEST_CODE = 9999;
    private static final int PLAY_SERVICES_REQUEST = 9997;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_cart);

        //permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_REQUEST_CODE);

        } else {
            if (checkPlayService()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }
        mGoogleMapService = Common.getGoogleMapAPI();
        mService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Restaurants").child(Common.restaurantSelected).child("Requests");
        requests1 = database.getReference("Requests");

        //Init
        recyclerView = findViewById(id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = findViewById(id.rootLayout);
        //swipe  delete item
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        tvEmptyCart = findViewById(id.tv_empty_cart);
        cartLayout = findViewById(id.cart_layout);
        tvListRestaurant = findViewById(id.listRestaurant);
        txtTotalPrice = findViewById(id.total);
        btnPlace = findViewById(id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(CartOld.this, "Your cart is empty!!!", Toast.LENGTH_SHORT).show();

            }
        });

        loadListFood();

    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    private boolean checkPlayService() {
        int resultCode = isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (isUserRecoverableError(resultCode)) {
                getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported !!", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartOld.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address");

        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View order_address_comment = inflater.inflate(layout.order_address_comment, null);


       final PlaceAutocompleteFragment edtAddress;
        edtAddress = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        edtAddress.getView().findViewById(id.place_autocomplete_search_button).setVisibility(View.GONE);

        ((EditText) edtAddress.getView().findViewById(id.place_autocomplete_search_input))
                .setHint("Enter your address Manually");

        ((EditText) edtAddress.getView().findViewById(id.place_autocomplete_search_input))
                .setTextSize(14);

        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
            }

            @Override
            public void onError(Status status) {

                Log.e("ERROR", status.getStatusMessage());
            }
        });


        final RadioButton rdiShipToAddress = order_address_comment.findViewById(id.rdiShipToAddress);
        final RadioButton rdiHomeAddress = order_address_comment.findViewById(id.rdiHomeAddress);


        rdiShipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mGoogleMapService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()))
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());

                                        JSONArray resultsArray = jsonObject.getJSONArray("results");
                                        JSONObject firstObject = resultsArray.getJSONObject(0);
                                        address = firstObject.getString("formatted_address");
                                        ((EditText) edtAddress.getView().findViewById(id.place_autocomplete_search_input))
                                                .setText(address);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(CartOld.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        rdiHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!TextUtils.isEmpty(Common.currentUser.getHomeAddress()) || Common.currentUser.getHomeAddress() != null) {
                        address = Common.currentUser.getHomeAddress();
                        ((EditText) edtAddress.getView().findViewById(id.place_autocomplete_search_input))
                                .setText(address);
                    } else {
                        Toast.makeText(CartOld.this, "Please update your home address", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(drawable.ic_shopping_cart_black_24dp);


        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                getSupportFragmentManager().beginTransaction()
                        .remove(getSupportFragmentManager().findFragmentById(id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialog.show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayService()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                    }
                }
            }
        }
    }

/*
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
Request request=new Request(
Common.currentUser.getPhone(),
Common.currentUser.getName(),
address,
txtTotalPrice.getText().toString(),
"0",
comment,
String.format("%s %s",shippingAddress.getLatLng().longitude,shippingAddress.getLatLng().latitude),
cart
);

String order_number=String.valueOf(System.currentTimeMillis());
requests.child(order_number)
.setValue(request);

new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
sendNotificationOrder(order_number);
Toast.makeText(Cart.this,"Thank you order placed",Toast.LENGTH_SHORT).show();
finish();
}
*/


    private void loadListFood() {
        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        orderList.add(cart);
        System.out.println("dede........hyhy............" + cart.size() + "....." + cart);
        if (cart.size() != 0) {
            tvEmptyCart.setVisibility(View.GONE);
            cartLayout.setVisibility(View.VISIBLE);
            tvListRestaurant.setText(cart.get(0).getRestaurantName());
        } else {
            tvEmptyCart.setVisibility(View.VISIBLE);
            cartLayout.setVisibility(View.GONE);
        }
//        adapter = new CartAdapter(rootLayout, cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate total price
        total = 0;
        for (Order order : cart)
            total += (float) (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);


        // add tax, profit to total, do we need to show the tax and profit on the app??
        float tax = (float) (total * 0.06);
        float profit = (float) (total * 0.3);
        total += tax + profit;

        totalPrice = total;

        txtTotalPrice.setText(fmt.format(total));

    }

    //Delete item


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(this).cleanCart(Common.currentUser.getPhone());

        for (Order item : cart)
            new Database(this).addToCart(item);

        loadListFood();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter) Objects.requireNonNull(recyclerView.getAdapter())).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext())
                    .removeFromCart(deleteItem.getProductId(), Common.currentUser.getPhone(), deleteItem.getPriceType());

            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            //  List<Order> orders=new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for (Order item : orders)
                total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("en", "US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);


            // add tax, profit to total, do we need to show the tax and profit on the app??
            float tax = (float) (total * 0.06);
            float profit = (float) (total * 0.3);
            total += tax + profit;

            txtTotalPrice.setText(fmt.format(total));

            Snackbar snackBar = Snackbar.make(rootLayout, name + " removed from cart !!!!!!", Snackbar.LENGTH_LONG);

            snackBar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);
                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    //  List<Order> orders=new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for (Order item : orders)
                        total += (float) (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                    Locale locale = new Locale("en", "US");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);


                    // add tax, profit to total, do we need to show the tax and profit on the app??
                    float tax = (float) (total * 0.06);
                    float profit = (float) (total * 0.3);
                    total += tax + profit;

                    txtTotalPrice.setText(fmt.format(total));

                    if (new Database(CartOld.this).getCountCart(Common.currentUser.getPhone()) == 0) {
                        Common.restaurantCartName = "";
                        Common.restaurantCartPhone = "";
                        tvEmptyCart.setVisibility(View.VISIBLE);
                        cartLayout.setVisibility(View.GONE);
                    } else {
                        tvEmptyCart.setVisibility(View.GONE);
                        cartLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
            snackBar.setActionTextColor(Color.YELLOW);
            snackBar.show();
            if (new Database(CartOld.this).getCountCart(Common.currentUser.getPhone()) == 0) {
                Common.restaurantCartName = "";
                Common.restaurantCartPhone = "";
                tvEmptyCart.setVisibility(View.VISIBLE);
                cartLayout.setVisibility(View.GONE);
            } else {
                tvEmptyCart.setVisibility(View.GONE);
                cartLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.e("LOCATION", "your location : " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        } else {
            Log.e("LOCATION", "Could not get your location");
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }
}
