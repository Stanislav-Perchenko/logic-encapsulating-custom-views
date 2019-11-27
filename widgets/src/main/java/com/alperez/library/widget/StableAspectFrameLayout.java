package com.alperez.library.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.alperez.library.customlayout.R;


/**
 * Created by stanislav.perchenko on 10/28/2016.
 */

public class StableAspectFrameLayout extends FrameLayout {

    public static final int REF_SIDE_WIDTH = 0;
    public static final int REF_SIDE_HEIGHT = 1;

    private int aspectWidth = 1;
    private int aspectHeight = 1;
    private int refSide = REF_SIDE_WIDTH;

    public StableAspectFrameLayout(Context context) {
        this(context, null, 0);
    }

    public StableAspectFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StableAspectFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractCustomAttrs(context, attrs);
        verifyLayoutParams(getLayoutParams());

    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        verifyLayoutParams(params);
        super.setLayoutParams(params);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StableAspectFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractCustomAttrs(context, attrs);
        verifyLayoutParams(getLayoutParams());
    }

    private void extractCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.StableAspectFrameLayout);
        try {
            aspectWidth = a.getInteger(R.styleable.StableAspectFrameLayout_aspect_width, 0);
            aspectHeight = a.getInteger(R.styleable.StableAspectFrameLayout_aspect_height, 0);
            refSide = a.getInteger(R.styleable.StableAspectFrameLayout_ref_side, refSide);
        } finally {
            a.recycle();
        }
    }

    private void verifyLayoutParams(@Nullable ViewGroup.LayoutParams params) {
        if (aspectHeight != 0 & aspectWidth != 0 & params != null) {
            switch (refSide) {
                case REF_SIDE_WIDTH:
                    if (params.width > 0) {
                        params.height = Math.round(((float) params.width) * aspectHeight / aspectWidth);
                    }
                    break;
                case REF_SIDE_HEIGHT:
                    if (params.height > 0) {
                        params.width = Math.round(((float) params.height) * aspectWidth / aspectHeight);
                    }
                    break;
                default:
                    throw new IllegalStateException("Value not supported - refSide");
            }
        }
    }


    public void setAspectWidth(int aspectWidth) {
        setAspectRatio(aspectWidth, this.aspectHeight);
    }

    public void setAspectHeight(int aspectHeight) {
        setAspectRatio(this.aspectWidth, aspectHeight);
    }

    public void setAspectRatio(int aspectWidth, int aspectHeight) {
        if ((this.aspectWidth != aspectWidth) || (this.aspectHeight != aspectHeight)) {
            this.aspectWidth = aspectWidth;
            this.aspectHeight = aspectHeight;
            verifyLayoutParams(getLayoutParams());
            invalidate();
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int wSpec, int hSpec) {

        if (aspectHeight != 0 && aspectWidth != 0) {
            int[] finSpecs = getFinalMeasureSpecs(wSpec, hSpec);
            super.onMeasure(finSpecs[0], finSpecs[1]);
        } else {
            super.onMeasure(wSpec, hSpec);
        }
    }




    private int[] finalSpecs;
    private int lastRequestSpecW;
    private int lastRequestSpecH;
    @SuppressLint("WrongCall")
    private int[] getFinalMeasureSpecs(int requestSpecW, int requestSpecH) {
        if ((finalSpecs != null) && (lastRequestSpecW == requestSpecW) && lastRequestSpecH == requestSpecH) {
            Log.d(getClass().getSimpleName(), "onMeasure() - same-params call");
            return finalSpecs;
        }

        if (finalSpecs == null) finalSpecs = new int[2];

        lastRequestSpecW = requestSpecW;
        lastRequestSpecH = requestSpecH;

        final int specW = MeasureSpec.getMode(requestSpecW);
        final int requiredW = MeasureSpec.getSize(requestSpecW);
        final int specH = MeasureSpec.getMode(requestSpecH);
        final int requiredH = MeasureSpec.getSize(requestSpecH);
        Log.d(getClass().getSimpleName(), String.format("onMeasure() specW=%d, requiredW=%d, specH=%d, requiredH=%d", specW, requiredW, specH, requiredH));

        switch (refSide) {
            case REF_SIDE_WIDTH:
                if ((specW == MeasureSpec.EXACTLY) && (requiredW > 0)) {
                    int newH = Math.round((float) requiredW * aspectHeight / aspectWidth);
                    finalSpecs[0] = requestSpecW;
                    finalSpecs[1] = MeasureSpec.makeMeasureSpec(newH, MeasureSpec.EXACTLY);
                } else {
                    super.onMeasure(requestSpecW, requestSpecH);
                    int newH = Math.round(((float) getMeasuredWidth()) * aspectHeight / aspectWidth);
                    finalSpecs[0] = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
                    finalSpecs[1] = MeasureSpec.makeMeasureSpec(newH, MeasureSpec.EXACTLY);
                }
                break;
            case REF_SIDE_HEIGHT:
                if ((specH == MeasureSpec.EXACTLY) && (requiredH > 0)) {
                    int newW = Math.round((float) requiredH * aspectWidth / aspectHeight);
                    finalSpecs[0] = MeasureSpec.makeMeasureSpec(newW, MeasureSpec.EXACTLY);
                    finalSpecs[1] = requestSpecH;
                } else {
                    super.onMeasure(requestSpecW, requestSpecH);
                    int newW = Math.round(((float) getMeasuredHeight()) * aspectWidth / aspectHeight);
                    finalSpecs[0] = MeasureSpec.makeMeasureSpec(newW, MeasureSpec.EXACTLY);
                    finalSpecs[1] = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
                }
                break;
        }
        return finalSpecs;
    }


}

