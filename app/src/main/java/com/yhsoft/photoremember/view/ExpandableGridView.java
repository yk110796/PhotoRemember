package com.yhsoft.photoremember.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static java.lang.Integer.MAX_VALUE;

public class ExpandableGridView extends GridView {

    public ExpandableGridView(Context context) {
        super(context);
    }

    public ExpandableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableGridView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
        int expandSpec = makeMeasureSpec(MAX_VALUE >> 2, AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
        getLayoutParams().height = getMeasuredHeight();
    }
}
