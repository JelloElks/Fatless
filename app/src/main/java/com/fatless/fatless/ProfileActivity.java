package com.fatless.fatless;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getName();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @BindView(R2.id.picture_in_frame)
    ImageView picture_in_frame;
    @BindView(R2.id.change_picture)
    TextView change_picture;


    @BindView(R2.id.logout_button)
    Button logout_button;
    @BindView(R2.id.save_profile)
    Button save_profile;

    @BindView(R2.id.user_name)
    EditText user_name;
    @BindView(R2.id.user_age)
    EditText user_age;
    @BindView(R2.id.user_length)
    EditText user_length;
    @BindView(R2.id.user_weight)
    EditText user_weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    prepareForm();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                }

            }
        };
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url

            String email = user.getEmail();


            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            save_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveProfile();
                    prepareForm();
                }
            });

            logout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();

                }
            });

        }

    }

    private void prepareForm() {

        SharedPreferences preferences = getSharedPreferences("profileprefs", MODE_PRIVATE);

        user_name.setText(preferences.getString("username", "..."));
        user_age.setText(preferences.getString("userage", "..."));
        user_length.setText(preferences.getString("userlength", "..."));
        user_weight.setText(preferences.getString("userweight", "..."));
    }

    private void saveProfile() {
        SharedPreferences preferences = getSharedPreferences("profileprefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (user_name != null) {
            editor.putString("username", user_name.getText().toString());
        }
        if (user_age != null) {
            editor.putString("userage", user_age.getText().toString());
        }
        if (user_length != null) {
            editor.putString("userlength", user_length.getText().toString());
        }
        if (user_weight != null) {
            editor.putString("userweight", user_weight.getText().toString());
        }

        editor.apply();
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
