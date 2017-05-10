package com.fatless.fatless;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getName();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int SELECT_PHOTO = 1;

    @BindView(R2.id.picture_in_frame)
    ImageView picture_in_frame;
    @BindView(R2.id.change_picture)
    TextView change_picture;


    @BindView(R2.id.logout_button)
    Button logout_button;
    @BindView(R2.id.save_profile)
    Button save_profile;

    // Go to SearchForFoodActivity temp
    @BindView(R2.id.go_to_food)
    Button goToFoodBtn;


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
            String uid = user.getUid();
            // prepareForm loads Preferences Object length weight age picture etc. bug atm can only change picture once while logged in
            prepareForm(uid);
            String email = user.getEmail();


            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.


            goToFoodBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProfileActivity.this, SearchForFoodActivity.class));
                }
            });


            save_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = user.getUid();
                    saveProfile(uid);
                    /*
                    Profile picture reverting to first image set if prepareForm is set here
                     */
                    // prepareForm(uid);
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
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(ProfileActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        ActivityCompat.requestPermissions(ProfileActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                } else {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ProfileActivity.this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                }

            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        picture_in_frame.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }


    private void prepareForm(String uid) {

        SharedPreferences preferences = getSharedPreferences("profileprefs" + uid, MODE_PRIVATE);

        user_name.setText(preferences.getString("username", ""));
        user_age.setText(preferences.getString("userage", ""));
        user_length.setText(preferences.getString("userlength", ""));
        user_weight.setText(preferences.getString("userweight", ""));
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
        Log.i(TAG, " Succes:  SharedPreferences Saved");
    }


    public void setProfilePhoto(String uid) {
        ImageView imageToString = picture_in_frame;
        //code image to string
        imageToString.buildDrawingCache();
        Bitmap bitmap = imageToString.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image = stream.toByteArray();

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
