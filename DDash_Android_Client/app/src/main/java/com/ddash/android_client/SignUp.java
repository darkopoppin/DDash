package com.ddash.android_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    EditText email;
    EditText password;
    EditText firstname;
    EditText surname;
    Button register;
    ProgressBar progBa;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sign_up);
        progBa = findViewById(R.id.signup_progressBar_bar);
        email = findViewById(R.id.signup_editText_email);
        password = findViewById(R.id.signup_editText_password);
        firstname = findViewById(R.id.signup_editText_firstname);
        surname = findViewById(R.id.signup_editText_surname);
        register = findViewById(R.id.signup_button_register);

        fAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progBa.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(
                        email.getText().toString(),
                        password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progBa.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            //Successfully registered to firebase
                            Toast.makeText(SignUp.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                        } else {
                            //Failed to register
                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

}
