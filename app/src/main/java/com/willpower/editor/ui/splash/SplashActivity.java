package com.willpower.editor.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.willpower.editor.R;
import com.willpower.editor.adapter.ProjectAdapter;
import com.willpower.editor.delegate.BaseActivity;
import com.willpower.editor.entity.Project;
import com.willpower.editor.ui.editor.ProjectActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity<SplashPresenter> {

    RecyclerView rvProjectList;

    ProjectAdapter projectAdapter;

    Button linkRefresh;

    QMUIDialog.EditTextDialogBuilder builder;

    List<Project> projects;


    @Override
    public SplashPresenter initPresenter() {
        return new SplashPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void init() {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        requestPermissions(REQUEST_CODE, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
        linkRefresh = findViewById(R.id.linkRefresh);
        linkRefresh.setOnClickListener(v -> {
            linkRefresh.setVisibility(View.GONE);
            mPresenter.getProjectList();
        });
        mPresenter.getProjectList();
    }

    /**
     * 初始化项目列表
     */
    private void initProjects() {
        rvProjectList = findViewById(R.id.rvProjectList);
        rvProjectList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        projectAdapter = new ProjectAdapter(projects);
        rvProjectList.setAdapter(projectAdapter);
        rvProjectList.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tvProjectName://选中项目
                        startActivity(new Intent(SplashActivity.this, ProjectActivity.class)
                                .putExtra("project", projectAdapter.getItem(position)));
                        finish();
                        break;
                    case R.id.imgDelete://删除项目
                        showDeleteProjectDialog(position);
                        break;
                }
            }
        });
    }

    private void showDeleteProjectDialog(int index){
        dialog = new QMUIDialog.MessageDialogBuilder(context)
                .setTitle("警告")
                .setMessage("确认删除项目 <" + projectAdapter.getData().get(index).getProjectName() + "> ?")
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, (dialog, index1) -> {
                    mPresenter.deleteProject(index, projectAdapter.getData().get(index).getProjectId());
                    dialog.dismiss();
                })
                .addAction("取消", (dialog, index12) -> dialog.dismiss())
                .show();
    }


    public void onAddProject(View v) {
        builder = new QMUIDialog.EditTextDialogBuilder(this)
                .setTitle("新建")
                .setPlaceholder("请输入项目名称")
                .addAction("确认", (dialog, index) -> {
                    String pName = builder.getEditText().getText().toString();
                    if (TextUtils.isEmpty(pName)) {
                        Toast.makeText(SplashActivity.this, "项目名称不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Project project = new Project();
                    project.setPages(new ArrayList<>());
                    project.setProjectId(System.currentTimeMillis());
                    project.setProjectName(pName);
                    dialog.dismiss();
                    mPresenter.createProject(project);
                })
                .addAction("取消", (dialog, index) -> dialog.dismiss());
        builder.create().show();
    }


    public void onCreateProjectSuccess(Project project) {
        startActivity(new Intent(SplashActivity.this, ProjectActivity.class)
                .putExtra("project", project));
        finish();
    }


    public void onGetProjectListSuccess(List<Project> projects) {
        this.projects = projects;
        initProjects();
        linkRefresh.setVisibility(View.GONE);
    }

    public void onGetProjectListFail() {
        linkRefresh.setVisibility(View.VISIBLE);
    }

    public void onDeleteProjectSuccess(int index) {
        projectAdapter.remove(index);
    }
}
