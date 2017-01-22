package com.lweynant.yearly.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ScrollingView;
import android.util.AttributeSet;
import android.view.View;

import timber.log.Timber;


//solution based on answer from stackoverflow
// http://stackoverflow.com/questions/30923889/flinging-with-recyclerview-appbarlayout
public final class FlingBehavior extends AppBarLayout.Behavior {

    private static final int TOP_CHILD_FLING_THRESHOLD = 3;
    private boolean isPositive;

    public FlingBehavior() {
    }

    public FlingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        Timber.d("fling: velocity: %s, ", Float.toString(velocityY));

        if (velocityY > 0 && !isPositive || velocityY < 0 && isPositive) {
            velocityY = velocityY * -1;
        }
        if (consumed && velocityY < 0) {
            //we are scrolling down
            if (target instanceof ScrollingView){
                consumed = false; //as soon as we start flinging down we will bring down the toolbar
            }
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        isPositive = dy > 0;
    }
}
