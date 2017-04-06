package io.steemapp.steemy.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import io.steemapp.steemy.R;

/**
 * Created by john.white on 9/2/15.
 */
public class IndeterminateRadialBar extends View {

    protected boolean spinning = false;
    protected int backgroundColor = 0;
    protected int fillColor = 0;

    protected Paint backgroundPaint;
    protected Paint fillPaint;
    protected Context mContext;

    protected int diameter = 0;

    protected RectF[] circleBounds = new RectF[4];
    protected Path[] backgroundPaths = new Path[4];
    protected Path[] fillPaths = new Path[4];

    protected static final float[] mSizeMods = {.5f, .4f, .3f, .2f};

    protected static final int[] mFillAngles = {200, 160, 120, 80};

    protected float[] mStartAngles = new float[4];

    protected ValueAnimator[] valueAnimator = new ValueAnimator[4];
    protected Matrix[] mRotationMatrices = new Matrix[4];

    float centerX;
    float centerY;

    public IndeterminateRadialBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        extractAttrs(attrs);
    }

    public IndeterminateRadialBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        extractAttrs(attrs);
    }

    private void extractAttrs(AttributeSet attrs){
        TypedArray a = mContext.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.IndeterminateRadialBar,
                0, 0);

        try{
            spinning = a.getBoolean(R.styleable.IndeterminateRadialBar_spinning, true);
            backgroundColor = a.getColor(R.styleable.IndeterminateRadialBar_ibackgroundColor, mContext.getResources().getColor(R.color.medGray));
            fillColor = a.getColor(R.styleable.IndeterminateRadialBar_ifillColor, mContext.getResources().getColor(R.color.colorPrimary));
        }finally{
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(w < h){
            diameter = w;
        }else
            diameter = h;

        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        if(width < height){
            diameter = width;
        }else
            diameter = height;
        init();
        setMeasuredDimension(width, height);
    }

    public void init(){
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        fillPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setStrokeCap(Paint.Cap.ROUND);

        float strokeWidth = 30;
        diameter = diameter - (int)strokeWidth;
        backgroundPaint.setStrokeWidth(strokeWidth);
        fillPaint.setStrokeWidth(strokeWidth);

        if(backgroundColor == 0)
            backgroundColor = mContext.getResources().getColor(R.color.accent_material_dark);

        if(fillColor == 0)
            fillColor = mContext.getResources().getColor(R.color.accent_material_light);

        backgroundPaint.setColor(backgroundColor);
        fillPaint.setColor(fillColor);

        float size;
        centerX = getWidth()/2;
        centerY = getHeight()/2;

        for(int i = 0; i < 4; i++) {
            size = diameter * mSizeMods[i];
            circleBounds[i] = new RectF();
            circleBounds[i].set(centerX - size, centerY - size, centerX + size, centerY + size);
            backgroundPaths[i] = new Path();
            backgroundPaths[i].addArc(circleBounds[i], -0, 360f);

            mStartAngles[i] = (float)Math.random() * 360f;
            fillPaths[i] = new Path();
            fillPaths[i].addArc(circleBounds[i], mStartAngles[i], mFillAngles[i]);
            mRotationMatrices[i] = new Matrix();
            mRotationMatrices[i].postRotate(mStartAngles[i], centerX, centerY);
        }
        valueAnimator[0] = ValueAnimator.ofFloat(mFillAngles[0], 360+mFillAngles[0]);
        valueAnimator[0].setInterpolator(new LinearInterpolator());
        valueAnimator[0].setDuration(1500);
        valueAnimator[0].setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator[0].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mStartAngles[0] = (float) animation.getAnimatedValue();
                if (mStartAngles[0] >= 360f)
                    mStartAngles[0] = mStartAngles[0] - 360f;

                fillPaths[0].rewind();
                fillPaths[0].addArc(circleBounds[0], mStartAngles[0], mFillAngles[0]);
                mRotationMatrices[0] = new Matrix();
                invalidate();

            }
        });
        valueAnimator[1] = ValueAnimator.ofFloat(mFillAngles[1], 360+mFillAngles[1]);
        valueAnimator[1].setInterpolator(new LinearInterpolator());
        valueAnimator[1].setDuration(1500);
        valueAnimator[1].setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator[1].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mStartAngles[1] = -(float) animation.getAnimatedValue();
                if (mStartAngles[1] <= 0f)
                    mStartAngles[1] = mStartAngles[1] + 360f;

                fillPaths[1].rewind();
                fillPaths[1].addArc(circleBounds[1], mStartAngles[1], mFillAngles[1]);
                invalidate();
            }
        });

        valueAnimator[2] = ValueAnimator.ofFloat(mFillAngles[2], 360+mFillAngles[2]);
        valueAnimator[2].setInterpolator(new LinearInterpolator());
        valueAnimator[2].setDuration(1500);
        valueAnimator[2].setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator[2].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mStartAngles[2] = (float) animation.getAnimatedValue();
                if (mStartAngles[2] >= 360f)
                    mStartAngles[2] = mStartAngles[2] - 360f;

                fillPaths[2].rewind();
                fillPaths[2].addArc(circleBounds[2], mStartAngles[2], mFillAngles[2]);
                invalidate();
            }
        });

        valueAnimator[3] = ValueAnimator.ofFloat(mFillAngles[3], 360+mFillAngles[3]);
        valueAnimator[3].setInterpolator(new LinearInterpolator());
        valueAnimator[3].setDuration(1500);
        valueAnimator[3].setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator[3].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mStartAngles[3] = -(float) animation.getAnimatedValue();
                if (mStartAngles[3] <= 0f)
                    mStartAngles[3] = mStartAngles[3] + 360f;

                fillPaths[3].rewind();
                fillPaths[3].addArc(circleBounds[3], mStartAngles[3], mFillAngles[3]);

                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(backgroundPaths[0], backgroundPaint);
        canvas.drawPath(fillPaths[0], fillPaint);

        canvas.drawPath(backgroundPaths[1], backgroundPaint);
        canvas.drawPath(fillPaths[1], fillPaint);

        canvas.drawPath(backgroundPaths[2], backgroundPaint);
        canvas.drawPath(fillPaths[2], fillPaint);

        canvas.drawPath(backgroundPaths[3], backgroundPaint);
        canvas.drawPath(fillPaths[3], fillPaint);

//        if(spinning){
//            indeterminateSpin();
//        }
    }

    public void indeterminateSpin() {
        valueAnimator[0].start();
        valueAnimator[1].start();
        valueAnimator[2].start();
        valueAnimator[3].start();
    }


}
