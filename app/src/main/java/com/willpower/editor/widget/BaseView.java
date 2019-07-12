package com.willpower.editor.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.willpower.editor.R;
import com.willpower.editor.entity.Frame;
import com.willpower.editor.utils.ValidUtils;
import com.willpower.log.Timber;

public abstract class BaseView extends AppCompatTextView implements GestureDetector.OnGestureListener {
    /*
      横向，纵向拉伸对应值
       */
    public static final int LEFT_CHANGE = 1;
    public static final int TOP_CHANGE = 2;
    public static final int RIGHT_CHANGE = 3;
    public static final int BOTTOM_CHANGE = 4;
    public static final int NONE = -1;

    protected static final int chooseSize = 40;

    protected OnChangeListener changeListener;

    protected Paint paint;

    protected int downX;
    protected int downY;

    protected Rect outSide;

    protected EditorHelper helper;

    protected int moveX = 0, moveY = 0;

    private int[] actions;/*横向，纵向拉伸状态*/

    private boolean isInfo = false;

    private boolean isChooseMode = false;

    private boolean isChoosed = false;

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
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setShadowLayer(4,2,2,Color.BLACK);
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
        if (isChooseMode()) {
            if (isChoosed) {
                canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_choose_choosed),
                        null, new RectF(20, 20,
                                20 + chooseSize, 20 + chooseSize), paint);
            } else {
                canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_choose_normal),
                        null, new RectF(20, 20,
                                20 + chooseSize, 20 + chooseSize), paint);
            }
        }
    }


    /*
    保存位置
     */
    protected void saveLocation() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.width = outSide.right - outSide.left;
        params.height = outSide.bottom - outSide.top;
        params.leftMargin = outSide.left;
        params.topMargin = outSide.top;
        setLayoutParams(params);
        try {
            Frame frame = (Frame) getTag();
            frame.setRect(outSide);
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
    private void drag(int mX, int mY) {
        final int x = mX - this.moveX;
        final int y = mY - this.moveY;

        this.moveX = mX;
        this.moveY = mY;
        Timber.e("移动：moveX：" + moveX + "moveY：" + moveY);
        outSide = helper.checkBoundary(outSide.left + x, outSide.top + y, outSide.right + x, outSide.bottom + y);
        requestMyLayout();
    }

    /*
    改变大小
     */
    private void change(int mX, int mY) {
        final int x = mX - this.moveX;
        final int y = mY - this.moveY;

        this.moveX = mX;
        this.moveY = mY;

        if (actions[0] == LEFT_CHANGE) {
            outSide.left += x;
        } else if (actions[0] == RIGHT_CHANGE) {
            outSide.right += x;
        }
        if (actions[1] == TOP_CHANGE) {
            outSide.top += y;
        } else if (actions[1] == BOTTOM_CHANGE) {
            outSide.bottom += y;
        }
        outSide = helper.checkBoundary(outSide.left, outSide.top, outSide.right, outSide.bottom);
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
                this.downX = (int) event.getRawX();
                this.downY = (int) event.getRawY();
                /*检测触控区域是否在边界*/
                actions = helper.checkTouchBoundary(event.getX(), event.getY(), outSide, actions);
                if (hasAction()) {
                    paint.setColor(Color.GREEN);
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getRawX() - this.downX);
                int dy = (int) (event.getRawY() - this.downY);
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
    设置监听
     */
    public void setListener(OnCustomClickListener listener) {
        this.listener = listener;
    }

    public boolean isInfo() {
        return isInfo;
    }

    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
        Frame frame = (Frame) getTag();
        if (frame != null && isInfo) {
            setText(ValidUtils.getFrameText(frame, isInfo));
        }
    }

    public void refreshText() {
        setText(ValidUtils.getFrameText((Frame) getTag(), isInfo));
    }

    public void setInfo(boolean isInfo) {
        this.isInfo = isInfo;
    }

    public boolean isChooseMode() {
        return isChooseMode;
    }

    public void setChooseMode(boolean chooseMode) {
        isChooseMode = chooseMode;
        postInvalidate();
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
        postInvalidate();
    }

    public interface OnChangeListener{
        void onChange();
    }

    public void setChangeListener(OnChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}
