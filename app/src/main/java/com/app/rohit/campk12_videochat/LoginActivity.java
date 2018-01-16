package com.app.rohit.campk12_videochat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends BaseActivity implements View.OnClickListener {


    private Button emailLogin;
    private EditText email;
    private EditText password;
    private Button emailRegister;

    private FirebaseAuth mAuth;

    LinearLayout registerLayout;
    LinearLayout signinLayout;
    private String TAG = "LoginActivity";
    private EditText name;
    private TextView signin_text;
    private TextView register_text;

    public static String user_name ;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        registerLayout = (LinearLayout)findViewById(R.id.register_layout);
        signinLayout = (LinearLayout)findViewById(R.id.signin_layout);

        emailLogin = (Button) findViewById(R.id.email_login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        emailRegister = (Button) findViewById(R.id.email_register);

        emailRegister.setOnClickListener(this);
        emailLogin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        //updateUI(currentUser);
    }

    private void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference users= FirebaseDatabase.getInstance().getReference("users");
                            users.child(mAuth.getCurrentUser().getUid()).child("name").setValue(name.getText().toString());
                            users.child(user.getUid()).child("email").setValue(email);
                            users.child(user.getUid()).child("connected").setValue(false);
                            users.child(user.getUid()).child("invite_sent_to").setValue("");
                            users.child(user.getUid()).child("invite_recieved_from").setValue("");
                            show_verify_dialog();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void show_verify_dialog() {

        name.setVisibility(View.GONE);
        emailLogin.setVisibility(View.VISIBLE);
        emailRegister.setVisibility(View.GONE);
        registerLayout.setVisibility(View.VISIBLE);
        signinLayout.setVisibility(View.GONE);



        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Verify your email")
                .setContentText(email.getText().toString())
                .setConfirmText("Verify")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        name.setText("");
                        email.setText("");
                        password.setText("");
                        sDialog.dismiss();
                        sendEmailVerification();


                    }
                })
                .show();



//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_verify_email);
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
//        Button verify_button = (Button) dialog.findViewById(R.id.verify_email);
//        TextView email_address = (TextView) dialog.findViewById(R.id.email_address);
//
//        email_address.setText(email.getText().toString());
//        verify_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendEmailVerification();
//                dialog.dismiss();
//            }
//        });
//
//
//        dialog.show();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //  mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        //findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        // findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {


//                            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
//                                    .setTitleText("Verify your email!")
//                                    .setContentText("Verification email has been sent!")
//                                    .setConfirmText("OK")
//                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                            sweetAlertDialog.dismiss();
//                                        }
//                                    })
//                                    .show();

                            Toast.makeText(LoginActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        if(name.getVisibility()==View.VISIBLE)
        {
            String na = name.getText().toString().trim();
            if (TextUtils.isEmpty(na)) {
                name.setError("Required.");
                valid = false;
            } else {
                name.setError(null);
            }
        }

        String emailadd = email.getText().toString();
        if (TextUtils.isEmpty(emailadd)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String pass = password.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
//        if (user != null) {
//            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
//                    user.getEmail(), user.isEmailVerified()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
//            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
//            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
//
//            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
//        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
//            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
//            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
//        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_register) {
            createAccount(email.getText().toString(), password.getText().toString());
        } else if (i == R.id.email_login) {
            signIn(email.getText().toString(), password.getText().toString());
        }

        if(i==R.id.register)
        {
            name.setVisibility(View.VISIBLE);
            emailLogin.setVisibility(View.GONE);
            emailRegister.setVisibility(View.VISIBLE);
            registerLayout.setVisibility(View.GONE);
            signinLayout.setVisibility(View.VISIBLE);
            name.requestFocus();
        }
        if(i==R.id.signin)
        {
            name.setVisibility(View.GONE);
            emailLogin.setVisibility(View.VISIBLE);
            emailRegister.setVisibility(View.GONE);
            registerLayout.setVisibility(View.VISIBLE);
            signinLayout.setVisibility(View.GONE);
        }
//        } else if (i == R.id.) {
//            signOut();
//        } else if (i == R.id.verify_email_button) {
//            sendEmailVerification();
//        }
    }

    private void initView() {
        name = (EditText) findViewById(R.id.name);
        signin_text = (TextView) findViewById(R.id.signin);
        signin_text.setOnClickListener(this);
        register_text = (TextView) findViewById(R.id.register);
        register_text.setOnClickListener(this);
    }
}
