package com.yhsoft.photoremember.view.multirecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.integer;
import static android.R.integer.config_shortAnimTime;
import static android.support.v7.widget.RecyclerView.OnItemTouchListener;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_INDEX_SHIFT;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.VelocityTracker.obtain;
import static android.view.ViewConfiguration.get;
import static com.yhsoft.photoremember.view.multirecyclerview.SwipeToDismissTouchListener.SwipeDirection.LEFT;
import static com.yhsoft.photoremember.view.multirecyclerview.SwipeToDismissTouchListener.SwipeDirection.NONE;
import static com.yhsoft.photoremember.view.multirecyclerview.SwipeToDismissTouchListener.SwipeDirection.RIGHT;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.sort;

/**
 * Based on Roman Nurik's Android-SwipeToDismiss lib <a href="https://github.com/romannurik/Android-SwipeToDismiss">https://github.com/romannurik/Android-SwipeToDismiss</a>
 * <p/>
 * RecyclerView.OnItemTouchListener that allows items to be swiped and dismissed.
 * <p/>
 * Typical usage:
 * <p/>
 * <pre>
 * {@code
 * swipeToDismissTouchListener = new SwipeToDismissTouchListener(recyclerView, new SwipeToDismissTouchListener.DismissCallbacks() {
 *           @Override
 *          public SwipeToDismissTouchListener.SwipeDirection canDismiss(int position) {
 *              return SwipeToDismissTouchListener.SwipeDirection.RIGHT;
 *          }
 *           @Override
 *          public void onDismiss(RecyclerView view, List<SwipeToDismissTouchListener.PendingDismissData> dismissData) {
 *             for (SwipeToDismissTouchListener.PendingDismissData data : dismissData) {
 *                 adapter.removeItem(data.position);
 *                 adapter.notifyItemRemoved(data.position);
 *             }
 *          }
 *  });
 *
 * }
 * </pre>
 */
public class SwipeToDismissTouchListener implements OnItemTouchListener {

    private final RecyclerView mRecyclerView;
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;

    private DismissCallbacks mCallbacks;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private float mTranslationX;
    private boolean mPaused = false;
    private View mSwipeView;
    private int mDismissCount = 0;
    private List<PendingDismissData> mPendingDismisses = new ArrayList<PendingDismissData>();
    private SwipeDirection mAllowedSwipeDirection = NONE;


    /**
     * Constructs a new swipe-to-dismiss OnItemTouchListener for RecyclerView
     *
     * @param recyclerView RecyclerView
     * @param callbacks    The callback to trigger when the user has indicated that she would like to
     *                     dismiss this view.
     */
    public SwipeToDismissTouchListener(RecyclerView recyclerView, DismissCallbacks callbacks) {
        ViewConfiguration vc = get(recyclerView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 4;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = recyclerView.getContext().getResources().getInteger(config_shortAnimTime);
        mRecyclerView = recyclerView;
        mCallbacks = callbacks;
    }

    public void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        motionEvent.offsetLocation(mTranslationX, 0);

        switch (motionEvent.getActionMasked()) {
            case ACTION_UP: {
                up(motionEvent);
                break;
            }

            case ACTION_CANCEL: {
                cancel();
                break;
            }

            case ACTION_MOVE: {
                move(motionEvent);
                break;
            }
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView view, MotionEvent motionEvent) {
        if (mPaused) return false;
        // offset because the view is translated during swipe
        motionEvent.offsetLocation(mTranslationX, 0);

        if (mViewWidth < 2) {
            mViewWidth = view.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case ACTION_DOWN: {
                return down(motionEvent);
            }
            case ACTION_MOVE: {
                return move(motionEvent);

            }

        }
        return false;
    }

    private boolean down(MotionEvent motionEvent) {
        if (mPaused) return false;

        mDownX = motionEvent.getRawX();
        mDownY = motionEvent.getRawY();
        mSwipeView = mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (mSwipeView == null) return false;
        int pos = mRecyclerView.getChildPosition(mSwipeView);
        mAllowedSwipeDirection = mCallbacks.canDismiss(pos);
        if (mAllowedSwipeDirection != NONE) {

            mVelocityTracker = obtain();
            mVelocityTracker.addMovement(motionEvent);
            return false;
        }
        resetMotion();
        return false;
    }

    private void cancel() {
        if (mVelocityTracker == null) {
            return;
        }

        mSwipeView.animate()
                .translationX(0)
                .alpha(1)
                .setDuration(mAnimationTime)
                .setListener(null);
        mVelocityTracker.recycle();
        mVelocityTracker = null;
        mTranslationX = 0;
        mDownX = 0;
        mDownY = 0;
        mSwiping = false;
        mSwipeView = null;
    }

    private void up(MotionEvent motionEvent) {
        if (mPaused || mVelocityTracker == null || mSwipeView == null) {
            return;
        }
        mSwipeView.setPressed(false);
        float deltaX = motionEvent.getRawX() - mDownX;
        mVelocityTracker.addMovement(motionEvent);
        mVelocityTracker.computeCurrentVelocity(1000);
        float velocityX = mVelocityTracker.getXVelocity();
        float absVelocityX = abs(velocityX);
        float absVelocityY = abs(mVelocityTracker.getYVelocity());
        boolean dismiss = false;
        boolean dismissRight = false;
        if (abs(deltaX) > mViewWidth / 2 && mSwiping) {
            dismiss = true;
            dismissRight = deltaX > 0;
        } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                && absVelocityY < absVelocityX
                && absVelocityY < absVelocityX && mSwiping) {
            // dismiss only if flinging in the same direction as dragging
            dismiss = (velocityX < 0) == (deltaX < 0);
            dismissRight = mVelocityTracker.getXVelocity() > 0;
        }
        if (dismiss) {
            // dismiss
            final int pos = mRecyclerView.getChildPosition(mSwipeView);
            final View swipeViewCopy = mSwipeView;
            final SwipeDirection swipeDirection = dismissRight ? RIGHT : LEFT;
            ++mDismissCount;
            mSwipeView.animate()
                    .translationX(dismissRight ? mViewWidth : -mViewWidth)
                    .alpha(0)
                    .setDuration(mAnimationTime);

            //this is instead of unreliable onAnimationEnd callback
            swipeViewCopy.postDelayed(new Runnable() {
                @Override
                public void run() {
                    performDismiss(swipeViewCopy, pos, swipeDirection);
                    swipeViewCopy.setTranslationX(0);
//                    swipeViewCopy.setAlpha(1);

                }
            }, mAnimationTime + 100);

        } else if (mSwiping) {
            // cancel
            mSwipeView.animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(mAnimationTime)
                    .setListener(null);
        }


        resetMotion();
    }

    private boolean move(MotionEvent motionEvent) {
        if (mSwipeView == null || mVelocityTracker == null || mPaused) {
            return false;
        }

        mVelocityTracker.addMovement(motionEvent);
        float deltaX = motionEvent.getRawX() - mDownX;
        float deltaY = motionEvent.getRawY() - mDownY;
        if (abs(deltaX) > mSlop && abs(deltaY) < abs(deltaX) / 2) {
            mSwiping = true;
            mSwipingSlop = (deltaX > 0 ? mSlop : -mSlop);
            mSwipeView.setPressed(false);

            MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
            cancelEvent.setAction(ACTION_CANCEL | (motionEvent.getActionIndex() << ACTION_POINTER_INDEX_SHIFT));
            mSwipeView.onTouchEvent(cancelEvent);
        }

        //Prevent swipes to disallowed directions
        if ((deltaX < 0 && mAllowedSwipeDirection == RIGHT) || (deltaX > 0 && mAllowedSwipeDirection == LEFT)) {
            resetMotion();
            return false;
        }

        if (mSwiping) {
            mTranslationX = deltaX;
            mSwipeView.setTranslationX(deltaX - mSwipingSlop);
            mSwipeView.setAlpha(max(0f, min(1f,
                    1f - 2f * abs(deltaX) / mViewWidth)));
            return true;
        }
        return false;
    }

    private void resetMotion() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        mTranslationX = 0;
        mDownX = 0;
        mDownY = 0;
        mSwiping = false;
        mSwipeView = null;
        mAllowedSwipeDirection = NONE;
    }

    private void performDismiss(View dismissView, int pos, SwipeDirection direction) {
        --mDismissCount;
        mPendingDismisses.add(new PendingDismissData(pos, dismissView, direction));
        if (mDismissCount == 0) {
            sort(mPendingDismisses);
            List<PendingDismissData> dismissData = new ArrayList<PendingDismissData>(mPendingDismisses);
            mCallbacks.onDismiss(mRecyclerView, dismissData);
            mPendingDismisses.clear();
        }
    }


    public interface DismissCallbacks {
        SwipeDirection canDismiss(int position);

        void onDismiss(RecyclerView view, List<PendingDismissData> dismissData);
    }


    public class PendingDismissData implements Comparable<PendingDismissData> {
        public int position;
        public View view;
        public SwipeDirection direction;

        public PendingDismissData(int position, View view, SwipeDirection direction) {
            this.position = position;
            this.view = view;
            this.direction = direction;

        }

        @Override
        public int compareTo(PendingDismissData other) {
            return other.position - position;
        }
    }

    public enum SwipeDirection {
        LEFT, RIGHT, BOTH, NONE
    }
}