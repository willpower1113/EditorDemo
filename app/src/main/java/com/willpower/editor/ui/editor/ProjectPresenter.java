package com.willpower.editor.ui.editor;

import android.util.Log;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.willpower.editor.delegate.BasePresenter;
import com.willpower.editor.entity.Frame;
import com.willpower.editor.entity.Page;
import com.willpower.editor.entity.Project;
import com.willpower.editor.http.GsonHelper;
import com.willpower.editor.http.RequestHelper;
import com.willpower.editor.http.RetrofitManager;
import com.willpower.editor.http.StringCallback;
import com.willpower.editor.widget.BaseView;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProjectPresenter extends BasePresenter<ProjectActivity> {

    public ProjectPresenter(ProjectActivity mView) {
        super(mView);
    }

    /*
    退出前保存当前编辑页信息
     */
    public void exitApplication(Page page) {
        getIView().showLoading();
        addEnqueue(RetrofitManager.instance().request().updatePage(RequestHelper.jsonBody(page)), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView() != null) {
                    getIView().hideLoading();
                    getIView().onExitApplicationSuccess();
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView() != null) {
                    getIView().hideLoading();
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL, "数据提交失败," + throwable.getMessage());
                }
            }
        });
    }


    /**
     * 修改项目名称
     */
    public void updateProjectName(long projectId, String newName) {
        addEnqueue(RetrofitManager.instance().request().updateProjectName(projectId, newName), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView() != null) {
                    getIView().onEditProjectNameSuccess(newName);
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView() != null) {
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL, throwable.getMessage());
                }
            }
        });
    }

    /**
     * 新增页面
     */
    public void addPage(Page page) {
        addEnqueue(RetrofitManager.instance().request().createPage(RequestHelper.jsonBody(page)), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView() != null) {
                    getIView().onAddPageSuccess();
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView() != null) {
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL, throwable.getMessage());
                }
            }
        });
    }

    /**
     * 更新页面
     */
    public void updatePage(Page page, boolean isAddPage) {
        getIView().showLoading();
        addEnqueue(RetrofitManager.instance().request().updatePage(RequestHelper.jsonBody(page)), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView() != null) {
                    getIView().hideLoading();
                    if (isAddPage) {
                        getIView().addPage();
                    }
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView() != null) {
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL, throwable.getMessage());
                    getIView().hideLoading();
                }
            }
        });
    }

    /**
     * 新增Frame
     */
    public void addFrame(Frame frame) {
        addEnqueue(RetrofitManager.instance().request().addFrame(RequestHelper.jsonBody(frame)), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView() != null) {
                    getIView().onCreateFrameSuccess(frame);
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView() != null) {
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL, throwable.getMessage());
                }
            }
        });
    }

    /**
     * 删除Frame
     */
    public void deleteFrames(List<BaseView> frames, long[] frameIds) {
        getIView().showLoading();
        addEnqueue(RetrofitManager.instance().request().deleteFrames(frameIds), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView() != null) {
                    getIView().onDeleteFrameSuccess(frames);
                    getIView().hideLoading();
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView() != null) {
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL, throwable.getMessage());
                    getIView().hideLoading();
                }
            }
        });
    }

    /**
     * 获取页面列表
     */
    public void getProjectInfo(long projectId) {
        getIView().showLoading();
        addEnqueue(RetrofitManager.instance().request().getProjectInfo(projectId), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                Log.e("OkHttp", "onSuccess: " + data);
                if (getIView() != null && data != null) {
                    getIView().hideLoading();
                    getIView().onGetProjectInfoSuccess(GsonHelper.gson().fromJson(data, Project.class));
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                Log.e("OkHttp", "onSuccess: " + code + "Throwable: " + throwable.getMessage());
                if (getIView() != null) {
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL, throwable.getMessage());
                    getIView().hideLoading();
                }
            }
        });
    }

    /**
     * 上传背景图
     */
    public void uploadBitmap(File file, long pageId, long projectId, String projectName) {
        getIView().showLoading();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        addEnqueue(RetrofitManager.instance().request().uploadPageThumbnail(part, pageId, projectId, projectName), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView() != null) {
                    getIView().hideLoading();
                    getIView().onUploadThumbnailSuccess(data);
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView() != null) {
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL, throwable.getMessage());
                    getIView().hideLoading();
                }
            }
        });
    }


}
