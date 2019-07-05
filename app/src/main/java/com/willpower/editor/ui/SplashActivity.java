package com.willpower.editor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.willpower.editor.R;
import com.willpower.editor.adapter.ProjectAdapter;
import com.willpower.editor.entity.Project;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    RecyclerView rvProjectList;

    ProjectAdapter projectAdapter;

    Project project;

    QMUIDialog.EditTextDialogBuilder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initProjects();
    }

    private void initProjects() {
        rvProjectList = findViewById(R.id.rvProjectList);
        rvProjectList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        projectAdapter = new ProjectAdapter();
        rvProjectList.setAdapter(projectAdapter);
        rvProjectList.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                project = projectAdapter.getItem(position);
                initProjects();
            }
        });
    }

    public void onAddProject(View v) {
        builder = new QMUIDialog.EditTextDialogBuilder(this)
                .setTitle("新建")
                .setPlaceholder("请输入项目名称")
                .addAction("确认", (dialog, index) -> {
                    project = new Project();
                    project.setPages(new ArrayList<>());
                    project.setCreated(System.currentTimeMillis());
                    project.setProjectName(builder.getEditText().getText().toString());
                    intentProject();
                    dialog.dismiss();
                })
                .addAction("取消", (dialog, index) -> dialog.dismiss());
        builder.create().show();
    }


    private void intentProject() {
        startActivity(new Intent(this, ProjectActivity.class).putExtra("project", project));
    }
}
