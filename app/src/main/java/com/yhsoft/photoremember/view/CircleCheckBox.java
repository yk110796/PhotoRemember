package com.yhsoft.photoremember.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import static android.R.integer.config_shortAnimTime;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.alpha;
import static android.graphics.Color.argb;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;
import static com.yhsoft.photoremember.R.styleable;
import static com.yhsoft.photoremember.R.styleable.CircleCheckBox_cb_color;
import static com.yhsoft.photoremember.R.styleable.CircleCheckBox_cb_pressed_ring_width;
import static java.lang.Math.min;

public class CircleCheckBox extends AppCompatCheckBox {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
    private static final int PRESSED_RING_ALPHA = 75;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
    private static final int ANIMATION_TIME_ID = config_shortAnimTime;

    private int centerY;
    private int centerX;
    private int outerRadius;
    private int pressedRingRadius;

    private Paint circlePaint;
    private Paint focusPaint;

    private float animationProgress;

    private int pressedRingWidth;
    private int defaultColor = BLACK;
    private int pressedColor;
//    private ObjectAnimator pressedAnimator;

    public CircleCheckBox(Context context) {
        super(context);
        init(context, null);
    }

    public CircleCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (circlePaint != null) {
            circlePaint.setColor(pressed ? pressedColor : defaultColor);
        }

        if (pressed) {
            showPressedRing();
        } else {
            hidePressedRing();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
        //canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = min(w, h) / 2;
        pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
        this.invalidate();
    }

    public void setColor(int color) {
        this.defaultColor = color;
        this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

        circlePaint.setColor(defaultColor);
        circlePaint.setAlpha(PRESSED_RING_ALPHA);
        focusPaint.setColor(defaultColor);
        //focusPaint.setAlpha(PRESSED_RING_ALPHA);

        this.invalidate();
    }

    private void hidePressedRing() {
//        pressedAnimator.setFloatValues(pressedRingWidth, 0f);
//        pressedAnimator.start();
    }

    private void showPressedRing() {
//        pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
//        pressedAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        //this.setFocusable(true);
        //this.setScaleType(ScaleType.CENTER_INSIDE);
        //setClickable(true);

        circlePaint = new Paint(ANTI_ALIAS_FLAG);
        circlePaint.setStyle(FILL);

        focusPaint = new Paint(ANTI_ALIAS_FLAG);
        focusPaint.setStyle(STROKE);

        pressedRingWidth = (int) applyDimension(COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());

        int color = BLACK;
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, styleable.CircleCheckBox);
            color = a.getColor(CircleCheckBox_cb_color, color);
            pressedRingWidth = (int) a.getDimension(CircleCheckBox_cb_pressed_ring_width, pressedRingWidth);
            a.recycle();
        }

        setColor(color);

        focusPaint.setStrokeWidth(pressedRingWidth);
        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
//        pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
//        pressedAnimator.setDuration(pressedAnimationTime);
    }

    private int getHighlightColor(int color, int amount) {
        return argb(min(255, alpha(color)), min(255, red(color) + amount),
                min(255, green(color) + amount), min(255, blue(color) + amount));
    }
}