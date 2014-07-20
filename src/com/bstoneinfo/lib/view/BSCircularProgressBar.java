package com.bstoneinfo.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.bstoneinfo.lib.ui.BSActivity;

/**
 * 圆环进度条
 * <p/>
 * by bojia 2014-04-17
 */
public class BSCircularProgressBar extends View {

    private float startAngle = -90.0f;
    private float sweepAngle = 0f;
    private int barWidth = 4;
    private int barBackColor = Color.parseColor("#d8d7d7");
    private int barForeColor = Color.RED;
    private int viewBackColor = Color.WHITE;
    protected Paint paint = new Paint();
    private PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    public BSCircularProgressBar(Context context) {
        super(context);
    }

    public BSCircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * setBarWidth: 设置进度条宽度，默认是 4dp.
     * <p/>
     * 
     * @param barWidth 进度条宽度
     * @return void
     */
    public void setBarWidth(int barWidth) {
        this.barWidth = BSActivity.dip2px(barWidth);
        invalidate();
    }

    /**
     * setSweepAngle: 设置进度条进度 （0-360）.
     * <p/>
     * 
     * @param sweepAngle 进度条进度
     * @return void
     */
    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle > 360 ? 360 : sweepAngle;
        this.sweepAngle = this.sweepAngle < 0 ? 0 : this.sweepAngle;
        invalidate();
    }

    /**
     * setBarBackColor: 设置进度条背景色，默认是 #d8d7d7.
     * <p/>
     * 
     * @param barBackColor 进度条背景色
     * @return void
     */
    public void setBarBackColor(int barBackColor) {
        this.barBackColor = barBackColor;
        invalidate();
    }

    /**
     * setBarForeColor: 设置进度条前景色，默认是 Color.RED.
     * <p/>
     * 
     * @param barForeColor 进度条前景色
     * @return void
     */
    public void setBarForeColor(int barForeColor) {
        this.barForeColor = barForeColor;
        invalidate();
    }

    /**
     * setViewBackColor: 设置进度条内圆背景色，默认是 Color.WHITE,可设置为 Color.TRANSPARENT.
     * <p/>
     * 
     * @param viewBackColor 进度条内圆背景色
     * @return void
     */
    public void setViewBackColor(int viewBackColor) {
        this.viewBackColor = viewBackColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        if (isViewBackTransparent()) {
            int sc = canvas.saveLayer(getLeft(), getTop(), getRight(), getBottom(), null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                    | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
            drawArc(canvas, paint, barBackColor, 0, 360);
            drawArc(canvas, paint, barForeColor, 0, sweepAngle);
            paint.setXfermode(porterDuffXfermode);
            drawArc(canvas, paint, Color.WHITE, barWidth, 360);
            paint.setXfermode(null);
            canvas.restoreToCount(sc);
        } else {
            drawArc(canvas, paint, barBackColor, 0, 360);
            drawArc(canvas, paint, barForeColor, 0, sweepAngle);
            drawArc(canvas, paint, viewBackColor, barWidth, 360);

        }
    }

    private boolean isViewBackTransparent() {
        return viewBackColor == Color.TRANSPARENT;
    }

    private void drawArc(Canvas canvas, Paint paint, int color, int offset, float sweepAngle) {
        paint.setColor(color);
        RectF oval = new RectF(getLeft() + offset, getTop() + offset, getRight() - offset, getBottom() - offset);
        canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
    }
}
