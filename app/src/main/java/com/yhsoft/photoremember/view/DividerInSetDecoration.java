package com.yhsoft.photoremember.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yhsoft.photoremember.R;

import static android.support.v7.widget.RecyclerView.ItemDecoration;
import static android.support.v7.widget.RecyclerView.LayoutParams;
import static android.support.v7.widget.RecyclerView.State;
import static com.yhsoft.photoremember.R.dimen;
import static com.yhsoft.photoremember.R.dimen.card_insets;
import static com.yhsoft.photoremember.R.drawable;
import static com.yhsoft.photoremember.R.drawable.line_divider;

/**
 * Created by James on 1/10/15.
 */
public class DividerInSetDecoration extends ItemDecoration {
    private Drawable mDivider;
    private int mInsets;

    public DividerInSetDecoration(Context context) {
        mDivider = context.getResources().getDrawable(line_divider);
        mInsets = context.getResources().getDimensionPixelSize(card_insets);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        outRect.bottom = mInsets;
        outRect.left = mInsets;
        outRect.right = mInsets;
        if (parent.getChildPosition(view) == 0) {
            outRect.top = mInsets;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            LayoutParams params = (LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
