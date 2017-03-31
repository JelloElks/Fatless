package com.fatless.fatless;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    @BindView(R.id.show_pass)
    TextView showPassword;

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

        showPassword.setVisibility(View.GONE);

        login_password_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (login_password_text.getText().length() > 0) {
                    showPassword.setVisibility(View.VISIBLE);
                } else {
                    showPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPassword.getText() == "Show") {
                    showPassword.setText("Hide");
                    login_password_text.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    login_password_text.setSelection(login_password_text.length());
                } else {
                    showPassword.setText("Show");
                    login_password_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    login_password_text.setSelection(login_password_text.length());
                }
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
