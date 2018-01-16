package com.app.rohit.campk12_videochat;

/**
 * Created by rohit on 10/1/18.
 */

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {

    public String userid;
    public String email;
    public String invite_sent_to;
    public String invite_recieved_from;
    public String session_id;
    public String token;
    public Boolean connected;
    public String name;
    public String gcm_id;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userid, String email) {
        this.userid = userid;
        this.email = email;
    }



}