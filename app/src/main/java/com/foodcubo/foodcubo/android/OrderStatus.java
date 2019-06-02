package com.foodcubo.foodcubo.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.android.Model.Request;
import com.foodcubo.foodcubo.android.ViewHolder.OrderViewHolder;
import com.foodcubo.foodcubo.foodcubo.Interface.ItemClickListener;
import com.foodcubo.foodcubo.foodcubo.TrackingOrder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.foodcubo.foodcubo.android.Common.Common.convertCodeToStatus;

//import com.foodcubo.foodcubo.foodcubo.Common.Common;

@SuppressLint("Registered")
public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        if(getIntent().getExtras()==null)
//        loadOrders(Common.currentUser.getPhone());
//        else
//            loadOrders(getIntent().getStringExtra("userPhone"));
        if(Common.currentUser!=null){
         if(getIntent() ==null)
            loadOrders(Common.currentUser.getPhone());
        else {
            if(getIntent().getStringExtra("userPhone") == null)
                loadOrders(Common.currentUser.getPhone());
            else
                loadOrders(getIntent().getStringExtra("userPhone"));
        }

        }else{
            Common.reopenApp(OrderStatus.this);
        }




        }

    private void loadOrders(String phone) {
        Query getOrderByUser=requests.orderByChild("phone")
                .equalTo(phone);

        FirebaseRecyclerOptions<Request> requestOptions=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrderByUser,Request.class)
                .build();
        adapter =new FirebaseRecyclerAdapter<Request, OrderViewHolder>(requestOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, @SuppressLint("RecyclerView") final int position, @NonNull final Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderphone.setText(model.getPhone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(!model.getStatus().equals("0")) {
                            Common.currentKey = adapter.getRef(position).getKey();
                            Intent i = new Intent(OrderStatus.this, TrackingOrder.class);
                            i.putExtra("phonenumber", model.getTempShipper());
                            startActivity(i);
                        }else{
                            Toast.makeText(getApplicationContext(),"Shipper Not Yet Assigned",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(adapter.getItem(position).getStatus().equals("0"))
                            deleteOrder(adapter.getRef(position).getKey());
                        else
                            Toast.makeText(OrderStatus.this, "You cannot delete this order!!", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void deleteOrder(final String key) {
        requests.child(key)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(OrderStatus.this, "Order" +
                        key +
                        " has been deleted !!!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderStatus.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
