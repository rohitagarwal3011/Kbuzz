package com.app.rohit.campk12_videochat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.opentok.Role;
import com.opentok.TokenOptions;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.opentok.exception.OpenTokException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.rohit.campk12_videochat.OpenTokConfig.API_KEY;
import static com.app.rohit.campk12_videochat.OpenTokConfig.SESSION_ID;
import static com.app.rohit.campk12_videochat.OpenTokConfig.TOKEN;
import static com.app.rohit.campk12_videochat.OpenTokConfig.generateToken;


public class MainActivity extends AppCompatActivity
                            implements EasyPermissions.PermissionCallbacks,
                                        WebServiceCoordinator.Listener,
                                        Session.SessionListener,
                                        PublisherKit.PublisherListener,
                                        SubscriberKit.SubscriberListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    // Suppressing this warning. mWebServiceCoordinator will get GarbageCollected if it is local.
    @SuppressWarnings("FieldCanBeLocal")
    private WebServiceCoordinator mWebServiceCoordinator;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    ImageView close;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize view objects from your layout
        mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);
        close = (ImageView) findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSession.disconnect();


            }
        });

        get_session_and_token();
        requestPermissions();




//        try {
//            Log.d("OpenTok :  ",OpenTokConfig.opentok.toString());
//
//
//
//
//           // generate_Session_id();
//
//
//           // Log.d("Session_id : ",OpenTokConfig.generated_session_id);
////            Log.d("Token : ", OpenTokConfig.generated_token);
////
////           // OpenTokConfig.SESSION_ID= OpenTokConfig.generated_session_id;
////            OpenTokConfig.TOKEN = OpenTokConfig.generated_token;
//
//        } catch (OpenTokException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
     //  requestPermissions();
    }


    @Override
    public void onBackPressed() {


//        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                .setTitleText("Are you sure?")
//                .setContentText("The call will be disconnected.")
//                .setCancelText("Cancel")
//                .setConfirmText("Yes,disconnect!")
//                .showCancelButton(true)
//                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.cancel();
//                        mSession.disconnect();
//                    }
//                })
//                .show();

    }

    public static void get_session_and_token()
    {
        OpenTokConfig.generated_session_id = HomeActivity.current_user.session_id;
        SESSION_ID = OpenTokConfig.generated_session_id;
        OpenTokConfig.generated_token = HomeActivity.current_user.token;
        TOKEN = OpenTokConfig.generated_token;
    }

    public  void generate_Session_id() throws OpenTokException, UnsupportedEncodingException {
        // A session that attempts to stream media directly between clients:
//        Session session = opentok.createSession();

// A session that uses the OpenTok Media Router (which is required for archiving):
//      Session session = opentok.createSession(new SessionProperties.Builder()
//                .mediaMode(MediaMode.RELAYED)
//                .build());

//// A Session with a location hint:
//        session = opentok.createSession(new SessionProperties.Builder()
//                .location("12.34.56.78")
//                .build());
//
//// A session that is automatically archived (it must used the routed media mode)
//         session = opentok.createSession(new SessionProperties.Builder()
//                .mediaMode(MediaMode.ROUTED)
//                .archiveMode(ArchiveMode.ALWAYS)
//                .build());

// Store this sessionId in the database for later use:
        //generated_session_id= session.getSessionId();

        String jwt = Jwts.builder()
                .setIssuer("46035482")
                .setIssuedAt(new Timestamp(System.currentTimeMillis()))
                .setExpiration(new Timestamp(System.currentTimeMillis()+((2 * 60) + 59) * 1000))
                .claim("ist", "project")
                .claim("jti", "jwt_nonce")
                .signWith(
                        SignatureAlgorithm.HS256,
                        "4461dcb871f5cf5f7ef62ad6ebcd7c37ce0f62d4".getBytes("UTF-8")
                )
                .compact();

        Log.d("TOken : " , jwt);

        final ApiServices apiServices = RetrofitClient.getApiService();

        Call<ResponseBody> call = apiServices.getsessionid("enabled","manual","application/json",jwt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                try {
//                    Gson gson = new Gson();
//                    JSONObject jsonObect = new JSONObject(gson.toJson(response));
                    JSONObject obj = new JSONArray(response.body().string()).getJSONObject(0);
                    Log.d("Response ",obj.toString());
                    OpenTokConfig.generated_session_id= obj.getString("session_id");
                    SESSION_ID = OpenTokConfig.generated_session_id;
                    String token = generateToken( new TokenOptions.Builder()
                            .role(Role.PUBLISHER)
                            .expireTime((System.currentTimeMillis() / 1000) + (7 * 24 * 60 * 60))
                            .build());

                    OpenTokConfig.generated_token = token;
                    TOKEN = OpenTokConfig.generated_token;



                    requestPermissions();


                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                } catch (OpenTokException e) {
                    e.printStackTrace();
                }


            }


            @Override
            public void onFailure(Call<ResponseBody> call1, Throwable t) {

            }
        });










    }



     /* Activity lifecycle methods */

    @Override
    protected void onPause() {

        Log.d(LOG_TAG, "onPause");

        super.onPause();

        if (mSession != null) {
            mSession.onPause();
        }

    }

    @Override
    protected void onResume() {

        Log.d(LOG_TAG, "onResume");

        super.onResume();

        if (mSession != null) {
            mSession.onResume();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        Log.d(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        Log.d(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private  void requestPermissions() {

        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // if there is no server URL set
            if (OpenTokConfig.CHAT_SERVER_URL == null) {
                // use hard coded session values
                Log.d("API KEy : ",API_KEY);
                Log.d("Session ID : ",SESSION_ID);
                Log.d("Token : ",TOKEN);
                if (OpenTokConfig.areHardCodedConfigsValid()) {
                    initializeSession(API_KEY, SESSION_ID, TOKEN);
                } else {
                    showConfigError("Configuration Error", OpenTokConfig.hardCodedConfigErrorMessage);
                }
            } else {
                // otherwise initialize WebServiceCoordinator and kick off request for session data
                // session initialization occurs once data is returned, in onSessionConnectionDataReady
                if (OpenTokConfig.isWebServerConfigUrlValid()) {
                    mWebServiceCoordinator = new WebServiceCoordinator(this, this);
                    mWebServiceCoordinator.fetchSessionConnectionData(OpenTokConfig.SESSION_INFO_ENDPOINT);
                } else {
                    showConfigError("Configuration Error", OpenTokConfig.webServerConfigErrorMessage);
                }
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }
    }

    private void initializeSession(String apiKey, String sessionId, String token) {

        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.connect(token);
    }

    /* Web Service Coordinator delegate methods */

    @Override
    public void onSessionConnectionDataReady(String apiKey, String sessionId, String token) {

        Log.d(LOG_TAG, "ApiKey: "+apiKey + " SessionId: "+ sessionId + " Token: "+token);
        initializeSession(apiKey, sessionId, token);
    }

    @Override
    public void onWebServiceCoordinatorError(Exception error) {

        Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
        Toast.makeText(this, "Web Service error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        finish();

    }

    /* Session Listener methods */

    @Override
    public void onConnected(Session session) {

        Log.d(LOG_TAG, "onConnected: Connected to session: "+session.getSessionId());

        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        // set publisher video style to fill view
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        mPublisherViewContainer.addView(mPublisher.getView());
        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {

        Log.d(LOG_TAG, "onDisconnected: Disconnected from session: "+session.getSessionId());

        HomeActivity.onSessionDisconnected();

        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();



    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.d(LOG_TAG, "onStreamReceived: New Stream Received "+stream.getStreamId() + " in session: "+session.getSessionId());

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            mSubscriber.setSubscriberListener(this);
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        Log.d(LOG_TAG, "onStreamDropped: Stream Dropped: "+stream.getStreamId() +" in session: "+session.getSessionId());

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
        mSession.disconnect();




    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: "+ opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - "+opentokError.getMessage() + " in session: "+ session.getSessionId());

        showOpenTokError(opentokError);
    }

    /* Publisher Listener methods */

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {


        Log.d(LOG_TAG, "onStreamCreated: Publisher Stream Created. Own stream "+stream.getStreamId());

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

        Log.d(LOG_TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream "+stream.getStreamId());
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

        Log.e(LOG_TAG, "onError: "+opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() +  " - "+opentokError.getMessage());

        showOpenTokError(opentokError);
    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {

//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
//                getResources().getDisplayMetrics().widthPixels, getResources()
//                .getDisplayMetrics().heightPixels);
//        mSubscriberViewContainer.addView(mSubscriber.getView(), layoutParams);
        Log.d(LOG_TAG, "onConnected: Subscriber connected. Stream: "+subscriberKit.getStream().getStreamId());
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {

        mSession.disconnect();

        Log.d(LOG_TAG, "onDisconnected: Subscriber disconnected. Stream: "+subscriberKit.getStream().getStreamId());
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {

        Log.e(LOG_TAG, "onError: "+opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() +  " - "+opentokError.getMessage());

        showOpenTokError(opentokError);
    }

    private void showOpenTokError(OpentokError opentokError) {

        Toast.makeText(this, opentokError.getErrorDomain().name() +": " +opentokError.getMessage() + " Please, see the logcat.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showConfigError(String alertTitle, final String errorMessage) {
        Log.e(LOG_TAG, "Error " + alertTitle + ": " + errorMessage);
        new AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(errorMessage)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
