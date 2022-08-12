package com.example.blog1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class After extends AppCompatActivity {
    TextView mTitletv,mDetailTv,nam,phone,exch;
    ImageView mImageView;
    Button msave,mwall,mshare,delete,up;
    ImageButton op;
    private DatabaseReference mRef;
    FirebaseAuth mAuth;
    private String mPost=null;
    private static final int REQUEST_CALL=1;
    private static final int WRITE_EXTERNAL_STORAGE_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Post Detail");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        mTitletv=findViewById(R.id.rTitl);
        mDetailTv=findViewById(R.id.rDes);
        mImageView=findViewById(R.id.rImag);
        msave=findViewById(R.id.save);
        mwall=findViewById(R.id.wall);
        mshare=findViewById(R.id.share);
        delete=findViewById(R.id.del);
        nam=findViewById(R.id.rNam);
        phone=findViewById(R.id.rPhn);
        up=findViewById(R.id.upi);
        op=findViewById(R.id.ophn);
        exch=findViewById(R.id.rExc);
        mPost=getIntent().getStringExtra("blog_id");
//        String title=getIntent().getExtras().getString("title");
//        String desc=getIntent().getExtras().getString("desc");
//        String img=getIntent().getExtras().getString("image");
        mRef=FirebaseDatabase.getInstance().getReference().child("Blog");
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(After.this,SetupActivity.class));
            }
        });
        op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonein();
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth=FirebaseAuth.getInstance();
        String image=getIntent().getStringExtra("image");
        String title=getIntent().getStringExtra("title");
        String desc=getIntent().getStringExtra("desc");
        String nama=getIntent().getStringExtra("username");
        String phn=getIntent().getStringExtra("phonenumber");
        String e2=getIntent().getStringExtra("excPrice");
        mTitletv.setText(title);
        mDetailTv.setText(desc);
        phone.setText(phn);
        nam.setText(nama);
        exch.setText(e2);
        Picasso.get().load(image).into(mImageView);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        msave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                            PackageManager.PERMISSION_DENIED){
                        String[] permission={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,WRITE_EXTERNAL_STORAGE_CODE);
                    }
                    else
                    {
                       saveImage();
                    }

                }
                else {
                    saveImage();
                }
            }
        });
        mwall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImgWallpaper();
            }
        });
        mshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });
        mRef.child(mPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String post_uid=(String)dataSnapshot.child("uid").getValue();
                if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                    delete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void phonein() {
        String number=phone.getText().toString();
        if(number.trim().length()>0){
            if (ContextCompat.checkSelfPermission(After.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(After.this,
                        new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);

            }else {
                String dial="tel:"+number;
                startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
            }
        }else {
           Toast.makeText(After.this,"Enter Phone Number",Toast.LENGTH_SHORT).show();
        }
    }

    private void delete() {
        mRef.child(mPost).removeValue();
        startActivity(new Intent(After.this,MainActivity.class));
    }

    public static Bitmap viewToBitmap(View view,int width,int height){
        Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    private void setImgWallpaper(){
        WallpaperManager wallpaperManager=WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(viewToBitmap(mImageView,mImageView.getWidth(),mImageView.getHeight()));
            Toast.makeText(After.this,"Sucess",Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private void shareImage() {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Bitmap bitmap=viewToBitmap(mImageView,mImageView.getWidth(),mImageView.getHeight());
            String s=mTitletv.getText().toString()+"\n"+mDetailTv.getText().toString();
            File file=new File(getExternalCacheDir(),"sample.png");
            FileOutputStream fOut=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true,false);
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT,s);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("images/png");
            startActivity(Intent.createChooser(intent,"Share via"));

        }catch (Exception e){
            Toast.makeText(After.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        FileOutputStream fileOutputStream=null;
        File file=getDisc();
        if(!file.exists() && !file.mkdirs()){
            Toast.makeText(After.this,"Can't Create Directory to Save Image",Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyymmsshhmmss");
        String date=simpleDateFormat.format(new Date());
        String name="Img"+date+".jpg";
        String file_name=file.getAbsolutePath()+"/"+name;
        File new_file=new File(file_name);
        try {
            fileOutputStream=new FileOutputStream(new_file);
            Bitmap bitmap=viewToBitmap(mImageView,mImageView.getWidth(),mImageView.getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            Toast.makeText(After.this,"Save image Sucess",Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(new_file);

    }
    public void refreshGallery(File file){
        Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }
    private File getDisc(){
        File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file,"Image Demo");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:{
                if(grantResults.length>0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
                    saveImage();
                }
                else {
                    Toast.makeText(this,"enable permissions",Toast.LENGTH_SHORT).show();
                }
            }

        }
        if(requestCode==REQUEST_CALL){
            if(grantResults.length>0 && grantResults[0]==
            PackageManager.PERMISSION_GRANTED){
                phonein();

            }else {

                Toast.makeText(After.this,"Permission DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
