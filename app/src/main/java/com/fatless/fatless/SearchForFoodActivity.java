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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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


public class SearchForFoodActivity extends AppCompatActivity {

    private static final String TAG = SearchForFoodActivity.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private ArrayList<FoodItems> foodList;
    private ArrayAdapter<FoodItems> itemsAdapter;


    @BindView(R2.id.search_food)
    SearchView search_food;

    @BindView(R2.id.list_food)
    ListView list_food;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_food);
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
                    // User is signed out
                    startActivity(new Intent(SearchForFoodActivity.this, LoginActivity.class));
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // Gets listView items
        populateFoodList();

        search_food.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                itemsAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemsAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    public void manageJson(String result) {


        try {
            foodList = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("name");
                int number = jsonObject.getInt("number");

                FoodItems foodItems = new FoodItems(name);

                foodItems.setName(name);
                foodItems.setNumber(number);

                foodList.add(foodItems);
            }

            itemsAdapter = new ArrayAdapter<>(this, R.layout.list_white_text_simple, foodList);

            list_food.setAdapter(itemsAdapter);

            list_food.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    FoodItems foodItems = (FoodItems) list_food.getItemAtPosition(position);
                    Intent intent = new Intent(SearchForFoodActivity.this, FoodInformationActivity.class);
                    intent.putExtra("name", foodItems.getName());
                    intent.putExtra("number", foodItems.getNumber());
                    startActivity(intent);


                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }


    private void populateFoodList() {


        String urlToMat = "http://www.matapi.se/foodstuff?query=";

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlToMat)
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
