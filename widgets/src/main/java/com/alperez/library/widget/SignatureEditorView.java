package com.alperez.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.alperez.library.customlayout.R;

/**
 * Created by stanislav.perchenko on 11/27/2019, 3:10 PM.
 */
public class SignatureEditorView extends View {

    public interface OnSignatureChangeListener {
        void onChanged(int nTracks);
    }

    public interface OnTouchStateChangeListener {
        void onChanged(SignatureEditorView signEditor);
    }


    private static final int N_TRACKS = 3;
    private static final int MAX_POINTS_IN_TRACK = 512;

    private int attrSignColor = Color.BLACK;
    private int attrSignStrokeWidth = 5;
    private float attrMinPointDelta = 2.1f;

    private Paint paint = new Paint();

    private Path[] mPPP = new Path[N_TRACKS];
    private float[][] mXX = new float[N_TRACKS][MAX_POINTS_IN_TRACK];
    private float[][] mYY = new float[N_TRACKS][MAX_POINTS_IN_TRACK];
    private int[] mTrackSizes = new int[N_TRACKS];
    private int mCurrentTrack = -1;
    private int mTrackIndex = 0;

    private OnSignatureChangeListener onSignatureChangeListener;
    private OnTouchStateChangeListener onTouchStateChangeListener;

    public SignatureEditorView(Context context) {
        super(context);
        init();
    }

    public SignatureEditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        extractCustomAttrs(context, attrs);
        init();
    }

    public SignatureEditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractCustomAttrs(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SignatureEditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractCustomAttrs(context, attrs);
        init();
    }

    private void extractCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.SignatureEditorView);
        try {
            attrSignColor = a.getColor(R.styleable.SignatureEditorView_signColor, attrSignColor);
            attrSignStrokeWidth = a.getDimensionPixelSize(R.styleable.SignatureEditorView_signStrokeWidth, attrSignStrokeWidth);
        } finally {
            a.recycle();
        }
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setColor(attrSignColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(attrSignStrokeWidth);
        for (int i=0; i<N_TRACKS; i++) mPPP[i] = new Path();
    }

    public void setOnSignatureChangeListener(OnSignatureChangeListener l) {
        this.onSignatureChangeListener = l;
    }

    public void setOnTouchStateChangeListener(OnTouchStateChangeListener l) {
        this.onTouchStateChangeListener = l;
    }

    public void clear() {
        for (int i=0; i<N_TRACKS; i++) mPPP[i].reset();
        invalidate();
        mCurrentTrack = -1;
        mTrackIndex = 0;
        if (onSignatureChangeListener != null) onSignatureChangeListener.onChanged(0);
    }

    public boolean isHasSignature() {
        return mCurrentTrack >= 0;
    }

    public boolean isInTouch() {
        return isInTouch;
    }

    public int getSignTracksCount() {
        return mCurrentTrack + 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i=0; i<=mCurrentTrack; i++) {
            canvas.drawPath(mPPP[i], paint);
        }
    }

    boolean isInTouch;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float evX = event.getX();
        final float evY = event.getY();

        boolean needInvalidate = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mCurrentTrack < (N_TRACKS - 1)) {
                    mCurrentTrack ++;
                    mTrackSizes[mCurrentTrack] = 0;
                    mTrackIndex = 0;
                    mPPP[mCurrentTrack].reset();
                    mPPP[mCurrentTrack].moveTo(evX, evY);
                    storePointToCurrentTrack(evX, evY);
                    isInTouch = true;
                    needInvalidate = true;
                    if (onTouchStateChangeListener != null) onTouchStateChangeListener.onChanged(this);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isInTouch) return false;

                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float hX = event.getHistoricalX(i);
                    float hY = event.getHistoricalY(i);
                    if (storePointToCurrentTrack(hX, hY)) {
                        mPPP[mCurrentTrack].lineTo(mXX[mCurrentTrack][mTrackIndex-1], mYY[mCurrentTrack][mTrackIndex-1]);
                        needInvalidate = true;
                    }
                }

                if (storePointToCurrentTrack(evX, evY)) {
                    mPPP[mCurrentTrack].lineTo(mXX[mCurrentTrack][mTrackIndex-1], mYY[mCurrentTrack][mTrackIndex-1]);
                    needInvalidate = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isInTouch) return false;
                if (storePointToCurrentTrack(evX, evY)) {
                    mPPP[mCurrentTrack].lineTo(mXX[mCurrentTrack][mTrackIndex-1], mYY[mCurrentTrack][mTrackIndex-1]);
                    needInvalidate = true;
                }
                mTrackSizes[mCurrentTrack] = mTrackIndex;
                isInTouch = false;
                if (onTouchStateChangeListener != null) onTouchStateChangeListener.onChanged(this);
                if (onSignatureChangeListener != null) onSignatureChangeListener.onChanged(mCurrentTrack + 1);
                break;

            case MotionEvent.ACTION_CANCEL:
                if (!isInTouch) return false;
                mPPP[mCurrentTrack].reset();
                mCurrentTrack --;
                mTrackIndex = 0;
                isInTouch = false;
                if (onTouchStateChangeListener != null) onTouchStateChangeListener.onChanged(this);
                break;

            default:
                return false;
        }

        if (needInvalidate) invalidate();
        return true;
    }

    private boolean storePointToCurrentTrack(float x, float y) {
        if (mTrackIndex < MAX_POINTS_IN_TRACK) {

            if (x < 0) x = 0;
            else if (x >= getWidth()) x = getWidth() - 1;

            if (y < 0) y = 0;
            else if (y >= getHeight()) y = getHeight() - 1;

            if (mTrackIndex > 0) {
                float dx = x - mXX[mCurrentTrack][mTrackIndex-1];
                float dy = y - mYY[mCurrentTrack][mTrackIndex-1];
                float dL = (float) Math.sqrt(dx*dx + dy*dy);

                if (dL < attrMinPointDelta) return false;
            }
            mXX[mCurrentTrack][mTrackIndex] = x;
            mYY[mCurrentTrack][mTrackIndex] = y;
            mTrackIndex ++;
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public String getSignature() {
        if (!isHasSignature() || isInTouch()) return null;

        final int width  = getWidth();
        final int height = getHeight();

        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append(';');
        sb.append(height);

        for (int track = 0; track <= mCurrentTrack; track++) {
            sb.append('+');
            final int trackSize = mTrackSizes[track];
            for (int j = 0; j < trackSize; j++) {
                if (j > 0) sb.append(';');
                sb.append((int)(10_000*mXX[track][j]/width));
                sb.append(',');
                sb.append((int)(10_000*mYY[track][j]/height));
            }
        }
        return sb.toString();
    }

}

