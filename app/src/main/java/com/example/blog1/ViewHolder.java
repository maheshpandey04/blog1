package com.example.blog1;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    public  ImageView image;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListner.onItemClick(view,getAdapterPosition() );
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListner.onItemLongClick(view,getAdapterPosition());
                return false;
            }
        });
    }
    public void  setTitle(String title){
        TextView postTite=(TextView)mView.findViewById(R.id.rTitle);
        postTite.setText(title);
    }
    public  void setDesc(String desc){
        TextView postDesc=(TextView)mView.findViewById(R.id.rDesc);
        postDesc.setText(desc);
    }
    public  void setImage(Context ctx,String image){
        ImageView postImage=(ImageView)mView.findViewById(R.id.rImage);
        Picasso.get().load(image).fit().centerCrop().into(postImage);
    }
    public void setUsername(String name){
        TextView postName=(TextView)mView.findViewById(R.id.rUsername);
        postName.setText(name);
    }
    public  void  setPhonenumber(String phonenumber){
        TextView postPhonenumber=(TextView)mView.findViewById(R.id.rPhonenumber);
        postPhonenumber.setText(phonenumber);
    }
    public void setExcPrice(String excPrice){
        TextView postExc=(TextView)mView.findViewById(R.id.rExc);
        postExc.setText(excPrice);
    }

    @Override
    public void onClick(View view) {
        itemView.setOnClickListener(this);
    }

    private  ViewHolder.ClickListner mClickListner;
    public  interface ClickListner{
        void onItemClick(View view,int position);
        void onItemLongClick(View view ,int posttion);
    }

    public void setClickListner(ViewHolder.ClickListner clickListner) {
        mClickListner=clickListner;
    }
}

