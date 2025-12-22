package com.example.drinkhelper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class WaterProgressBar extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF arcRect;
    private float strokeWidth;
    private int max = 100;
    private int progress = 0;

    public WaterProgressBar(Context context) {
        super(context);
        init();
    }

    public WaterProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaterProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WaterProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        strokeWidth = dpToPx(15f);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setColor(0xFFE0E0E0);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(0xFF33CCCC);
        arcRect = new RectF();
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    public void setMax(int max) {
        if (max <= 0) {
            max = 1;
        }
        this.max = max;
        invalidate();
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        }
        if (progress > max) {
            progress = max;
        }
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float left = getPaddingLeft() + strokeWidth / 2f;
        float top = getPaddingTop() + strokeWidth / 2f;
        float right = w - getPaddingRight() - strokeWidth / 2f;
        float bottom = h - getPaddingBottom() - strokeWidth / 2f;
        arcRect.set(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float startAngle = -90f;
        canvas.drawArc(arcRect, startAngle, 360f, false, backgroundPaint);
        float sweepAngle = 0f;
        if (max > 0) {
            sweepAngle = 360f * progress / (float) max;
        }
        canvas.drawArc(arcRect, startAngle, sweepAngle, false, progressPaint);
    }
    

    
}
