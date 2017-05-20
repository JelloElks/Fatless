package com.fatless.fatless;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChoreActivity extends AppCompatActivity {

    private static final String TAG = ChoreActivity.class.getName();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @BindView(R2.id.random_chore_button)
    Button random_chore_button;
    @BindView(R2.id.pick_chore_button)
    Button pick_chore_button;

    @BindView(R2.id.chore_info_view)
    TextView chore_info_view;

    private int energyKcal;
    private String choreName;
    private double metValue;
    private String uid;

    private ArrayList<MetHelper> metList;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore);

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
                    startActivity(new Intent(ChoreActivity.this, LoginActivity.class));
                    finish();

                }

            }
        };
        metList = new ArrayList<>();
        metText();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }


        Intent intent = getIntent();
        energyKcal = intent.getIntExtra("kcal", 0);

        pick_chore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        random_chore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chore_info_view.setText(calculateChore(energyKcal, getUserWeight(uid)));
            }
        });

    }

    private double getUserWeight(String uid) {
        SharedPreferences sharedPreferences = getSharedPreferences("profileprefs" + uid, MODE_PRIVATE);

        String strUserWeight = sharedPreferences.getString("userweight", "");

        return Double.parseDouble(strUserWeight);
    }

    public String calculateChore(double calories, double kg) {

        Random random = new Random();

        MetHelper metHelper = metList.get(random.nextInt(metList.size()));

        double h = calories / (metHelper.getMetLevel() * kg);

        int hours = (int) h;
        int minutes = (int) (h * 60) % 60;
        int seconds = (int) (h * (60 * 60)) % 60;

        return ("You could " + metHelper.getActivity() + " for " + String.format("%s(h) %s(m) %s(s)", hours, minutes, seconds)
                + "\nPress Random to randomize");
    }

    public void metText() {

        Scanner scanner;

        try {
            DataInputStream textFileStream = new DataInputStream(getAssets().open("met/" + "metvalues.txt"));
            scanner = new Scanner(textFileStream);

            while (scanner.hasNextLine()) {
                String activity;
                double metLevel;
                activity = scanner.nextLine();
                metLevel = (Double.parseDouble(scanner.nextLine()));
                metList.add(new MetHelper(activity, metLevel));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getLocalizedMessage());
        }
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
