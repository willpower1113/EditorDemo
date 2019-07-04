package com.willpower.editor.entity;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.io.Serializable;

public class Frame implements Serializable {
    public static final int MODE_BUTTON = 1;
    public static final int MODE_BOX = 2;

    private long FID;

    private int mode;/*类型 button ，box*/

    private String link;/*点击跳转*/

    private Rect info;/*坐标，大小*/

    private int action;//用途 眼跳，回看

    private long pageID;/*页面ID*/

    public long getFID() {
        return FID;
    }

    public void setFID(long FID) {
        this.FID = FID;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Rect getInfo() {
        return info;
    }

    public void setInfo(Rect info) {
        this.info = info;
    }

    public long getPageID() {
        return pageID;
    }

    public void setPageID(long pageID) {
        this.pageID = pageID;
    }

    public static class Rect implements Serializable {
        private int left;
        private int top;
        private int right;
        private int bottom;

        public Rect(android.graphics.Rect rect) {
            this.left = rect.left;
            this.top = rect.top;
            this.right = rect.right;
            this.bottom = rect.bottom;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public int getBottom() {
            return bottom;
        }

        public void setBottom(int bottom) {
            this.bottom = bottom;
        }
    }
}
