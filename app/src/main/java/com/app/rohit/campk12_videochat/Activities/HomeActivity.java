package com.app.rohit.campk12_videochat.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.app.rohit.campk12_videochat.Services.ApiServices;
import com.app.rohit.campk12_videochat.Services.FcmNotificationBuilder;
import com.app.rohit.campk12_videochat.Services.FirebaseServices;
import com.app.rohit.campk12_videochat.Utils.AppUtils;
import com.app.rohit.campk12_videochat.Utils.Constant;
import com.app.rohit.campk12_videochat.Services.FirebaseClient;
import com.app.rohit.campk12_videochat.Utils.OpenTokConfig;
import com.app.rohit.campk12_videochat.R;
import com.app.rohit.campk12_videochat.Services.RetrofitClient;
import com.app.rohit.campk12_videochat.Models.User;
import com.app.rohit.campk12_videochat.Adapters.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.opentok.Role;
import com.opentok.TokenOptions;
import com.opentok.exception.OpenTokException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.rohit.campk12_videochat.Utils.OpenTokConfig.SESSION_ID;
import static com.app.rohit.campk12_videochat.Utils.OpenTokConfig.TOKEN;
import static com.app.rohit.campk12_videochat.Utils.OpenTokConfig.generateToken;
import static com.app.rohit.campk12_videochat.Utils.OpenTokConfig.generated_session_id;

public class HomeActivity extends BaseActivity {


    private DatabaseReference mDatabase;
    private static DatabaseReference users;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    final List<User> listofuser = new ArrayList<>();

    public static String current_user_id;
    public static String current_email;
    public static User current_user;
    public static User current_chat_user;

    TextView username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        username = (TextView)findViewById(R.id.username);


        users= FirebaseDatabase.getInstance().getReference("users");

        current_user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        current_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        String value=sharedPreferences.getString(Constant.GCM_ID, "");
        Log.d("GCM token",value);
        Log.d("GCM : ",FirebaseInstanceId.getInstance().getToken());
        String gcmid = value;
        users.child(current_user_id).child("gcm_id").setValue(FirebaseInstanceId.getInstance().getToken());



        //writeNewUser(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getEmail());
         }

    @Override
    protected void onResume() {
        super.onResume();
        set_recycler_view();

        //set_recycler_view();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.signout :
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void writeNewUser(final String userId, final String email) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.hasChild(userId)) {
                    // run some code

                    Toast.makeText(HomeActivity.this,"User already exist",Toast.LENGTH_LONG).show();
                }
                else
                {
                    users.push().child(userId);
                    users.child(userId).child("email").setValue(email);
                    users.child(userId).child("connected").setValue(false);
                    users.child(userId).child("invite_sent_to").setValue("");
                    users.child(userId).child("invite_recieved_from").setValue("");
                    Toast.makeText(HomeActivity.this,"New User Added",Toast.LENGTH_LONG).show();

                }

                for (DataSnapshot noteSnapshot: snapshot.getChildren()){
                    User user = noteSnapshot.getValue(User.class);
                    user.userid= noteSnapshot.getKey();
                    Log.d("User id : ",user.userid);
                    if(!noteSnapshot.getKey().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        listofuser.add(user);
                    else
                        current_user = user;


                }





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    ValueEventListener valueEventListener;
    private void set_recycler_view()
    {

        listofuser.clear();

       valueEventListener=  users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                listofuser.clear();
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    User user = noteSnapshot.getValue(User.class);
                    user.userid= noteSnapshot.getKey();
                    Log.d("User id : ",user.userid);
                    if(!noteSnapshot.getKey().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        listofuser.add(user);
                    else
                        current_user = user;


                }
//                Log.d("User Adapter : ","Notify change");
//                if(current_user.invite_recieved_from.length()>0)
//                {
//
//                    final String gcm = "cYLv2FYNBvk:APA91bF-28wSjmP-YZ8AH3bDUMwiwxRl6pYhPVvHy6FA2Dhj-Yj5sK_INOAog7z68QJFyHFX927qmbCeDu4fYcLWLnm6InfcSNQtgX3pQgOsVaCT8fLVuuXOCVnezQGPQovgYVEZea-h";
//
//                    sendNotification(gcm);
//                    // users.push().setValue("hello");
//
//                }
                load_recycler();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Home Activity", databaseError.getMessage());
            }
        });

    }






    private void load_recycler()
    {
        username.setText(current_user.name+" (Logged In)");

//        Log.d("User Name ", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        Log.d("Users : ",listofuser.toString());

        userAdapter = new UserAdapter(HomeActivity.this, listofuser);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
       // recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(HomeActivity.this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    public void on_invite_clicked(User user_selected,Boolean video) throws JSONException {
        users.child(current_user_id).child("invite_sent_to").setValue(user_selected.email);
        users.child(user_selected.userid).child("invite_recieved_from").setValue(current_email);
        users.child(current_user_id).child("video").setValue(video);
        users.child(user_selected.userid).child("video").setValue(video);
        try {
            send_notification(user_selected);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
       // send_notification();
        //userAdapter.notifyDataSetChanged();
        //send_notification();
    }

    public void on_invite_accepted(User user_selected) throws UnsupportedEncodingException, OpenTokException {

        Log.d("HomeActivity : ","Invite Accepted");
        OpenTokConfig.generate_Session_id(user_selected);
//        if(OpenTokConfig.generate_Session_id()) {
//            Log.d("Session id : ","generated");
//            AppUtils.set_session_connected(user_selected);
//        Intent intent = new Intent(HomeActivity.this, VideocallActivity.class);
//        startActivity(intent);

    }
    public void on_invite_cancel(User user_selected)
    {
        users.child(current_user_id).child("invite_sent_to").setValue("");
        users.child(user_selected.userid).child("invite_recieved_from").setValue("");
       // userAdapter.notifyDataSetChanged();
    }

    public void on_reject_invite(User user_selected)
    {

        users.child(user_selected.userid).child("invite_sent_to").setValue("");
        users.child(current_user_id).child("invite_recieved_from").setValue("");
    }

    public void on_user_selected(User user_selected)
    {

        current_chat_user=user_selected;
        ChatActivity.startActivity(HomeActivity.this,user_selected.name,user_selected.userid,user_selected.gcm_id);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Remove Lister : ","Done ");
        users.removeEventListener(valueEventListener);
    }
//
//    public  void generate_Session_id(final User user_selected) throws OpenTokException, UnsupportedEncodingException {
//        // A session that attempts to stream media directly between clients:
////        Session session = opentok.createSession();
//
//// A session that uses the OpenTok Media Router (which is required for archiving):
////      Session session = opentok.createSession(new SessionProperties.Builder()
////                .mediaMode(MediaMode.RELAYED)
////                .build());
//
////// A Session with a location hint:
////        session = opentok.createSession(new SessionProperties.Builder()
////                .location("12.34.56.78")
////                .build());
////
////// A session that is automatically archived (it must used the routed media mode)
////         session = opentok.createSession(new SessionProperties.Builder()
////                .mediaMode(MediaMode.ROUTED)
////                .archiveMode(ArchiveMode.ALWAYS)
////                .build());
//
//// Store this sessionId in the database for later use:
//        //generated_session_id= session.getSessionId();
//
//        String jwt = Jwts.builder()
//                .setIssuer("46035482")
//                .setIssuedAt(new Timestamp(System.currentTimeMillis()))
//                .setExpiration(new Timestamp(System.currentTimeMillis()+((2 * 60) + 59) * 1000))
//                .claim("ist", "project")
//                .claim("jti", "jwt_nonce")
//                .signWith(
//                        SignatureAlgorithm.HS256,
//                        "4461dcb871f5cf5f7ef62ad6ebcd7c37ce0f62d4".getBytes("UTF-8")
//                )
//                .compact();
//
//        Log.d("TOken : " , jwt);
//
//        final ApiServices apiServices = RetrofitClient.getApiService();
//
//        Call<ResponseBody> call = apiServices.getsessionid("enabled","manual","application/json",jwt);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//
//                try {
////                    Gson gson = new Gson();
////                    JSONObject jsonObect = new JSONObject(gson.toJson(response));
//                    JSONObject obj = new JSONArray(response.body().string()).getJSONObject(0);
//                    Log.d("Response ",obj.toString());
//                    OpenTokConfig.generated_session_id= obj.getString("session_id");
//                    SESSION_ID = OpenTokConfig.generated_session_id;
//                    String token = generateToken( new TokenOptions.Builder()
//                            .role(Role.PUBLISHER)
//                            .expireTime((System.currentTimeMillis() / 1000) + (7 * 24 * 60 * 60))
//                            .build());
//
//                    OpenTokConfig.generated_token = token;
//                    TOKEN = OpenTokConfig.generated_token;
//
//                   on_invite_accepted(user_selected);
//
//                   // requestPermissions();
//
//
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//                catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (OpenTokException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//
//            @Override
//            public void onFailure(Call<ResponseBody> call1, Throwable t) {
//
//            }
//        });
//
//
//
//
//
//
//
//
//
//
//    }
//

    public static void onSessionDisconnected()
    {
        users.child(current_user_id).child("session_id").setValue("");
        users.child(current_user_id).child("token").setValue("");
        users.child(current_user_id).child("connected").setValue(false);
        users.child(current_user_id).child("invite_recieved_from").setValue("");
        users.child(current_user_id).child("invite_sent_to").setValue("");

    }






    public void send_notification(final User user) throws JSONException {


        FcmNotificationBuilder.initialize()
                .title(current_user.name)
                .message("Incoming Connection Request")
                .username(current_user.name)
                .uid(current_user.userid)
                .firebaseToken(current_user.gcm_id)
                .receiverFirebaseToken(user.gcm_id)
                .send();

//        final FirebaseServices apiServices = FirebaseClient.getApiService();
//
//        String gcm = "eVG76xOMhOk:APA91bFNRD-B7tETywXK3aG0UFzecQQKTSN-MYF5YB-ZtJeSDlblYnPJ_dCCHBRqhorbtDbo6sA960Iu6G2ky6Th3LUgAulaF-gJh1_8Q-rH4YBFlBfTb2SaJwF-VPdnlIFcCVLiHDDY";
//        JSONObject dataJson=new JSONObject();
//        JSONObject json=new JSONObject();
//        dataJson.put("body","Hi this is sent from device to device");
//        dataJson.put("title","dummy title");
//        json.put("data",dataJson);
//        json.put("to",gcm);
//        JSONObject payload = new JSONObject();
//        payload.put("payload",json);
//        Log.d("Payload",payload.toString());
//
//        Call<ResponseBody> call = apiServices.sendnotification(user.gcm_id,user.name);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//
//                try {
////                    Gson gson = new Gson();
////                    JSONObject jsonObect = new JSONObject(gson.toJson(response));
//                    JSONObject obj = new JSONObject(response.body().string());
//                    Log.d("Response ",obj.toString());
//
//                    // requestPermissions();
//
//
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//                catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//
//            @Override
//            public void onFailure(Call<ResponseBody> call1, Throwable t) {
//
//            }
//        });

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}
