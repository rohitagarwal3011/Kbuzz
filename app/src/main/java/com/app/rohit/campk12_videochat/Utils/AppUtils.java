package com.app.rohit.campk12_videochat.Utils;

import android.util.Log;
import android.widget.Toast;

import com.app.rohit.campk12_videochat.Activities.HomeActivity;
import com.app.rohit.campk12_videochat.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rohit on 18/1/18.
 */

public class AppUtils {


    private DatabaseReference mDatabase;
    private static DatabaseReference users;

    public static String current_user_id;
    public static String current_email;
    public static User current_user;

    public static void get_current_user()
    {

        users= FirebaseDatabase.getInstance().getReference("users");

        current_user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        current_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public static void set_session_connected(final User user_selected)
    {
        get_current_user();
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.d("Set session "," : true");
                users.child(current_user_id).child("session_id").setValue(OpenTokConfig.generated_session_id);
                users.child(current_user_id).child("token").setValue(OpenTokConfig.generated_token);
                users.child(user_selected.userid).child("session_id").setValue(OpenTokConfig.generated_session_id);
                users.child(user_selected.userid).child("token").setValue(OpenTokConfig.generated_token);

                users.child(current_user_id).child("connected").setValue(true);
                users.child(user_selected.userid).child("connected").setValue(true);




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




}
