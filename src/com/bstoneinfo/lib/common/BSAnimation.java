package com.bstoneinfo.lib.common;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BSAnimation {

    private final AnimationSet animation = new AnimationSet(true);
    private final View view;
    private final long durationMillis;
    private int fromOffsetX = 0, fromOffsetY = 0, toOffsetX = 0, toOffsetY = 0;
    private float fromScale = 1, toScale = 1;
    private float fromAlpha = 1, toAlpha = 1;
    private boolean endPositionDidSet;
    private Runnable endListener;

    public BSAnimation(View view, long durationMillis) {
        this.view = view;
        this.durationMillis = durationMillis;
    }

    public void setInterpolator(Interpolator i) {
        animation.setInterpolator(i);
    }

    public void setEndListener(Runnable callback) {
        endListener = callback;
    }

    public void addTranslateAnimationToAbsolute(final int centerToX, final int centerToY) {
        addTranslateAnimationToRelative(centerToX - (view.getLeft() + view.getRight()) / 2, centerToY - (view.getTop() + view.getBottom()) / 2);
    }

    public void addTranslateAnimationToRelative(int centerOffsetX, int centerOffsetY) {
        toOffsetX = centerOffsetX;
        toOffsetY = centerOffsetY;
        fromOffsetX = 0;
        fromOffsetY = 0;
        fromScale = 1;
    }

    public void addTranslateAnimationFromRelative(int centerOffsetX, int centerOffsetY) {
        fromOffsetX = centerOffsetX;
        fromOffsetY = centerOffsetY;
        toOffsetX = 0;
        toOffsetY = 0;
        toScale = 1;
    }

    public void addScaleAnimationFrom(float fromScale) {
        this.fromScale = fromScale;
    }

    public void addScaleAnimationTo(float toScale) {
        this.toScale = toScale;
    }

    public void addAlphaAnimation(float fromAlpha, final float toAlpha) {
        this.fromAlpha = fromAlpha;
        this.toAlpha = toAlpha;
    }

    private final AnimationListener androidAnimationListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            cancel();
            setEndAlpha();
            if (endListener != null) {
                endListener.run();
            }
        }
    };

    private void setEndPositionAnimationTo() {
        if (endPositionDidSet) {
            return;
        }
        endPositionDidSet = true;
        if (view.getParent() instanceof RelativeLayout) {
            int parentLeftPadding = ((ViewGroup) view.getParent()).getPaddingLeft();
            int parentTopPadding = ((ViewGroup) view.getParent()).getPaddingTop();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (view.getWidth() * toScale), (int) (view.getHeight() * toScale));
            int centerToX = (view.getLeft() + view.getRight()) / 2 + toOffsetX;
            int centerToY = (view.getTop() + view.getBottom()) / 2 + toOffsetY;
            params.leftMargin = centerToX - (int) (view.getWidth() * toScale / 2) - parentLeftPadding;
            params.topMargin = centerToY - (int) (view.getHeight() * toScale / 2) - parentTopPadding;
            view.setLayoutParams(params);
            view.setPadding((int) (toScale * view.getPaddingLeft()), (int) (toScale * view.getPaddingTop()), (int) (toScale * view.getPaddingRight()),
                    (int) (toScale * view.getPaddingBottom()));
        } else if (view.getParent() instanceof LinearLayout) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (((LinearLayout) view.getParent()).getOrientation() == LinearLayout.VERTICAL) {
                params.topMargin += toOffsetY;
            } else {
                params.leftMargin += toOffsetX;
            }
            view.setLayoutParams(params);
        }
    }

    @SuppressLint("NewApi")
    private void setEndAlpha() {
        if (fromAlpha != 1 || toAlpha != 1) {
            if (android.os.Build.VERSION.SDK_INT >= 11) {//android 3.0以上
                view.setAlpha(toAlpha);
            } else {
                if (toAlpha == 0) {
                    view.setVisibility(View.INVISIBLE);
                } else if (toAlpha == 1) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void start() {

        if (fromOffsetX != 0 || fromOffsetY != 0 || toOffsetX != 0 || toOffsetY != 0) {
            if (fromOffsetX != 0 || fromOffsetY != 0) {
                //TODO: 待实现
                //animation.addAnimation(new TranslateAnimation(fromOffsetX, 0, fromOffsetY, 0));
            } else if (toOffsetX != 0 || toOffsetY != 0) {
                animation.addAnimation(new TranslateAnimation((-toOffsetX + view.getWidth() * (toScale - 1) / 2) * toScale, 0, (-toOffsetY + view.getHeight() * (toScale - 1) / 2)
                        * toScale, 0));
                animation.addAnimation(new ScaleAnimation(1.0f / toScale, 1, 1.0f / toScale, 1));
            }
        } else if (fromScale != 1 || toScale != 1) {
            if (fromScale != 1) {
                animation.addAnimation(new ScaleAnimation(fromScale, 1, fromScale, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
            } else {
                animation.addAnimation(new ScaleAnimation(1.0f / toScale, 1, 1.0f / toScale, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
            }
        }

        if (!animation.getAnimations().isEmpty()) {
            setEndPositionAnimationTo();
        }

        if (fromAlpha != 1 || toAlpha != 1) {
            animation.addAnimation(new AlphaAnimation(fromAlpha, toAlpha));
        }
        animation.setDuration(durationMillis);
        animation.setAnimationListener(androidAnimationListener);
        view.setVisibility(View.INVISIBLE);
        view.startAnimation(animation);
    }

    public void finish() {
        setEndPositionAnimationTo();
        androidAnimationListener.onAnimationEnd(animation);
    }

    public void cancel() {
        animation.setAnimationListener(null);
        view.clearAnimation();
    }
}
