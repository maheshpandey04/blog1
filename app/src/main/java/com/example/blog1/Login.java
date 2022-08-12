package com.example.blog1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    private ImageButton btRegister;
    private TextView tvLogin,frgt;
    private Button sub,phon;
    private EditText regemail,regpass;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseuser;
    private static final String EMAIL = "email";
    SignInButton goog;
    private GoogleApiClient mGoogleApi;
    private static final int RC_SIGN_IN=1;
    private static final String TAG="Login";
    GoogleSignInClient mGoogleSignInClient;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btRegister = findViewById(R.id.btRegister);
        regemail=(EditText)findViewById(R.id.email);
        regpass=(EditText)findViewById(R.id.password);
        firebaseAuth=FirebaseAuth.getInstance();
        goog=findViewById(R.id.ggl);
        frgt=findViewById(R.id.tvForgot);
        tvLogin = findViewById(R.id.tvLogin);
        sub=(Button)findViewById(R.id.btn);
        mDatabaseuser= FirebaseDatabase.getInstance().getReference().child("Users");
        //mDatabaseuser.keepSynced(true);
        progressBar=findViewById(R.id.progressbar);
        phon=findViewById(R.id.phn);
        phon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Phone.class));
            }
        });

        frgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Forget.class));
            }
        });
        goog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String email = regemail.getText().toString().trim();
                String password = regpass.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    regemail.setError("email is required");
                }
                else if (TextUtils.isEmpty(password)) {
                    regpass.setError("password is required");
                }
                else if (password.length() < 6) {
                    regpass.setError("password must be 6 digit long");
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkEmailVerification();
                            } else {
                                Toast.makeText(Login.this, "error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btRegister) {

                    Intent intent = new Intent(Login.this, Register.class);

                    Pair[] pairs = new Pair[1];

                    pairs[0] = new Pair<View, String>(tvLogin, "tvLogin");

                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);

                    startActivity(intent, activityOptions.toBundle());
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestId()
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private TextWatcher loginTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String username=regemail.getText().toString().trim();
            String userpassword=regpass.getText().toString().trim();
            sub.setEnabled(!username.isEmpty() && !userpassword.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private void checkEmailVerification() {
        FirebaseUser firebaseUser=firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag=firebaseUser.isEmailVerified();
        if(emailflag){
            startActivity(new Intent(Login.this,MainActivity.class));

        }
        else{
            Toast.makeText(this,"verify email",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            startActivity(new Intent(Login.this,MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
