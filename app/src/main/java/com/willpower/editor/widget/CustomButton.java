package com.willpower.editor.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CustomButton extends BaseView{

    private static final int boxColor = Color.GRAY;

    private CustomButton(Context context, Rect boundary) {
        super(context);
        helper = new EditorHelper(boundary);
    }

    public static CustomButton createView(Context context, Rect boundary) {
        Log.e("Custom->背景框", "left: " + boundary.left + "top:" + boundary.top + "right:" + boundary.right + "bottom:" + boundary.bottom);
        return new CustomButton(context, boundary);
    }

    @Override
    public void setPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(1);
        paint.setColor(boxColor);
        paint.setStyle(Paint.Style.STROKE);
        setText("Button");
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void drawBox(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }

    @Override
    protected void clearStates() {
        paint.setColor(boxColor);
        postInvalidate();
        this.moveX = 0;
        this.moveY = 0;
        saveLocation();
    }
}
