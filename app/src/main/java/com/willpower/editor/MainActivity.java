package com.willpower.editor;

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
import com.willpower.editor.adapter.PageAdapter;
import com.willpower.editor.entity.Frame;
import com.willpower.editor.entity.Page;
import com.willpower.editor.widget.BaseView;
import com.willpower.editor.widget.CustomButton;
import com.willpower.editor.widget.CustomRectView;

import java.io.File;

public class MainActivity extends TakePhotoActivity {
    private FrameLayout boxLayout;
    private ImageView background;
    private RecyclerView lvPage;
    private BaseView tempView;
    private PageAdapter pageAdapter;
    private LinearLayout console;
    private long tempPageId = -1;

    private QMUIDialog.EditTextDialogBuilder builder;

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
    private void setTempPageId(long pageId) {
        this.tempPageId = pageId;
        if (tempPageId == -1) {
            console.setVisibility(View.GONE);
            boxLayout.setVisibility(View.GONE);
        } else {
            console.setVisibility(View.VISIBLE);
            boxLayout.setVisibility(View.VISIBLE);
        }
    }

    void log(String msg) {
        Log.e("MainActivity", msg);
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
                    boxLayout.addView(view,params);
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
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lvPage.setLayoutManager(linearLayoutManager);
        pageAdapter = new PageAdapter();
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

    public void onAddBox(View v) {
        tempView = CustomRectView.createView(this, new Rect(0, 0, boxLayout.getWidth(), boxLayout.getHeight()));
        Frame frame = new Frame();
        frame.setMode(Frame.MODE_BOX);
        frame.setFID(System.currentTimeMillis());
        frame.setPageID(tempPage.getPageID());
        tempView.setTag(frame);
        boxLayout.addView(tempView, new FrameLayout.LayoutParams(100, 100));
        tempPage.addFrame(frame);
        pageAdapter.getCurrentData().addFrame(frame);
    }

    public void onAddButton(View v) {
        tempView = CustomButton.createView(this, new Rect(0, 0, boxLayout.getWidth(), boxLayout.getHeight()));
        Frame frame = new Frame();
        frame.setMode(Frame.MODE_BUTTON);
        frame.setFID(System.currentTimeMillis());
        frame.setPageID(tempPage.getPageID());
        tempView.setTag(frame);
        boxLayout.addView(tempView, new FrameLayout.LayoutParams(200, 50));
        tempPage.addFrame(frame);
        pageAdapter.getCurrentData().addFrame(frame);
    }


    public void onAddPage(View v) {
        savePageData();
        builder = new QMUIDialog.EditTextDialogBuilder(this)
                .setPlaceholder("请输入页面名称")
                .addAction("确认", (dialog, index) -> {
                    tempPage = new Page();
                    tempPage.setPageID(System.currentTimeMillis());
                    tempPage.setPageName(builder.getEditText().getText().toString());
                    pageAdapter.addData(tempPage);
                    pageAdapter.setCurrentPosition(pageAdapter.getItemCount() - 1);
                    setTempPageId(tempPage.getPageID());
                    refreshPage();
                    new Handler().postDelayed(() -> {
                        linearLayoutManager.setStackFromEnd(
                                linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition()
                                < pageAdapter.getItemCount() -1
                    );
                },50);

                    dialog.dismiss();
                });
        builder.show();
    }

    public void onUpload(View v) {

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
