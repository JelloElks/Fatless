package com.fatless.fatless;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private
    @BindView(R2.id.editTextEmail)
    EditText editTextEmail;
    private
    @BindView(R2.id.editTextPassword)
    EditText editTextPassword;
    private
    @BindView(R2.id.buttonRegister)
    Button buttonRegister;
    private
    @BindView(R2.id.textViewSignin)
    TextView textViewSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



    }

    private void registerUser() {

    }

}
