package com.willpower.editor.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.willpower.editor.entity.Frame;

public abstract class BaseView extends AppCompatTextView implements GestureDetector.OnGestureListener {
    /*
      横向，纵向拉伸对应值
       */
    public static final int LEFT_CHANGE = 1;
    public static final int TOP_CHANGE = 2;
    public static final int RIGHT_CHANGE = 3;
    public static final int BOTTOM_CHANGE = 4;
    public static final int NONE = -1;

    protected Paint paint;

    protected float downX;
    protected float downY;

    protected Rect outSide;

    protected EditorHelper helper;

    protected float moveX = 0, moveY = 0;

    private int[] actions;/*横向，纵向拉伸状态*/

    private GestureDetector detector;

    private OnCustomClickListener listener;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        detector = new GestureDetector(this);
        actions = new int[2];
        setPaint();
    }

    protected abstract void setPaint();

    protected abstract void drawBox(Canvas canvas);

    protected abstract void clearStates();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        outSide = new Rect(getLeft(), getTop(), getRight(), getBottom());
        drawBox(canvas);
    }

    /*
    保存位置
     */
    protected void saveLocation() {
        Log.e("Custom->保存位置", "left: " + outSide.left + "top:" + outSide.top + "right:" + outSide.right + "bottom:" + outSide.bottom);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.width = outSide.right - outSide.left;
        params.height = outSide.bottom - outSide.top;
        params.leftMargin = outSide.left;
        params.topMargin = outSide.top;
        setLayoutParams(params);
        try {
            Frame frame = (Frame) getTag();
            frame.setInfo(new Frame.Rect(outSide));
        } catch (Exception e) {
        }
    }

    /*
    更新并显示
     */
    private void requestMyLayout() {
        layout(outSide.left, outSide.top, outSide.right, outSide.bottom);
        postInvalidate();
    }

    /*
   拖动
    */
    private void drag(float moveX, float moveY) {
        outSide = helper.checkBoundary((int) (outSide.left + moveX), (int) (outSide.top + moveY), (int) (outSide.right + moveX), (int) (outSide.bottom + moveY));
        requestMyLayout();
    }

    /*
    改变大小
     */
    private void change(float mX, float mY) {
        final float x = mX - this.moveX;
        final float y = mY - this.moveY;

        this.moveX = mX;
        this.moveY = mY;

        outSide = helper.checkBoundary((int) (actions[0] == LEFT_CHANGE ? (outSide.left + x) : outSide.left), (int) (actions[1] == TOP_CHANGE ? (outSide.top + y) : outSide.top)
                , (int) (actions[0] == RIGHT_CHANGE ? (outSide.right + x) : outSide.right), (int) (actions[1] == BOTTOM_CHANGE ? (outSide.bottom + y) : outSide.bottom));
        requestMyLayout();
    }

    /*
    是否拖拽边界
     */
    private boolean hasAction() {
        return actions[0] != NONE || actions[1] != NONE;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.downX = event.getX();
                this.downY = event.getY();
                /*检测触控区域是否在边界*/
                actions = helper.checkTouchBoundary(event.getX(), event.getY(), outSide, actions);
                if (hasAction()) {
                    paint.setColor(Color.GREEN);
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - downX;
                float dy = event.getY() - downY;
                if (hasAction()) {
                    change(dx, dy);
                } else {
                    drag(dx, dy);
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
                clearStates();
                break;
            case MotionEvent.ACTION_UP:
                clearStates();
                break;
        }
        return detector.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (listener != null) {
            listener.onClick(false, this);
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (listener != null) {
            listener.onClick(true, this);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /*
   对外提供获取坐标
    */
    public Rect getLocation() {
        return outSide;
    }

    /*
    设置监听
     */
    public void setListener(OnCustomClickListener listener) {
        this.listener = listener;
    }
}
