package com.willpower.editor.entity;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page implements Serializable {

    private long pageID;

    private String pageName;

    private Bitmap thumbnail;//缩略图

    private List<Frame> frameList;

    public Frame getFrame(int position) {
        if (frameList == null || position >= frameList.size()) return null;
        return frameList.get(position);
    }

    public void addFrame(Frame frame) {
        if (frameList == null) {
            frameList = new ArrayList<>();
        }
        for (int i = 0; i < frameList.size(); i++) {
            if (frameList.get(i).getFID() == frame.getFID()) {
                frameList.set(i, frame);
                return;
            }
        }
        frameList.add(frame);
    }

    public List<Frame> getFrameList() {
        return frameList;
    }

    public void setFrameList(List<Frame> frameList) {
        this.frameList = frameList;
    }

    public long getPageID() {
        return pageID;
    }

    public void setPageID(long pageID) {
        this.pageID = pageID;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageID=" + pageID +
                ", pageName='" + pageName + '\'' +
                ", thumbnail=" + thumbnail +
                ", frameList=" + frameList +
                '}';
    }
}
