package com.willpower.editor.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page implements Serializable {

    private long pageId;

    private String pageName;

    private String thumbnail;//背景图

    private long projectId;

    private List<Frame> frameList;

    public Page() {
    }

    public Page(long pageId, long projectId) {
        this.pageId = pageId;
        this.projectId = projectId;
    }

    public Frame getFrame(int position) {
        if (frameList == null || position >= frameList.size()) return null;
        return frameList.get(position);
    }

    public void addFrame(Frame frame) {
        if (frameList == null) {
            frameList = new ArrayList<>();
        }
        for (int i = 0; i < frameList.size(); i++) {
            if (frameList.get(i).getFrameId() == frame.getFrameId()) {
                frameList.set(i, frame);
                return;
            }
        }
        frameList.add(frame);
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public List<Frame> getFrameList() {
        return frameList;
    }

    public void setFrameList(List<Frame> frameList) {
        this.frameList = frameList;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

}
