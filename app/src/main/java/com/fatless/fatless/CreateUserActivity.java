package com.fatless.fatless;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateUserActivity extends AppCompatActivity {

    private static final String TAG = CreateUserActivity.class.getName();


    @BindView(R2.id.editTextEmail)
    EditText editTextEmail;

    @BindView(R2.id.editTextPassword)
    EditText editTextPassword;

    @BindView(R2.id.emailRegisterButton)
    Button emailRegisterButton;

    @BindView(R2.id.textViewSignin)
    TextView textViewSignin;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed o0t
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        textViewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateUserActivity.this, LoginActivity.class));
                finish();
            }
        });

        emailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidPassword(editTextPassword.getText().toString()) && isValidEmail(editTextEmail.getText().toString())) {
                    regUser();
                    editTextEmail.setError(null);
                    editTextPassword.setError(null);
                }
                if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
                    editTextEmail.setError("Required.");

                } else if (!isValidEmail(editTextEmail.getText().toString())) {
                    Toast.makeText(CreateUserActivity.this, "Wrong Email Format", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(editTextPassword.getText().toString())) {
                    editTextPassword.setError("Required.");

                } else if (!isValidPassword(editTextPassword.getText().toString())) {
                    Toast.makeText(CreateUserActivity.this, R.string.password_requirement, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void regUser() {
        mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(CreateUserActivity.this, "User with this email is already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CreateUserActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (task.isSuccessful()) {
                            startActivity(new Intent(CreateUserActivity.this, ProfileActivity.class));
                            finish();
                        }

                    }
                });
    }

    /*
    Om vi vill ändra är det bara ta bort det första i regexen och ha det där (6,20) som kollar hur långt passwordet är
    Just nu behöver man 1 stor bokstav och 1 nummer samt minst 6 tecken och högst 20 .
     */
    private boolean isValidEmail(CharSequence mail) {

        return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    private boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "((?=.*[0-9])(?=.*[A-Z]).{6,20})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
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