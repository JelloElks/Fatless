package com.fatless.fatless;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FoodInformationActivity extends AppCompatActivity {

    private static final String TAG = FoodInformationActivity.class.getName();
    private static final String FOOD_URL_INFO = "http://www.matapi.se/foodstuff/";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<FoodInformation> foodInfoList;

    private String name;
    private int number;

    @BindView(R2.id.food_name)
    TextView food_name;
    @BindView(R2.id.food_information_text)
    TextView food_information_text;

    @BindView(R2.id.energyKcal_text_info)
    TextView energyKcal_text_info;
    @BindView(R2.id.protein_text_info)
    TextView protein_text_info;
    @BindView(R2.id.fat_text_info)
    TextView fat_text_info;
    @BindView(R2.id.sodium_text_info)
    TextView sodium_text_info;

    @BindView(R2.id.pick_food_button)
    Button pick_food_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_information);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    startActivity(new Intent(FoodInformationActivity.this, LoginActivity.class));
                    finish();
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        Intent intent = getIntent();
        number = intent.getIntExtra("number", 0);
        name = intent.getStringExtra("name");

        food_name.setText(name);
        getFoodInformation(number);


    }


    public void manageJson(String result) {


        try {
            foodInfoList = new ArrayList<>();

            JSONObject jsObject = new JSONObject(result);

            JSONObject jss = jsObject.getJSONObject("nutrientValues");


            int protein = jss.getInt("protein");
            double energyKcal = jss.getDouble("energyKcal");
            int fat = jss.getInt("fat");
            int sodium = jss.getInt("sodium");
            final FoodInformation foodInformation = new FoodInformation(number, protein, fat, energyKcal, sodium);
            foodInformation.setName(name);
            foodInfoList.add(foodInformation);


            energyKcal_text_info.setText(String.format("EnergyKcal :  %s", String.valueOf(foodInformation.getEnergyKcal())));
            fat_text_info.setText(String.format("Fat :  %s", String.valueOf(foodInformation.getFat())));
            protein_text_info.setText(String.format("Protein :  %s", String.valueOf(foodInformation.getProtein())));
            sodium_text_info.setText(String.format("Sodium :  %s", String.valueOf(foodInformation.getSodium())));


            double kcalPerGram = energyKcal / 100;
            foodInformation.setEnergyKcal(kcalPerGram);
            
            pick_food_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentForChore = new Intent(FoodInformationActivity.this, ChoreActivity.class);
                    intentForChore.putExtra("kcal", foodInformation.getEnergyKcal());
                    startActivity(intentForChore);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }


    private void getFoodInformation(int number) {


        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(FOOD_URL_INFO + number)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed  :" + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String jsonData = response.body().string();

                    Log.v(TAG, jsonData);
                    if (response.isSuccessful()) {


                        runOnUiThread(new Runnable() {
                            public void run() {
                                manageJson(jsonData);
                            }
                        });

                    }

                }
            });
        } else {
            Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private int calculateAmountOfKcal() {
        return 2;
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
}
