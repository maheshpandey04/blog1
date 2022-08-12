package com.example.blog1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    Animation topanim,bottomanim;
    ImageView image;
    TextView slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        LogoLauncher logoLauncher=new LogoLauncher();
        logoLauncher.start();
        topanim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomanim=AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        image=findViewById(R.id.img);
        slogan=findViewById(R.id.logo);
        image.setAnimation(topanim);
        slogan.setAnimation(bottomanim);
    }
    private class LogoLauncher extends Thread{
        public void run(){
            try {
                sleep(5000);
            }catch (InterruptedException e){
               e.printStackTrace();
            }
            Intent intent=new Intent(Home.this,MainActivity.class);
            startActivity(intent);
            Home.this.finish();
        }
    }
}
