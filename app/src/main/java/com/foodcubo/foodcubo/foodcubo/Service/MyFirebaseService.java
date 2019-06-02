package com.foodcubo.foodcubo.foodcubo.Service;

import com.foodcubo.foodcubo.android.Common.Common;
import com.foodcubo.foodcubo.foodcubo.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String tokenRefreshed=FirebaseInstanceId.getInstance().getToken();

        if(Common.currentUser!=null)
        updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token = new Token(tokenRefreshed,false);
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
