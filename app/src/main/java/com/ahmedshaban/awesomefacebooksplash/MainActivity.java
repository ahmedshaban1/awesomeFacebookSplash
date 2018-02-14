package com.ahmedshaban.awesomefacebooksplash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements FacebookLoadingListener {

    FacebookSplashView facebookSplashView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facebookSplashView = findViewById(R.id.facebookLoading);
        facebookSplashView.setFacebookLoadingListener(this);
    }

    @Override
    public void onLoadingFinished() {
        // handle finish loading
        Toast.makeText(this, "loading finished", Toast.LENGTH_SHORT).show();
    }
}
