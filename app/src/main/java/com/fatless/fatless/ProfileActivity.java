package com.fatless.fatless;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;

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
                    String uid = user.getUid();
                    prepareForm(uid);
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


            save_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = user.getUid();
                    saveProfile(uid);
                    prepareForm(uid);
                    setProfilePhoto(uid);
                }
            });

            logout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();

                }
            });

        }

        picture_in_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                // String picturePath contains the path of selected Image

                // Show the Selected Image on ImageView
                ImageView imageView = picture_in_frame;
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            }
        }
    }


    private void prepareForm(String uid) {

        SharedPreferences preferences = getSharedPreferences("profileprefs" + uid, MODE_PRIVATE);

        user_name.setText(preferences.getString("username", " "));
        user_age.setText(preferences.getString("userage", " "));
        user_length.setText(preferences.getString("userlength", " "));
        user_weight.setText(preferences.getString("userweight", " "));
        String img_str = preferences.getString("userphoto", "");
        if (!img_str.equals("")) {
            byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
            picture_in_frame.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
    }

    private void saveProfile(String uid) {
        SharedPreferences preferences = getSharedPreferences("profileprefs" + uid, MODE_PRIVATE);
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


    public void setProfilePhoto(String uid) {
        ImageView imageToString = picture_in_frame;
        //code image to string
        imageToString.buildDrawingCache();
        Bitmap bitmap = imageToString.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image = stream.toByteArray();
        //System.out.println("byte array:"+image);
        //final String img_str = "data:image/png;base64,"+ Base64.encodeToString(image, 0);
        //System.out.println("string:"+img_str);
        String img_str = Base64.encodeToString(image, 0);
        //decode string to image
        byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
        ImageView decodedImage = picture_in_frame;
        decodedImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
        );

        SharedPreferences preferences = getSharedPreferences("profileprefs" + uid, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userphoto", img_str);
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
