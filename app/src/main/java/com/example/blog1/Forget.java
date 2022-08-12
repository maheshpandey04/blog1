package com.example.blog1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget extends AppCompatActivity {
    private TextView ema;
    private Button btn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ema=findViewById(R.id.email);
        btn=findViewById(R.id.btn);
        firebaseAuth=FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(ema.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Forget.this,"Password Reset Link Sent to your Email",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Forget.this,Login.class));
                        }
                        else {
                            Toast.makeText(Forget.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
