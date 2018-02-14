package com.ahmedshaban.awesomefacebooksplash;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by USER on 2/13/2018.
 */

public class FacebookSplashView extends RelativeLayout {


    private View rootView;
    private ImageView imageView;
    private ValueBar valueBar;
    FacebookLoadingListener facebookLoadingListener;
    Context context;
    int duration = 4001;






    public FacebookSplashView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context,attrs);
    }

    public FacebookSplashView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
    }

    public void setFacebookLoadingListener(FacebookLoadingListener facebookLoadingListener){
        this.facebookLoadingListener = facebookLoadingListener;
        valueBar.setLoadingListener(facebookLoadingListener);
    }



    private void init(Context context,AttributeSet attrs) {
        this.context = context;
        rootView = inflate(context, R.layout.facebooksplash, this);
        imageView = (ImageView) rootView.findViewById(R.id.logo);
        valueBar = (ValueBar) rootView.findViewById(R.id.valuebar);
        valueBar.setMaxValue(100);
        valueBar.setAnimated(true);
        valueBar.setValue(100);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ValueBar, 0, 0);

        Drawable drawable = ta.getDrawable(R.styleable.ValueBar_logosrc);
        duration = ta.getInt(R.styleable.ValueBar_duration,duration);
        valueBar.setAnimationDuration(duration);

        ta.recycle();
        imageView.setImageDrawable(drawable);



    }

    public void setSpaceBetweenLogoAndBar(int spaceBetweenLogoAndBar) {
        setMargins(imageView,0,0,0,spaceBetweenLogoAndBar);

    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams p = (MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void setLogoWidth(int logoWidth) {
        imageView.getLayoutParams().width = logoWidth;
    }


    public void setLogoHeight(int logoHeight) {
        imageView.getLayoutParams().height = logoHeight;
    }

    public  void setAnimationDuration(long duration){
        valueBar.setAnimationDuration(duration);

    }


}
