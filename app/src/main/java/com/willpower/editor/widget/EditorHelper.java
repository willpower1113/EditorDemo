package com.willpower.editor.widget;

import android.graphics.Rect;

import static com.willpower.editor.widget.BaseView.BOTTOM_CHANGE;
import static com.willpower.editor.widget.BaseView.LEFT_CHANGE;
import static com.willpower.editor.widget.BaseView.NONE;
import static com.willpower.editor.widget.BaseView.RIGHT_CHANGE;
import static com.willpower.editor.widget.BaseView.TOP_CHANGE;

public class EditorHelper {

    public int nearBy = 30;

    public Rect boundary;

    public int leftBoundary, topBoundary;

    public EditorHelper(Rect boundary) {
        this.boundary = boundary;
        leftBoundary = boundary.width() - (2 * nearBy);
        topBoundary = boundary.height() - (2 * nearBy);
    }

    /**
     * 边界检查
     *
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public synchronized Rect checkBoundary(int l, int t, int r, int b) {
        if (boundary != null) {
            if (r > boundary.width()) {
                l -= (r - boundary.width());
                r = boundary.width();
            }
            if (b > boundary.height()) {
                t -= (b - boundary.height());
                b = boundary.height();
            }
            if (l < 0) {
                r -= l;
                l = 0;
            }
            if (t < 0) {
                b -= t;
                t = 0;
            }
        }
        if (l > (r - (2 * nearBy))) {
            l = r - (2 * nearBy);
        }
        if (t > (b - (2 * nearBy))) {
            t = b - (2 * nearBy);
        }
        return new Rect(l, t, r, b);
    }


    /**
     * 检查是否在边界
     *
     * @param x
     * @param y
     */
    public int[] checkTouchBoundary(float x, float y, Rect outSide, int[] actions) {
        if (x >= -nearBy && x <= nearBy) {
            actions[0] = LEFT_CHANGE;
        } else if (x >= (outSide.width() - nearBy) && x <= (outSide.width() + nearBy)) {
            actions[0] = RIGHT_CHANGE;
        } else {
            actions[0] = NONE;
        }
        if (y >= -nearBy && y <= nearBy) {
            actions[1] = TOP_CHANGE;
        } else if (y >= (outSide.height() - nearBy) && y <= (outSide.height() + nearBy)) {
            actions[1] = BOTTOM_CHANGE;
        } else {
            actions[1] = NONE;
        }
        return actions;
    }

}
