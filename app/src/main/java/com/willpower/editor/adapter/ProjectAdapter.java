package com.willpower.editor.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.willpower.editor.R;
import com.willpower.editor.entity.Project;

public class ProjectAdapter extends BaseQuickAdapter<Project, BaseViewHolder> {

    public ProjectAdapter() {
        super(R.layout.item_project);
    }

    @Override
    protected void convert(BaseViewHolder helper, Project item) {
        helper.setText(R.id.tvProjectName, item.getProjectName());
        helper.addOnClickListener(R.id.tvProjectName);
    }
}
