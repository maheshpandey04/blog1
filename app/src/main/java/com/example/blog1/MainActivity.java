package com.example.blog1;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class MainActivity extends AppCompatActivity {
    LinearLayoutManager mLayoutManager;
    SharedPreferences mSharedPref;
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    DatabaseReference mDatabaseuser;
    FirebaseUser mFirebaseUser;
    String usern;
    //usern = firebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Posts Title");
        mSharedPref=getSharedPreferences("Sort Settings",MODE_PRIVATE);
        String msorting =mSharedPref.getString("Sort","newest");
        if(msorting.equals("newest")){
            mLayoutManager=new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        }else if(msorting.equals("oldest")){
            mLayoutManager=new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }
        mRecyclerView=findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mRef=mFirebaseDatabase.getReference().child("Blog");
        mDatabaseuser=mFirebaseDatabase.getReference().child("Users");
        mFirebaseUser=mAuth.getCurrentUser();
        //mDatabaseuser.keepSynced(true);
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginintent = new Intent(MainActivity.this, Login.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
        FirebaseRecyclerAdapter<Model,ViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Model, ViewHolder> (
                        Model.class,
                        R.layout.row,
                        ViewHolder.class,
                        mRef
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
                        final String post_key=getRef(i).getKey();
                        viewHolder.setTitle(model.getTitle());
                        //viewHolder.setName(model.getName());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setPhonenumber(model.getPhonenumber());
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setExcPrice(model.getExcPrice());
                        viewHolder.setImage(getApplicationContext(),model.getImage());


                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        ViewHolder viewHolder=super.onCreateViewHolder(parent, viewType);
                        viewHolder.setClickListner(new ViewHolder.ClickListner() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String post_key=getRef(position).getKey();;
                                Toast.makeText(MainActivity.this,post_key,Toast.LENGTH_SHORT).show();
                                String mTitle=getItem(position).getTitle();
                                String mDesc=getItem(position).getDesc();
                                String mImage=getItem(position).getImage();
                                String mnam=getItem(position).getUsername();
                                String mphn=getItem(position).getPhonenumber();
                                String mExc=getItem(position).getExcPrice();
                                Intent intent=new Intent(view.getContext(),After.class);
                                intent.putExtra("image",mImage);
                                intent.putExtra("title",mTitle);
                                intent.putExtra("desc",mDesc);
                                intent.putExtra("phonenumber",mphn);
                                intent.putExtra("username",mnam);
                                intent.putExtra("blog_id",post_key);
                                intent.putExtra("excPrice",mExc);
                                startActivity(intent);

                            }

                            @Override
                            public void onItemLongClick(View view, int posttion) {
                                String currentTitle=getItem(posttion).getTitle();
                                String currentImage=getItem(posttion).getImage();
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();
                                if (user.getUid().equals(uid)) {
                                    //showDeleteDialog(currentTitle, currentImage);
                                }

                            }
                        });
                        return viewHolder;

                    }
                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebasesearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebasesearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==R.id.action_logout){
            logout();
        }
        if(item.getItemId()==R.id.action_sort)
        {
            sort();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sort() {
        String[] sortOptions={"Newsest","Oldest"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Sort by")
                .setIcon(R.drawable.sort)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            SharedPreferences.Editor editor=mSharedPref.edit();
                            editor.putString("Sort","newest");
                            editor.apply();
                            recreate();
                        }
                        else if(which==1){
                            SharedPreferences.Editor editor=mSharedPref.edit();
                            editor.putString("Sort","oldest");
                            editor.apply();
                            recreate();
                        }
                    }
                });
        builder.show();
    }


    private  void firebasesearch(String searchText){
        Query firebaseSearchQuery=mRef.orderByChild("title").startAt(searchText.toLowerCase()).endAt(searchText.toLowerCase()+"\uf8ff");
        FirebaseRecyclerAdapter<Model,ViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Model, ViewHolder>(
                        Model.class,
                        R.layout.row,
                        ViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
                        viewHolder.setTitle(model.getTitle());
                        //viewHolder.setName(model.getName());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setPhonenumber(model.getPhonenumber());
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setExcPrice(model.getExcPrice());
                        viewHolder.setImage(getApplicationContext(),model.getImage());
                    }
                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        ViewHolder viewHolder=super.onCreateViewHolder(parent, viewType);
                        viewHolder.setClickListner(new ViewHolder.ClickListner() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String mTitle=getItem(position).getTitle();
                                String mDesc=getItem(position).getDesc();
                                String mImage=getItem(position).getImage();
                                Intent intent=new Intent(view.getContext(),After.class);
                                intent.putExtra("image",mImage);
                                intent.putExtra("title",mTitle);
                                intent.putExtra("desc",mDesc);
                                startActivity(intent);

                            }

                            @Override
                            public void onItemLongClick(View view, int posttion) {
                               String currentTitle=getItem(posttion).getTitle();
                               String currentImage=getItem(posttion).getImage();
                               //showDeleteDialog(currentTitle,currentImage);

                            }
                        });
                        return viewHolder;

                    }
                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private void showDeleteDialog(final String currentTitle, final String currentImage) {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are You Sure To Delete This Post?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query quary=mRef.orderByChild("title").equalTo(currentTitle);
                quary.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(MainActivity.this,"Post deleted  Sucessfully",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                StorageReference mPictureRefs=getInstance().getReferenceFromUrl(currentImage);
                mPictureRefs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this,"Image deleted Sucessfully....",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    

    private void logout() {
        mAuth.signOut();
    }

}
