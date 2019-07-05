package com.willpower.editor.ui;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.uitl.TUriParse;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.willpower.editor.R;
import com.willpower.editor.adapter.PageAdapter;
import com.willpower.editor.entity.Frame;
import com.willpower.editor.entity.Page;
import com.willpower.editor.entity.Project;
import com.willpower.editor.utils.FileUtils;
import com.willpower.editor.utils.JSONHelper;
import com.willpower.editor.widget.BaseView;
import com.willpower.editor.widget.CustomButton;
import com.willpower.editor.widget.CustomRectView;

import java.io.File;
import java.util.ArrayList;

public class ProjectActivity extends TakePhotoActivity {
    private FrameLayout boxLayout;
    private ImageView background;
    private RecyclerView lvPage;
    private BaseView tempView;
    private PageAdapter pageAdapter;
    private LinearLayout console;

    private QMUIDialog.EditTextDialogBuilder builder;

    private Project project;

    private Page tempPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_main);
        boxLayout = findViewById(R.id.boxLayout);
        background = findViewById(R.id.background);
        lvPage = findViewById(R.id.lvPage);
        console = findViewById(R.id.console);
        initPages();
    }

    /*
    设置当前显示页
     */
    private void setTempPageId() {
        if (tempPage == null) {
            console.setVisibility(View.GONE);
            boxLayout.setVisibility(View.GONE);
        } else {
            console.setVisibility(View.VISIBLE);
            boxLayout.setVisibility(View.VISIBLE);
        }
    }

    void log(String msg) {
        Log.e("ProjectActivity", msg);
    }

    /*
    刷新页面
     */
    private void refreshPage() {
        log("刷新页面");
        //清空界面
        boxLayout.removeAllViews();
        if (tempPage != null) {
            Glide.with(this).load(tempPage.getThumbnail()).into(background);
            if (tempPage.getFrameList() != null) {
                for (Frame frame :
                        tempPage.getFrameList()) {
                    BaseView view;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frame.getInfo().getRight() - frame.getInfo().getLeft(),
                            frame.getInfo().getBottom() - frame.getInfo().getTop());
                    params.leftMargin = frame.getInfo().getLeft();
                    params.topMargin = frame.getInfo().getTop();
                    if (frame.getMode() == Frame.MODE_BOX) {
                        view = CustomRectView.createView(this, new Rect(0, 0, boxLayout.getWidth(), boxLayout.getHeight()));
                    } else {
                        view = CustomButton.createView(this, new Rect(0, 0, boxLayout.getWidth(), boxLayout.getHeight()));
                    }
                    view.setTag(frame);
                    view.setListener((isLong, v) -> {
                        if (isLong) {
                            new QMUIDialog.MessageDialogBuilder(ProjectActivity.this)
                                    .setTitle("提示")
                                    .setMessage("是否要删除选中框？")
                                    .addAction("删除", (dialog, index) -> {
                                        tempPage.getFrameList().remove(frame);
                                        boxLayout.removeView(v);
                                        dialog.dismiss();
                                    })
                                    .addAction("取消", (dialog, index) -> dialog.dismiss())
                                    .show();
                        } else {
                            new QMUIDialog.MessageDialogBuilder(ProjectActivity.this)
                                    .setTitle("提示")
                                    .setMessage("点击了选中框！")
                                    .addAction("确定", (dialog, index) -> dialog.dismiss())
                                    .addAction("取消", (dialog, index) -> dialog.dismiss())
                                    .show();
                        }
                    });
                    boxLayout.addView(view, params);
                }
            }
        } else {
            background.setImageDrawable(null);
        }
    }

    /**
     * 显示Page列表
     */
    LinearLayoutManager linearLayoutManager;

    private void initPages() {
        project = (Project) getIntent().getSerializableExtra("project");
        if (project.getPages() == null || project.getPages().size() == 0){
            //显示空界面
        }
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lvPage.setLayoutManager(linearLayoutManager);
        pageAdapter = new PageAdapter(project.getPages());
        lvPage.setAdapter(pageAdapter);
        pageAdapter.openLoadAnimation();
        pageAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.imgBit:
                    savePageData();
                    pageAdapter.setCurrentPosition(position);
                    tempPage = pageAdapter.getData(position);
                    refreshPage();
                    break;
                case R.id.tvPageName:
                    editPageName(pageAdapter.getData().get(position).getPageName(), position);
                    break;
            }
        });
    }


    /*
    编辑页面名称
     */

    private void editPageName(String pageName, int position) {
        builder = new QMUIDialog.EditTextDialogBuilder(this)
                .setDefaultText(pageName)
                .addAction("确定", (dialog, index) -> {
                    pageAdapter.getData(position).setPageName(builder.getEditText().getText().toString());
                    pageAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                });
        builder.show();
    }


    /**
     * 设置全屏
     */
    public void setFullScreen() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void hideNavigationBar(View v) {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * 添加框
     *
     * @param v
     */
    public void onAddBox(View v) {
        tempView = CustomRectView.createView(this, new Rect(0, 0, boxLayout.getWidth(), boxLayout.getHeight()));
        createControlView(Frame.MODE_BOX);
    }

    /**
     * 添加按钮
     *
     * @param v
     */
    public void onAddButton(View v) {
        tempView = CustomButton.createView(this, new Rect(0, 0, boxLayout.getWidth(), boxLayout.getHeight()));
        createControlView(Frame.MODE_BUTTON);
    }


    private void createControlView(int mode) {
        Frame frame = new Frame();
        frame.setMode(mode);
        frame.setFID(System.currentTimeMillis());
        tempView.setTag(frame);
        tempView.setListener((isLong, view) -> {
            if (isLong) {
                new QMUIDialog.MessageDialogBuilder(ProjectActivity.this)
                        .setTitle("提示")
                        .setMessage("是否要删除选中框？")
                        .addAction("删除", (dialog, index) -> {
                            tempPage.getFrameList().remove(frame);
                            boxLayout.removeView(view);
                            dialog.dismiss();
                        })
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .show();
            } else {
                new QMUIDialog.MessageDialogBuilder(ProjectActivity.this)
                        .setTitle("提示")
                        .setMessage("点击了选中框！")
                        .addAction("确定", (dialog, index) -> dialog.dismiss())
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .show();
            }
        });
        boxLayout.addView(tempView, new FrameLayout.LayoutParams(100, 100));
        tempPage.addFrame(frame);
        pageAdapter.getCurrentData().addFrame(frame);
    }


    public void onAddPage(View v) {
        savePageData();
        builder = new QMUIDialog.EditTextDialogBuilder(this)
                .setPlaceholder("请输入页面名称")
                .addAction("确认", (dialog, index) -> {
                    tempPage = new Page();
                    tempPage.setPageName(builder.getEditText().getText().toString());
                    pageAdapter.addData(tempPage);
                    pageAdapter.setCurrentPosition(pageAdapter.getItemCount() - 1);
                    refreshPage();
                    new Handler().postDelayed(() -> linearLayoutManager.setStackFromEnd(
                            linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition()
                                    < pageAdapter.getItemCount() - 1
                    ), 50);
                    dialog.dismiss();
                });
        builder.show();
    }

    public void onUpload(View v) {
        Log.e("上传", JSONHelper.serializable(pageAdapter.getData()));
        FileUtils.saveProjectToFile(project);
    }

    public void onBack(View v) {
        onBackPressed();
    }

    public void onChoosePicture(View v) {
        getTakePhoto().onPickMultiple(1);
    }

    @Override
    public void takeSuccess(TResult result) {
        Glide.with(this)
                .load(TUriParse.getUriForFile(this, new File(result.getImage().getOriginalPath()))).into(background);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.e("TakePhoto", "takeFail:" + msg);
    }

    /*
    保存页面数据
     */
    private void savePageData() {
        /*保存数据*/
        log("保存数据");
        for (int i = 0; i < boxLayout.getChildCount(); i++) {
            Frame frame = (Frame) boxLayout.getChildAt(i).getTag();
            pageAdapter.getCurrentData().addFrame(frame);
        }
        project.setPages((ArrayList<Page>) pageAdapter.getData());
    }

    @Override
    public void onBackPressed() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setMessage("退出程序时请先上传数据")
                .setTitle("警告")
                .addAction("上传数据", (dialog, index) -> onUpload(null))
                .addAction("退出程序", (dialog, index) -> finish())
                .show();
    }
}
