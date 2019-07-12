package com.willpower.editor.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class CustomRectView extends BaseView {
    private static final int boxColor = Color.WHITE;

    private CustomRectView(Context context, Rect boundary) {
        super(context);
        helper = new EditorHelper(boundary);
    }

    public static CustomRectView createView(Context context, Rect boundary) {
        Log.e("Custom->背景框", "left: " + boundary.left + "top:" + boundary.top + "right:" + boundary.right + "bottom:" + boundary.bottom);
        return new CustomRectView(context, boundary);
    }

    @Override
    protected void setPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        paint.setColor(boxColor);
        paint.setStyle(Paint.Style.FILL);
        setTextSize(10);
        setBackgroundColor(Color.parseColor("#20000000"));
    }

    @Override
    protected void drawBox(Canvas canvas) {
        canvas.drawCircle(5, 5, 5, paint);
        canvas.drawCircle(getWidth() - 5, 5, 5, paint);
        canvas.drawCircle(getWidth() - 5, getHeight() - 5, 5, paint);
        canvas.drawCircle(5, getHeight() - 5, 5, paint);
        canvas.drawLine(5, 5, getWidth() - 5, 5, paint);
        canvas.drawLine(getWidth() - 5, 5, getWidth() - 5, getHeight() - 5, paint);
        canvas.drawLine(getWidth() - 5, getHeight() - 5, 5, getHeight() - 5, paint);
        canvas.drawLine(5, getHeight() - 5, 5, 5, paint);
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
