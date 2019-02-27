package com.yhsoft.photoremember.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import static android.view.View.MeasureSpec.getSize;
import static java.lang.Math.ceil;

/**
 * Created by James on 2/26/15.
 */
public class ResizeImageView extends AppCompatImageView {

    public ResizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();

        if (d != null) {
            int width = getSize(widthMeasureSpec);
            int height = (int) ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}