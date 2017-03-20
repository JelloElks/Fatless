package com.fatless.fatless;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    @BindView(R2.id.login_password_text)
    EditText login_password_text;

    @BindView(R2.id.login_email_text)
    EditText login_email_text;

    @BindView(R2.id.login)
    Button login;

    @BindView(R2.id.registerButtonMain)
    Button registerWithMail;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Butterknife bindings
        ButterKnife.bind(this);

        //Facebook stuff
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in
                    Toast.makeText(MainActivity.this, "Welcome  " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(MainActivity.this, LoggedInActivity.class));

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        registerUser();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(login_email_text.getText().toString(), login_password_text.getText().toString());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void registerUser() {
        registerWithMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateUserActivity.class));
            }
        });

    }

    private void signIn(String email, String password) {

        Log.d(TAG, "signIn: " + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInComplete : " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithMail : failed" + task.getException());
                    Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = login_email_text.getText().toString();
        if (TextUtils.isEmpty(email)) {
            login_email_text.setError("Required.");
            valid = false;
        } else {
            login_email_text.setError(null);
        }
        String password = login_password_text.getText().toString();
        if (TextUtils.isEmpty(password)) {
            login_password_text.setError("Required.");
            valid = false;
        } else {
            login_password_text.setError(null);
        }

        return valid;
    }
}
