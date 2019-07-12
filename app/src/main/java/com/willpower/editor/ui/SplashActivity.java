package com.willpower.editor.ui;

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
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.willpower.editor.R;
import com.willpower.editor.adapter.ProjectAdapter;
import com.willpower.editor.delegate.BaseActivity;
import com.willpower.editor.delegate.IPresenter;
import com.willpower.editor.entity.Project;
import com.willpower.editor.http.GsonHelper;
import com.willpower.editor.http.RequestHelper;
import com.willpower.editor.http.RetrofitManager;
import com.willpower.editor.http.StringCallback;
import com.willpower.editor.ui.editor.ProjectActivity;
import com.willpower.editor.widget.DialogHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {

    RecyclerView rvProjectList;

    ProjectAdapter projectAdapter;

    Button linkRefresh;

    Project project;

    QMUIDialog.EditTextDialogBuilder builder;

    List<Project> projects;


    @Override
    public IPresenter initPresenter() {
        return null;
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
            getProjectList();
        });
        getProjectList();
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
                project = projectAdapter.getItem(position);
                startActivity(new Intent(SplashActivity.this, ProjectActivity.class).putExtra("project", project));
                finish();
            }
        });
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
                    project = new Project();
                    project.setPages(new ArrayList<>());
                    project.setProjectId(System.currentTimeMillis());
                    project.setProjectName(pName);
                    dialog.dismiss();
                    createProject();
                })
                .addAction("取消", (dialog, index) -> dialog.dismiss());
        builder.create().show();
    }


    private void createProject() {
        showLoading();
        RetrofitManager.instance()
                .request()
                .createProject(RequestHelper.jsonBody(project))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        startActivity(new Intent(SplashActivity.this, ProjectActivity.class).putExtra("project", project));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {
                        hideLoading();
                        DialogHelper.showTipDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, "创建失败：" + throwable);
                    }
                });
    }


    private void getProjectList() {
        showLoading();
        RetrofitManager.instance()
                .request()
                .getProjectList()
                .enqueue(new StringCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String data) {
                        projects = GsonHelper.gson().fromJson(data, new TypeToken<List<Project>>() {
                        }.getType());
                        initProjects();
                        linkRefresh.setVisibility(View.GONE);
                        hideLoading();
                    }

                    @Override
                    public void onFailure(int code, Throwable throwable) {
                        hideLoading();
                        DialogHelper.showTipDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, throwable.getMessage());
                        linkRefresh.setVisibility(View.VISIBLE);
                    }
                });
    }
}
