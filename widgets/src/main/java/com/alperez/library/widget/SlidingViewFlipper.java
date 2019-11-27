package com.alperez.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.alperez.library.customlayout.R;


/**
 * Created by stanislav.perchenko on 1/4/2016.
 */
public class SlidingViewFlipper extends ViewFlipper {
    public interface OnChildChangedListener {
        void onChildChanged(int childIndex);
    }


    public SlidingViewFlipper(Context context) {
        super(context);
    }

    public SlidingViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.SlidingViewFlipper);
        try {
            int id = a.getResourceId(R.styleable.SlidingViewFlipper_incrementInAnimation, -1);
            Log.i(getClass().getSimpleName(), "incrementInAnimation ID = " + id);
            if (id > 0) {
                nextNewAnimation = AnimationUtils.loadAnimation(context, id);
                nextNewAnimation.setAnimationListener(mAnimListener);
            }
            id = a.getResourceId(R.styleable.SlidingViewFlipper_incrementOutAnimation, -1);
            Log.i(getClass().getSimpleName(), "incrementOutAnimation ID = " + id);
            if (id > 0) {
                nextOldAnimation = AnimationUtils.loadAnimation(context, id);
                nextOldAnimation.setAnimationListener(mAnimListener);
            }
            id = a.getResourceId(R.styleable.SlidingViewFlipper_decrementInAnimation, -1);
            Log.i(getClass().getSimpleName(), "decrementInAnimation ID = " + id);
            if (id > 0) {
                prevNewAnimation = AnimationUtils.loadAnimation(context, id);
                prevNewAnimation.setAnimationListener(mAnimListener);
            }
            id = a.getResourceId(R.styleable.SlidingViewFlipper_decrementOutAnimation, -1);
            Log.i(getClass().getSimpleName(), "decrementOutAnimation ID = " + id);
            if (id > 0) {
                prevOldAnimation = AnimationUtils.loadAnimation(context, id);
                prevOldAnimation.setAnimationListener(mAnimListener);
            }
        } finally {
            a.recycle();
        }
    }


    private Animation nextOldAnimation;
    private Animation nextNewAnimation;
    private Animation prevOldAnimation;
    private Animation prevNewAnimation;

    private OnChildChangedListener mChildChangedListener;

    private boolean animationInProgress;

    public void setOnChildChangedListener(OnChildChangedListener l) {
        mChildChangedListener = l;
    }

    public boolean isAnimationInProgress() {
        return animationInProgress;
    }


    private boolean mNewAnimCompleted;
    private boolean mOldAnimCompleted;

    private Animation.AnimationListener mAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            animationInProgress = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if((nextNewAnimation != null) && (nextNewAnimation == animation)) mNewAnimCompleted = true;
            if((prevNewAnimation != null) && (prevNewAnimation == animation)) mNewAnimCompleted = true;
            if((nextOldAnimation != null) && (nextOldAnimation == animation)) mOldAnimCompleted = true;
            if((prevOldAnimation != null) && (prevOldAnimation == animation)) mOldAnimCompleted = true;
            if (mNewAnimCompleted && mOldAnimCompleted) {
                animationInProgress = false;
                if (mChildChangedListener != null) {
                    mChildChangedListener.onChildChanged(SlidingViewFlipper.this.getDisplayedChild());
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    @Override
    public void showNext() {
        setInAnimation(nextNewAnimation);
        setOutAnimation(nextOldAnimation);
        super.showNext();
        if (mChildChangedListener != null) {
            if ((nextNewAnimation == null) && (nextOldAnimation == null)) {
                mChildChangedListener.onChildChanged(getDisplayedChild());
            } else {
                mNewAnimCompleted = (nextNewAnimation == null);
                mOldAnimCompleted = (nextOldAnimation == null);
            }
        }
    }

    public void showNext(int absPageIndex) {
        setInAnimation(nextNewAnimation);
        setOutAnimation(nextOldAnimation);
        super.setDisplayedChild(absPageIndex);
        if (mChildChangedListener != null) {
            if ((nextNewAnimation == null) && (nextOldAnimation == null)) {
                mChildChangedListener.onChildChanged(absPageIndex);
            } else {
                mNewAnimCompleted = (nextNewAnimation == null);
                mOldAnimCompleted = (nextOldAnimation == null);
            }
        }
    }

    @Override
    public void showPrevious() {
        setInAnimation(prevNewAnimation);
        setOutAnimation(prevOldAnimation);
        super.showPrevious();
        if (mChildChangedListener != null) {
            if ((prevNewAnimation == null) && (prevOldAnimation == null)) {
                mChildChangedListener.onChildChanged(getDisplayedChild());
            } else {
                mNewAnimCompleted = (prevNewAnimation == null);
                mOldAnimCompleted = (prevOldAnimation == null);
            }
        }
    }

    public void showPrevious(int absPageIndex) {
        setInAnimation(prevNewAnimation);
        setOutAnimation(prevOldAnimation);
        super.setDisplayedChild(absPageIndex);
        if (mChildChangedListener != null) {
            if ((prevNewAnimation == null) && (prevOldAnimation == null)) {
                mChildChangedListener.onChildChanged(absPageIndex);
            } else {
                mNewAnimCompleted = (prevNewAnimation == null);
                mOldAnimCompleted = (prevOldAnimation == null);
            }
        }
    }

    public void showWithoutAnim(int which) {
        setInAnimation(null);
        setOutAnimation(null);
        setDisplayedChild(which);
        if(mChildChangedListener != null) {
            mChildChangedListener.onChildChanged(which);
        }
    }



    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }
}

