package com.fatless.fatless;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<FoodInformation> foodInfoList;
    // private ArrayAdapter<FoodInformation> informationArrayAdapter;

    private String foodUrlInfo = "http://www.matapi.se/foodstuff/";
    private String name;
    private int number;

    @BindView(R2.id.food_name)
    TextView food_name;
    @BindView(R2.id.food_info_view)
    TextView food_info_view;

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
                    startActivity(new Intent(FoodInformationActivity.this, MainActivity.class));
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


            //TODO är inte en array är redan ett jsonObject måste komma på nå sätt att få ut dom värden vi vill ha och inte alla.
            JSONObject jsObject = new JSONObject(result);
            JSONArray jsonArray = jsObject.getJSONArray("nutrientValues");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("name");
                int number = jsonObject.getInt("number");
                int protein = jsonObject.getInt("protein");
                int energyKcal = jsonObject.getInt("energyKcal");
                int fat = jsonObject.getInt("fat");
                int sodium = jsonObject.getInt("sodium");

                FoodInformation foodInformation = new FoodInformation(number, protein, fat, energyKcal, sodium);
                foodInformation.setName(name);
                foodInformation.setProtein(protein);
                foodInformation.setEnergyKcal(energyKcal);
                foodInformation.setFat(fat);
                foodInformation.setSodium(sodium);

                foodInfoList.add(foodInformation);
            }

            StringBuilder builder = new StringBuilder();
            for (FoodInformation information : foodInfoList) {
                builder.append(information).append("\n");
            }

            food_info_view.setText(builder.toString());
            //  informationArrayAdapter = new ArrayAdapter<>(this, R.layout.list_white_text_simple, foodInfoList);

            // food_info_view.setAdapter(informationArrayAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }


    private void getFoodInformation(int number) {


        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(foodUrlInfo + number)
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
