package com.ahmedshaban.awesomefacebooksplash;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * View that displays a colored bar as a ratio of an integer value to a maximum value.  There is also a circle
 * indicator and text that displays at the position of the current value, as well as a label (positioned above the bar).
 * The maximum value is displayed to the right of the bar.
 *
 * <p>See {@link R.styleable#ValueBar ValueBar Attributes}</p>
 */
public class ValueBar extends View {

    private int maxValue = 100;
    private int currentValue = 0;

    private boolean animated;
    private float valueToDraw; //for use during an animation
    private long animationDuration = 4000l; //4 second default.  this is the time it takes to traverse the entire bar
    ValueAnimator animation = null;

    //instance variables for storing xml attributes
    private int barHeight;
    private int circleRadius;
    private int spaceAfterBar;
    private int circleTextSize;
    private int maxValueTextSize;
    private int currentValueTextColor;
    private int circleTextColor;
    private int baseColor;
    private int fillColor;

    private String labelText;

    //objects used for drawing
    private Paint labelPaint;
    private Paint maxValuePaint;
    private Paint barBasePaint;
    private Paint barFillPaint;
    private Paint circlePaint;
    private Paint currentValuePaint;
    private FacebookLoadingListener mloadinglistener;

    private Bitmap mLogoBitmap;


    public ValueBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Set the maximum value that will be allowed
     *
     * @param maxValue
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
        requestLayout();
    }

    /**
     * Sets the value of the bar.  If the passed in value exceeds the maximum, the value
     * will be set to the maximum.
     *
     * @param newValue
     */
    public void setValue(int newValue) {
        int previousValue = currentValue;
        if(newValue < 0) {
            currentValue = 0;
        } else if (newValue > maxValue) {
            currentValue = maxValue;
        } else {
            currentValue = newValue;
        }

        if(animation != null) {
            animation.cancel();
        }

        if(animated) {
            animation = ValueAnimator.ofFloat(previousValue, currentValue);
            //animationDuration specifies how long it should take to animate the entire graph, so the
            //actual value to use depends on how much the value needs to change
            int changeInValue = Math.abs(currentValue - previousValue);
            long durationToUse = (long) (animationDuration * ((float) changeInValue / (float) maxValue));
            animation.setDuration(durationToUse);

            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    valueToDraw = (float) valueAnimator.getAnimatedValue();
                    ValueBar.this.invalidate();
                    if(valueToDraw==maxValue && mloadinglistener !=null){
                        mloadinglistener.onLoadingFinished();
                    }
                }
            });

            animation.start();
        } else {
            valueToDraw = currentValue;
        }



        invalidate();
    }

    /**
     * Get the current value
     *
     * @return
     */
    public int getValue() {
        return currentValue;
    }

    private void init(Context context, AttributeSet attrs) {
        setSaveEnabled(true);
        //logo init
        mLogoBitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_round);
        mLogoBitmap = getResizedBitmap(mLogoBitmap,100,100);
        //read xml attributes
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ValueBar, 0, 0);
        barHeight = ta.getDimensionPixelSize(R.styleable.ValueBar_barHeight, 0);
        circleRadius = ta.getDimensionPixelSize(R.styleable.ValueBar_circleRadius, 0);
        spaceAfterBar = ta.getDimensionPixelSize(R.styleable.ValueBar_spaceAfterBar, 0);
        circleTextSize = ta.getDimensionPixelSize(R.styleable.ValueBar_circleTextSize, 0);
        maxValueTextSize = ta.getDimensionPixelSize(R.styleable.ValueBar_maxValueTextSize, 0);
        currentValueTextColor = ta.getColor(R.styleable.ValueBar_maxValueTextColor, Color.BLACK);
        circleTextColor = ta.getColor(R.styleable.ValueBar_circleTextColor, Color.BLACK);
        baseColor = ta.getColor(R.styleable.ValueBar_baseColor, Color.BLACK);
        fillColor = ta.getColor(R.styleable.ValueBar_fillColor, Color.BLACK);
        labelText = ta.getString(R.styleable.ValueBar_labelText);
        ta.recycle();

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        maxValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maxValuePaint.setTextSize(maxValueTextSize);
        maxValuePaint.setColor(currentValueTextColor);
        maxValuePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        maxValuePaint.setTextAlign(Paint.Align.RIGHT);

        barBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBasePaint.setColor(baseColor);

        barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barFillPaint.setColor(fillColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(fillColor);

        currentValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentValuePaint.setTextSize(circleTextSize);
        currentValuePaint.setColor(circleTextColor);
        currentValuePaint.setTextAlign(Paint.Align.CENTER);
    }

    private int measureHeight(int measureSpec) {

        int size = getPaddingTop() + getPaddingBottom();
        size += labelPaint.getFontSpacing();
        float maxValueTextSpacing = maxValuePaint.getFontSpacing();
        size += Math.max(maxValueTextSpacing, Math.max(barHeight, circleRadius * 2));

        return resolveSizeAndState(size, measureSpec, 0);
    }

    private int measureWidth(int measureSpec) {

        int size = getPaddingLeft() + getPaddingRight();
        Rect bounds = new Rect();
        String maxValueText = String.valueOf(maxValue);
        maxValuePaint.getTextBounds(maxValueText, 0, maxValueText.length(), bounds);
        size += bounds.width();

        return resolveSizeAndState(size, measureSpec, 0);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onDraw (Canvas canvas) {
        //drawLabel(canvas);
        //drawLogo(canvas);
        drawBar(canvas);
        //drawMaxValue(canvas);
    }

    private void drawLogo(Canvas canvas) {
        int size = (canvas.getWidth()/2);
        size  = size  - mLogoBitmap.getWidth();
        canvas.drawBitmap(mLogoBitmap,size+getPaddingLeft()+getPaddingRight(),0,labelPaint);
    }

    private void drawLabel(Canvas canvas) {
        float x = getPaddingLeft();
        //the y coordinate marks the bottom of the text, so we need to factor in the height
        Rect bounds = new Rect();
        labelPaint.getTextBounds(labelText, 0, labelText.length(), bounds);
        float y = getPaddingTop() + bounds.height();
        canvas.drawText(labelText, x, y, labelPaint);
    }

    private void drawBar(Canvas canvas) {
        float barLength = getWidth() - getPaddingRight() - getPaddingLeft();

        float barCenter = getBarCenter();

        float halfBarHeight = barHeight / 2;
        float top = barCenter - halfBarHeight;
        float bottom = barCenter + halfBarHeight;
        float left = getPaddingLeft();
        float right = getPaddingLeft() + barLength;
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rect, halfBarHeight, halfBarHeight, barBasePaint);


        float percentFilled = (float) valueToDraw / (float) maxValue;
        float fillLength = barLength * percentFilled;
        float fillPosition = left + fillLength;
        RectF fillRect = new RectF(left, top, fillPosition, bottom);
        canvas.drawRoundRect(fillRect, halfBarHeight, halfBarHeight, barFillPaint);

    }



    private float getBarCenter() {
        //position the bar slightly below the middle of the drawable area
        float barCenter = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2; //this is the center
        return barCenter;
    }


    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    /**
     * Set the time (in milliseconds) that the animation should use to traverse the entire graph.  The actual
     * value used will depend on how much tha value changes.
     *
     * @param animationDuration duration of the animation
     */
    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.value = currentValue;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        currentValue = ss.value;
        valueToDraw = currentValue; //set valueToDraw directly to prevent re-animation
    }

    public void setLoadingListener(FacebookLoadingListener loadinglistener) {
        this.mloadinglistener = loadinglistener;
    }

    private static class SavedState extends BaseSavedState {
        int value;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}
