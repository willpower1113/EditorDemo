package com.willpower.editor.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Project implements Serializable{

    private String projectName;

    private ArrayList<Page> pages;

    private long created;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
