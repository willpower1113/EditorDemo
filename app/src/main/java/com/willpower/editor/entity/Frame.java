package com.willpower.editor.entity;

import android.graphics.Rect;

import java.io.Serializable;

public class Frame implements Serializable {
    public static final int MODE_BUTTON = 1;
    public static final int MODE_BOX = 2;

    private long frameId;

    private int frameMode;/*类型 button ，box*/

    private long linkPage;/*点击跳转*/

    private int frameAction;//用途 眼跳 = 1，回看 = 2, 默认0

    private long pageId;/*页面ID*/

    private long projectId;/*项目ID*/

    private int loc_left;/*坐标*/

    private int loc_top;

    private int loc_right;

    private int loc_bottom;

    private long groupId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getFrameId() {
        return frameId;
    }

    public void setFrameId(long frameId) {
        this.frameId = frameId;
    }

    public int getAction() {
        return frameAction;
    }

    public void setAction(int action) {
        this.frameAction = action;
    }

    public int getMode() {
        return frameMode;
    }

    public void setMode(int mode) {
        this.frameMode = mode;
    }

    public long getLinkPage() {
        return linkPage;
    }

    public void setLinkPage(long linkPage) {
        this.linkPage = linkPage;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public void setRect(Rect rect) {
        this.loc_left = rect.left;
        this.loc_top = rect.top;
        this.loc_right = rect.right;
        this.loc_bottom = rect.bottom;
    }

    public int getLoc_left() {
        return loc_left;
    }

    public void setLoc_left(int loc_left) {
        this.loc_left = loc_left;
    }

    public int getLoc_top() {
        return loc_top;
    }

    public void setLoc_top(int loc_top) {
        this.loc_top = loc_top;
    }

    public int getLoc_Right() {
        return loc_right;
    }

    public void setLoc_Right(int right) {
        this.loc_right = right;
    }

    public int getLoc_Bottom() {
        return loc_bottom;
    }

    public void setLoc_Bottom(int bottom) {
        this.loc_bottom = bottom;
    }
}
