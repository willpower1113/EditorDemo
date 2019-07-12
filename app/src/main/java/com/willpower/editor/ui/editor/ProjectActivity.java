package com.willpower.editor.ui.editor;

import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.jph.takephoto.model.TResult;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.willpower.editor.R;
import com.willpower.editor.adapter.PageAdapter;
import com.willpower.editor.delegate.BaseTakePhotoActivity;
import com.willpower.editor.entity.Frame;
import com.willpower.editor.entity.Page;
import com.willpower.editor.entity.Project;
import com.willpower.editor.utils.Config;
import com.willpower.editor.utils.ValidUtils;
import com.willpower.editor.widget.BaseView;
import com.willpower.editor.widget.CustomButton;
import com.willpower.editor.widget.CustomRectView;
import com.willpower.editor.widget.DialogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends BaseTakePhotoActivity<ProjectPresenter> {
    private FrameLayout editorLayout;
    private ImageView imgEditorBackground;
    private RecyclerView rvPageList;
    private LinearLayout consoleLayout;
    private TextView tvProjectName;
    private Button btnGroup, btnDelete;
    private ToggleButton switchInfo, switchChoose;

    /*
    temp
     */
    private BaseView tempView;
    private Project temProject;
    private Page tempPage;

    private PageAdapter pageAdapter;
    private LinearLayoutManager linearLayoutManager;

    private QMUIDialog.EditTextDialogBuilder builder;

    private Handler handler = new Handler();


    @Override
    public int getLayoutId() {
        return R.layout.activity_project;
    }

    void log(String msg) {
        Log.e("ProjectActivity", msg);
    }


    /*****************************************Init Method********************************************************/


    @Override
    public ProjectPresenter initPresenter() {
        return new ProjectPresenter(this);
    }


    @Override
    public void init() {
        initView();
        initPages();
    }

    private void initView() {
        editorLayout = findViewById(R.id.boxLayout);
        imgEditorBackground = findViewById(R.id.background);
        rvPageList = findViewById(R.id.lvPage);
        consoleLayout = findViewById(R.id.console);
        tvProjectName = findViewById(R.id.tvProjectName);
        switchInfo = findViewById(R.id.switchInfo);
        switchChoose = findViewById(R.id.switchChoose);
        btnGroup = findViewById(R.id.btnGroup);
        btnDelete = findViewById(R.id.btnDelete);

        switchChoose.setOnCheckedChangeListener(chooseListener);
        switchInfo.setOnCheckedChangeListener(infoListener);
    }

    /*
     * 初始化Page列表
     */
    private void initPages() {
        temProject = (Project) getIntent().getSerializableExtra("project");
        tvProjectName.setText(temProject.getProjectName());
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvPageList.setLayoutManager(linearLayoutManager);
        pageAdapter = new PageAdapter();
        rvPageList.setAdapter(pageAdapter);
        pageAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.imgBit:
                    savePageDataToTemProject();
                    pageAdapter.setCurrentPosition(position);
                    onGetFrameListSuccess(pageAdapter.getCurrentData().getFrameList());
                    break;
                case R.id.tvPageName:
                    showPageNameDialog(pageAdapter.getData().get(position).getPageName(), position);
                    break;
            }
        });
        mPresenter.getProjectInfo(temProject.getProjectId());
    }


    /*****************************************LifeCycle Method********************************************************/

    @Override
    protected void onResume() {
        super.onResume();
        if (pageAdapter.getData() != null && pageAdapter.getData().size() > 0) {
            //刷新界面
            tempPage = pageAdapter.getCurrentData();
            handler.postDelayed(() -> refreshPage(), 200);
        }
    }


    /*****************************************UI Method********************************************************/

     /*
       刷新框
        */
    private void changeFrameState() {
        //清空界面
        for (int i = 0; i < editorLayout.getChildCount(); i++) {
            BaseView view = (BaseView) editorLayout.getChildAt(i);
            if (switchInfo.isChecked()) {
                view.setInfo(true);
            } else {
                view.setInfo(false);
            }
            if (switchChoose.isChecked()) {
                view.setChooseMode(true);
            } else {
                view.setChooseMode(false);
            }
            view.refreshText();
        }
    }

    /*
 刷新页面
  */
    private void refreshPage() {
        setConsoleVisibility(tempPage != null);
        log("刷新页面");
        //清空界面
        editorLayout.removeAllViews();
        if (tempPage != null) {
            Glide.with(context).load(tempPage.getThumbnail()).into(imgEditorBackground);
            if (tempPage.getFrameList() != null) {
                for (Frame frame :
                        tempPage.getFrameList()) {
                    BaseView view;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frame.getLoc_Right() - frame.getLoc_left(),
                            frame.getLoc_Bottom() - frame.getLoc_top());
                    params.leftMargin = frame.getLoc_left();
                    params.topMargin = frame.getLoc_top();
                    if (frame.getMode() == Frame.MODE_BOX) {
                        view = CustomRectView.createView(context, new Rect(0, 0, editorLayout.getWidth(), editorLayout.getHeight()));
                    } else {
                        view = CustomButton.createView(context, new Rect(0, 0, editorLayout.getWidth(), editorLayout.getHeight()));
                    }
                    if (switchInfo.isChecked()) {
                        view.setInfo(true);
                    } else {
                        view.setInfo(false);
                    }
                    view.setTag(frame);
                    if (switchChoose.isChecked()) {
                        view.setChooseMode(true);
                    } else {
                        view.setChooseMode(false);
                    }
                    bindListener(view);
                    editorLayout.addView(view, params);
                }
            }
        } else {
            imgEditorBackground.setImageDrawable(null);
        }
    }

    /*
    给BaseView绑定事件
     */
    private void bindListener(BaseView view) {
        view.setListener((isLong, v) -> {
            if (view.isChooseMode()) {
                view.setChoosed(!view.isChoosed());
            } else {
                if (view instanceof CustomButton) {
                    //给Button 绑定事件
                    /*先获取Frame，判断是否已绑定事件*/
                    Frame frame1 = (Frame) view.getTag();
                    showLinkDialog(frame1, view);
                }
            }
        });
    }

    /*
   设置当前是否显示编辑框
    */
    private void setConsoleVisibility(boolean visibility) {
        if (!visibility) {
            consoleLayout.setVisibility(View.GONE);
            editorLayout.setVisibility(View.INVISIBLE);
        } else {
            consoleLayout.setVisibility(View.VISIBLE);
            editorLayout.setVisibility(View.VISIBLE);
        }
    }

    /*
  弹出要关联界面Dialog
   */
    private void showLinkDialog(Frame frame, View view) {
        int selectedItem = -1;
        int count = pageAdapter.getData().size();
        CharSequence[] items = new CharSequence[count];
        for (int i = 0; i < count; i++) {
            if (frame.getLinkPage() == pageAdapter.getData(i).getPageId()) {
                selectedItem = i;
            }
            items[i] = pageAdapter.getData(i).getPageName();
        }
        dialog = new QMUIDialog.CheckableDialogBuilder(context)
                .addItems(items, (dialog, which) -> {
                    if (which != pageAdapter.getCurrent()) {//不能选中当前页
                        frame.setLinkPage(pageAdapter.getData(which).getPageId());
                        view.setTag(frame);//view为null时为新建Frame
                        dialog.dismiss();
                    }
                })
                .setCheckedIndex(selectedItem)
                .setTitle("选择要关联的界面")
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .show();
    }

    /*
   编辑页面名称Dialog
    */
    private void showPageNameDialog(String pageName, int position) {
        builder = new QMUIDialog.EditTextDialogBuilder(context)
                .setDefaultText(pageName)
                .addAction("确定", (dialog, index) -> {
                    pageAdapter.getData(position).setPageName(builder.getEditText().getText().toString());
                    pageAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                });
        dialog = builder.show();
    }

    public void addPage() {
        builder = new QMUIDialog.EditTextDialogBuilder(context)
                .setPlaceholder("请输入页面名称")
                .addAction("确认", (dialog, index) -> {
                    String name = builder.getEditText().getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        name = "Page " + (pageAdapter.getItemCount() + 1);
                    }
                    tempPage = new Page();
                    tempPage.setPageId(System.currentTimeMillis());
                    tempPage.setProjectId(temProject.getProjectId());
                    tempPage.setPageName(name);
                    mPresenter.addPage(tempPage);
                    dialog.dismiss();
                });
        dialog = builder.show();
    }

    /*
      添加Frame
       */
    private void createFrame(int mode) {
        Frame frame = new Frame();
        frame.setMode(mode);
        frame.setProjectId(temProject.getProjectId());
        frame.setPageId(tempPage.getPageId());
        frame.setFrameId(System.currentTimeMillis());
        frame.setLoc_left(0);
        frame.setLoc_top(0);
        frame.setLoc_Right(100);
        frame.setLoc_Bottom(100);

        if (mode == Frame.MODE_BOX) {
            dialog = new QMUIDialog.CheckableDialogBuilder(context)
                    .setCheckedIndex(0)
                    .addItems(Config.Frame_Mode, (dialog, which) -> {
                        frame.setAction(which);
                        mPresenter.addFrame(frame);
                        dialog.dismiss();
                    }).show();
        } else {
            mPresenter.addFrame(frame);
        }
    }

    /*
   保存当前页面数据到temProject
    */
    private void savePageDataToTemProject() {
        for (int i = 0; i < editorLayout.getChildCount(); i++) {
            Frame frame = (Frame) editorLayout.getChildAt(i).getTag();
            pageAdapter.getCurrentData().addFrame(frame);
        }
        temProject.setPages(pageAdapter.getData());
    }

    /*****************************************Listener********************************************************/
    /**
     * 选择
     */
    CompoundButton.OnCheckedChangeListener chooseListener = (buttonView, isChecked) -> {
        btnDelete.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        btnGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        changeFrameState();
    };
    /**
     * 显示详情
     */
    CompoundButton.OnCheckedChangeListener infoListener = (buttonView, isChecked) -> changeFrameState();

    /*****************************************API回调********************************************************/
    public void onEditProjectNameSuccess(String newName) {
        tvProjectName.setText(newName);
    }

    public void onDeleteFrameSuccess(List<BaseView> views) {
        for (BaseView v :
                views) {
            editorLayout.removeView(v);
            pageAdapter.getCurrentData().getFrameList().remove(v.getTag());
        }
        temProject.setPages(pageAdapter.getData());
    }

    public void onGetProjectInfoSuccess(Project project) {
        this.temProject = project;
        pageAdapter.setNewData(project.getPages());
        if (!ValidUtils.isEmpty(project.getPages())) {
            onGetFrameListSuccess(project.getPages().get(0).getFrameList());
        }
    }

    public void onGetFrameListSuccess(List<Frame> frameList) {
        pageAdapter.getCurrentData().setFrameList(frameList);
        this.tempPage = pageAdapter.getCurrentData();
        refreshPage();
    }

    public void onAddPageSuccess() {
        pageAdapter.addData(tempPage);
        pageAdapter.setCurrentPosition(pageAdapter.getItemCount() - 1);
        refreshPage();
        new Handler().postDelayed(() -> linearLayoutManager.setStackFromEnd(
                linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition()
                        < (pageAdapter.getItemCount() - 1)
        ), 100);
    }

    public void onCreateFrameSuccess(Frame frame) {
        if (frame.getMode() == Frame.MODE_BUTTON) {
            tempView = CustomButton.createView(context, new Rect(0, 0, editorLayout.getWidth(), editorLayout.getHeight()));
        } else {
            tempView = CustomRectView.createView(context, new Rect(0, 0, editorLayout.getWidth(), editorLayout.getHeight()));
        }
        if (switchInfo.isChecked()) {
            tempView.setInfo(true);
        } else {
            tempView.setInfo(false);
        }
        tempView.setTag(frame);/*注：此处位置不可动，setInfo之后*/
        if (switchChoose.isChecked()) {
            tempView.setChooseMode(true);
        } else {
            tempView.setChooseMode(false);
        }
        bindListener(tempView);
        editorLayout.addView(tempView, new FrameLayout.LayoutParams(100, 100));
        tempPage.addFrame(frame);
        pageAdapter.getCurrentData().addFrame(frame);
    }


    /*
  上传图片成功回调
   */
    public void onUploadThumbnailSuccess(String path) {
        tempPage.setThumbnail(path);
        pageAdapter.getCurrentData().setThumbnail(path);
        pageAdapter.notifyItemChanged(pageAdapter.getCurrent());
        Glide.with(context)
                .load(path)
                .error(R.drawable.image_placeholder)
                .into(imgEditorBackground);
    }

    /*
    提交项目回调
     */
    public void onExitApplicationSuccess() {
        finish();
    }

    /*****************************************点击********************************************************/
    /*
    修改页面名称
     */
    public void onEditProjectNameClick(View view) {
        builder = new QMUIDialog.EditTextDialogBuilder(context)
                .setTitle("修改项目名称")
                .setPlaceholder("请输入新的项目名称")
                .addAction("修改", (dialog, index) -> {
                    CharSequence sequence = builder.getEditText().getText();
                    if (ValidUtils.isEmpty(sequence)){
                        showToast("项目名称不能为空！");
                        return;
                    }
                    mPresenter.updateProjectName(temProject.getProjectId(), sequence.toString());
                    dialog.dismiss();
                }).addAction("取消", (dialog, index) -> dialog.dismiss());
        dialog = builder.show();
    }

    /*
     * 添加框
     */
    public void onCreateBoxClick(View v) {
        createFrame(Frame.MODE_BOX);
    }

    /*
     * 添加按钮
     */
    public void onCreateButtonClick(View v) {
        createFrame(Frame.MODE_BUTTON);
    }

    /*
    新增页面
     */
    public void onAddPageClick(View v) {
        savePageDataToTemProject();
        if (tempPage != null) {
            mPresenter.updatePage(tempPage, true);
        } else {
            addPage();
        }
    }

    /*
    保存页面
     */
    public void onSavePageClick(View v) {
        mPresenter.updatePage(tempPage, false);
    }

    /*
    关闭程序
     */
    public void onCloseProjectClick(View v) {
        onBackPressed();
    }

    /*
    设置背景
     */
    public void onChoosePictureClick(View v) {
        getTakePhoto().onPickMultiple(1);
    }

    /*
    创建组
     */
    public void onGroupClick(View v) {
        long groupId = System.currentTimeMillis();
        List<BaseView> views = new ArrayList<>();
        for (int i = 0; i < editorLayout.getChildCount(); i++) {
            BaseView view = (BaseView) editorLayout.getChildAt(i);
            if (view.isChoosed()) {
                Frame f = (Frame) view.getTag();
                if (f.getMode() != Frame.MODE_BOX || f.getAction() != 2) {
                    DialogHelper.showTipDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, "ID:" + f.getFrameId() + " 不是回看框");
                    return;
                }
                views.add(view);
            }
        }
        for (BaseView view :
                views) {
            Frame f = (Frame) view.getTag();
            f.setGroupId(groupId);
            view.setTag(f);
        }
    }

    /*
    删除
     */
    public void onDeleteClick(View v) {
        dialog = new QMUIDialog.MessageDialogBuilder(context).
                setMessage("确定删除选中框？")
                .setTitle("提示")
                .addAction(R.drawable.icon_delete, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, (dialog, index) -> {
                    List<BaseView> views = new ArrayList<>();
                    for (int i = 0; i < editorLayout.getChildCount(); i++) {
                        BaseView view = (BaseView) editorLayout.getChildAt(i);
                        if (view.isChoosed()) {
                            views.add(view);
                        }
                    }
                    long[] frameIds = new long[views.size()];
                    for (int i = 0; i < views.size(); i++) {
                        frameIds[i] = ((Frame) views.get(i).getTag()).getFrameId();
                    }
                    mPresenter.deleteFrames(views, frameIds);
                    dialog.dismiss();
                })
                .addAction("取消", (dialog, index) -> dialog.dismiss()).show();
    }


    @Override
    public void takeSuccess(TResult result) {
        //选取背景成功后，上传到服务器
        Log.e("TakePhoto", "takeSuccess:" + result.getImage().getOriginalPath());
        mPresenter.uploadBitmap(new File(result.getImage().getOriginalPath()), tempPage.getPageId(), temProject.getProjectId(), temProject.getProjectName());
    }

    @Override
    public void onBackPressed() {
        mPresenter.exitApplication(tempPage);
    }
}
