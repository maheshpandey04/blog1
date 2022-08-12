package com.example.blog1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

public class PostActivity extends AppCompatActivity {
    private ImageButton mSelectImage;
    private Button subbtn;
    private EditText title,desc,name,phone,exc;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Uri FilePathUri;
    StorageReference storageReference;
    StorageReference storageReference2nd;
    DatabaseReference databaseReference;
    int Image_Request_Code = 7;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        title=(EditText)findViewById(R.id.titleField);
        desc=(EditText)findViewById(R.id.descField);
        name=(EditText)findViewById(R.id.nameField);
        phone=(EditText)findViewById(R.id.phoneField);
        exc=(EditText) findViewById(R.id.excField);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mSelectImage=(ImageButton)findViewById(R.id.imageSelect);
        storageReference = FirebaseStorage.getInstance().getReference();
        subbtn=(Button)findViewById(R.id.submitBtn);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChooseFile(view);
//                CropImage.activity().start(PostActivity.this);
//                Intent intent=new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Please Select Image"),Image_Request_Code);

            }
        });
        subbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();

            }
        });
    }
    public void onChooseFile(View v){
        CropImage.activity().start(PostActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                FilePathUri=result.getUri();
                mSelectImage.setImageURI(FilePathUri);
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e=result.getError();
                Toast.makeText(PostActivity.this,"Possible error is"+e,Toast.LENGTH_SHORT).show();
            }
        }
    }
    public String GetFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void UploadImage(){
        //mProgressDialog.setTitle("Uploading");

        final String title_val=title.getText().toString().trim();
        final String desc_val=desc.getText().toString().trim();
        final String name_val=name.getText().toString().trim();
        final String phone_val=phone.getText().toString().trim();
        final String exc_val=exc.getText().toString().trim();
        int length = phone.getText().length();
        String convert=String.valueOf(length);
        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) &&  !TextUtils.isEmpty(name_val) &&!TextUtils.isEmpty(exc_val) && !TextUtils.isEmpty(phone_val)  && phone_val.length()>=10 && FilePathUri!=null){
            //mProgressDialog.show();
            storageReference2nd=storageReference.child(System.currentTimeMillis()+"."+GetFileExtension(FilePathUri));
            storageReference2nd.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"Image Uploaded Sucessfully",Toast.LENGTH_LONG).show();
                }
            })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference newPost=databaseReference.push();
                                    newPost.child("username").setValue(name_val);
                                    newPost.child("title").setValue(title_val);
                                    newPost.child("desc").setValue(desc_val);
                                    newPost.child("phonenumber").setValue(phone_val);
                                    newPost.child("uid").setValue(mUser.getUid());
                                    newPost.child("image").setValue(uri.toString());
                                    newPost.child("excPrice").setValue(exc_val);
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }
                            });
                        }
                    });
        }
    }
}
