package com.bstoneinfo.lib.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bstoneinfo.lib.common.BSUtils;

public class BSNavigationBar {

    private final TextView textView;
    private final LinearLayout leftLayout;
    private final LinearLayout rightLayout;
    private ImageView backbutton;
    final ViewGroup rootView;
    BSViewController viewController;

    int heightDip = 50;
    int backgroundColor = Color.LTGRAY;
    int backgroundResource = 0;
    int backButtonImageResource = 0;
    int titleTextSizeDip = 24;
    int titleTextColor = Color.BLACK;
    float titleShadowRadius = 0;
    float titleShadowDx = 0;
    float titleShadowDy = 0;
    int titleShadowColor = Color.TRANSPARENT;

    BSNavigationBar(Context context) {
        rootView = new LinearLayout(context);
        rootView.setClickable(true);

        leftLayout = new LinearLayout(context);
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        rootView.addView(leftLayout, params);

        textView = new TextView(context);
        textView.setSingleLine();
        textView.getPaint().setFakeBoldText(true);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = params.rightMargin = BSActivity.dip2px(10);
        rootView.addView(textView, params);

        rightLayout = new LinearLayout(context);
        rightLayout.setOrientation(LinearLayout.HORIZONTAL);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        rootView.addView(rightLayout, params);

        restoreToFractoryDefault();
    }

    void setViewController(BSViewController viewController) {
        this.viewController = viewController;
    }

    public int getHeight() {
        return BSActivity.dip2px(heightDip);
    }

    public void restoreToFractoryDefault() {

        if (backgroundResource > 0) {
            rootView.setBackgroundResource(backgroundResource);
        } else {
            rootView.setBackgroundColor(backgroundColor);
        }
        setBackButtonImageResource(backButtonImageResource);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleTextSizeDip);
        textView.setTextColor(titleTextColor);
        textView.setShadowLayer(titleShadowRadius, titleShadowDx, titleShadowDy, titleShadowColor);

    }

    public void setBackgroundColor(int color) {
        rootView.setBackgroundColor(color);
    }

    public void setBackgroundResource(int resid) {
        rootView.setBackgroundResource(resid);
    }

    public void setBackButton() {
        backbutton = new ImageView(rootView.getContext());
        backbutton.setImageResource(backButtonImageResource);
        leftLayout.removeAllViews();
        leftLayout.addView(backbutton);
        backbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BSUtils.isFastDoubleClick() || viewController == null) {
                    return;
                }
                //                if (viewController == viewController.getNavigationController().getRootViewController()) {
                //                    viewController.getParentViewController().removeFromParentViewController(true);
                //                } else {
                //                    viewController.removeFromParentViewController(true);
                //                }
            }
        });
    }

    public void setBackButtonImageResource(int resid) {
        if (backbutton != null) {
            backbutton.setImageResource(resid);
        }
    }

    public void setTitle(String title) {
        textView.setText(title);
    }

    public void setTitleTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setTitleTextSize(int dip) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dip);
    }

    public void setTitleShadowLayer(float radius, float dx, float dy, int color) {
        textView.setShadowLayer(radius, dx, dy, color);
    }

    public void addRightButton(View view) {
        rightLayout.addView(view);
    }

}
