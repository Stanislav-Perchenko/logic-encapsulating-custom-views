package com.alperez.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.alperez.library.customlayout.R;

/**
 * Created by stanislav.perchenko on 11/19/2019, 4:03 PM.
 */
public class SignatureView extends View {

    private int attrSignColor = Color.BLACK;
    private int attrSignStrokeWidth = 5;

    private String mSignature;
    private int originalSignatureWidth;
    private int originalSignatureHeight;

    private int nTracks;
    private float[][] mXX, mYY;
    private Path[] mPPP;

    private Paint paint;

    public SignatureView(Context context) {
        super(context);
        init();
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        extractCustomAttrs(context, attrs);
        init();
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractCustomAttrs(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SignatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractCustomAttrs(context, attrs);
        init();
    }

    private void extractCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.SignatureView);
        try {
            attrSignColor = a.getColor(R.styleable.SignatureView_signColor, attrSignColor);
            attrSignStrokeWidth = a.getDimensionPixelSize(R.styleable.SignatureView_signStrokeWidth, attrSignStrokeWidth);
        } finally {
            a.recycle();
        }
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(attrSignColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(attrSignStrokeWidth);
    }

    public void setSignColor(int signColor) {
        if (this.attrSignColor !=signColor) {
            this.attrSignColor = signColor;
            init();
            invalidate();
        }
    }

    public void setSignStrokeWidth(int signStrokeWidth) {
        if (this.attrSignStrokeWidth !=signStrokeWidth) {
            this.attrSignStrokeWidth = signStrokeWidth;
            init();
            invalidate();
        }
    }

    public void setSignature(@Nullable String signature) {
        if (!TextUtils.equals(signature, mSignature)) {
            mSignature = signature;
            nTracks = 0;
            mXX = null;
            mYY = null;
            mPPP = null;

            String[] sections = mSignature.split("\\+");
            String[] txt_wh = sections[0].split(";");
            originalSignatureWidth = Integer.parseInt(txt_wh[0]);
            originalSignatureHeight = Integer.parseInt(txt_wh[1]);

            invalidate();
        }
    }

    public int getOriginalSignatureWidth() {
        return originalSignatureWidth;
    }

    public int getOriginalSignatureHeight() {
        return originalSignatureHeight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            nTracks = 0;
            mXX = null;
            mYY = null;
            mPPP = null;
        }
    }


    private void calculateDrawings(final int width, final int height) {
        try {
            String[] sections = mSignature.split("\\+");

            nTracks = sections.length - 1;
            mXX = new float[nTracks][];
            mYY = new float[nTracks][];

            for (int trk=0; trk<nTracks; trk++) {
                String[] points = sections[1+trk].split(";");
                final int n_points = points.length;
                mXX[trk] = new float[n_points];
                mYY[trk] = new float[n_points];
                for (int i=0; i<n_points; i++) {
                    String[] p_i = points[i].split(",");
                    float x = Float.parseFloat(p_i[0]) * width / 10_000f;
                    float y = Float.parseFloat(p_i[1]) * height / 10_000f;
                    mXX[trk][i] = x;
                    mYY[trk][i] = y;
                }
            }
        } catch (Exception e) {
            setSignature(null);
            return;
        }

        mPPP = new Path[nTracks];
        for (int trk=0; trk<nTracks; trk++) {
            Path p = mPPP[trk] = new Path();

            float[] xx = mXX[trk];
            float[] yy = mYY[trk];
            p.moveTo(xx[0], yy[0]);
            int n_points = xx.length;
            for (int i=1; i<n_points; i++) p.lineTo(xx[i], yy[i]);
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mSignature == null) {
            canvas.drawColor(0);
        } else {
            if (nTracks == 0) {
                calculateDrawings(getWidth(), getHeight());
            }
            for (int i=0; i < nTracks; i++) {
                canvas.drawPath(mPPP[i], paint);
            }
        }
    }
}
