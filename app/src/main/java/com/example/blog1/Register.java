package com.example.blog1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private RelativeLayout rlayout;
    private Animation animation;
    private EditText regemail,regpass;
    private Button btn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.bgHeader);
        rlayout = findViewById(R.id.rlayout);
        animation = AnimationUtils.loadAnimation(this,R.anim.uptodowndiagonal);
        rlayout.setAnimation(animation);
        auth=FirebaseAuth.getInstance();
        regemail=(EditText)findViewById(R.id.emai);
        regpass=(EditText)findViewById(R.id.pass);
        btn=(Button)findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    // Upload data to database
                    String userEmail = regemail.getText().toString().trim();
                    String userPassword = regpass.getText().toString().trim();

                    auth.createUserWithEmailAndPassword(userEmail, userPassword).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        sendEmailVerification();
                                    } else {
                                        Toast.makeText(Register.this, "registration failed", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                }
            }
        });
    }
    private boolean validate() {
        Boolean result = false;
        String email = regemail.getText().toString();
        String password = regpass.getText().toString();


        if (password.isEmpty() || email.isEmpty()) {
            Toast.makeText(Register.this, "Please enter all details", Toast.LENGTH_LONG).show();
        } else {
            result = true;
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = auth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register.this, "Sucessfully registered,verification mail is sent", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                        finish();
                        startActivity(new Intent(Register.this, MainActivity.class));
                    } else {
                        Toast.makeText(Register.this, "verification email has'nt been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
