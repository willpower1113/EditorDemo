package com.willpower.editor.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.willpower.editor.R;
import com.willpower.editor.entity.Project;
import com.willpower.log.Timber;

import java.util.List;

public class ProjectAdapter extends BaseQuickAdapter<Project, BaseViewHolder> {

    public ProjectAdapter(@Nullable List<Project> data) {
        super(R.layout.item_project, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Project item) {
        helper.setText(R.id.tvProjectName, item.getProjectName());
        helper.addOnClickListener(R.id.tvProjectName);
    }
}
