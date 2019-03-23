package com.DDebbie.util;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;

/**
 * Created by appsplanet on 25/1/16.
 */
public class MenuArrowDrawable extends DrawerArrowDrawable {

    private final ValueAnimator mMenuToArrowAnimator;
    private final ValueAnimator mArrowToMenuAnimator;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MenuArrowDrawable(Context context) {
        super(context);

        ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setPosition((float)animation.getAnimatedValue());
            }
        };

        mMenuToArrowAnimator = ValueAnimator.ofFloat(0f, 1f);
        mMenuToArrowAnimator.setDuration(250);
        mMenuToArrowAnimator.addUpdateListener(animatorUpdateListener);

        mArrowToMenuAnimator = ValueAnimator.ofFloat(1f, 0f);
        mArrowToMenuAnimator.setDuration(250);
        mArrowToMenuAnimator.addUpdateListener(animatorUpdateListener);
    }

    public void setPosition(float position) {
        if (position >= 1f) {
            setVerticalMirror(true);
        } else if (position <= 0f) {
            setVerticalMirror(false);
        }
        setProgress(position);
    }

    public float getPosition() {
        return getProgress();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void animateDrawable(boolean menuToArrow) {
        if (menuToArrow && getPosition() >= 1f) return;
        if (!menuToArrow && getPosition() <= 0f) return;

        ValueAnimator animator = menuToArrow? mMenuToArrowAnimator : mArrowToMenuAnimator;
        if (animator.isRunning()) animator.end();
        animator.start();
    }
}
