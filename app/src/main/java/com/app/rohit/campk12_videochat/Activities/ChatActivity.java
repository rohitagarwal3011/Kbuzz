package com.app.rohit.campk12_videochat.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rohit.campk12_drawnshare.MainActivity;
import com.app.rohit.campk12_videochat.Adapters.ChatRecyclerAdapter;
import com.app.rohit.campk12_videochat.ChatUtils.ChatContract;
import com.app.rohit.campk12_videochat.ChatUtils.ChatPresenter;
import com.app.rohit.campk12_videochat.Models.Chat;
import com.app.rohit.campk12_videochat.R;
import com.app.rohit.campk12_videochat.Utils.Constant;
import com.app.rohit.campk12_videochat.app.AppController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements ChatContract.View, TextView.OnEditorActionListener {

    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    private static DatabaseReference users;

   private FirebaseStorage storage ;

    StorageReference images;


    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setTitle(getIntent().getStringExtra(Constant.ARG_RECEIVER));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        mRecyclerViewChat = (RecyclerView)findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText)findViewById(R.id.edit_text_message);

        mETxtMessage.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getIntent().getStringExtra(Constant.ARG_RECEIVER_UID));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.audiocall :



            case R.id.videocall:



            case R.id.paint:

                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                startActivityForResult(intent,2);


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(resultCode==2)
        {

            String message=data.getStringExtra("URI");
            Log.d("ChatActivity"," file uri : "+message);

            images=storageRef.child("images");

            UploadTask uploadTask;
            Uri file = Uri.fromFile(new File(message));
            StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
            uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    sendMessageImage(downloadUrl.toString());

                }
            });

        }
        else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.setChatActivityOpen(false);
    }

    public static void startActivity(Context context,
                                     String receiver,
                                     String receiverUid,
                                     String firebaseToken) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constant.ARG_RECEIVER, receiver);
        intent.putExtra(Constant.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constant.ARG_FIREBASE_TOKEN, firebaseToken);
        context.startActivity(intent);
    }

    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>(),ChatActivity.this);
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
    }

//    @Subscribe
//    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
//        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
//            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                    pushNotificationEvent.getUid());
//        }
//    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessageText();
            return true;
        }
        return false;
    }

    private void sendMessageText() {
        String message = mETxtMessage.getText().toString();
        String receiver = getIntent().getStringExtra(Constant.ARG_RECEIVER);
        String receiverUid = getIntent().getStringExtra(Constant.ARG_RECEIVER_UID);
        String sender = HomeActivity.current_user.name;
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String type="Text";
        String receiverFirebaseToken = getIntent().getStringExtra(Constant.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                type,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(getApplicationContext(),
                chat,
                receiverFirebaseToken);
    }

    private void sendMessageImage(String url) {
        String message = url;
        String receiver = getIntent().getStringExtra(Constant.ARG_RECEIVER);
        String receiverUid = getIntent().getStringExtra(Constant.ARG_RECEIVER_UID);
        String sender = HomeActivity.current_user.name;
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String type="Image";
        String receiverFirebaseToken = getIntent().getStringExtra(Constant.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                type,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(getApplicationContext(),
                chat,
                receiverFirebaseToken);
    }


}
