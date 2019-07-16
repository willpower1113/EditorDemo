package com.willpower.editor.ui.splash;

import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.willpower.editor.delegate.BasePresenter;
import com.willpower.editor.entity.Project;
import com.willpower.editor.http.GsonHelper;
import com.willpower.editor.http.RequestHelper;
import com.willpower.editor.http.RetrofitManager;
import com.willpower.editor.http.StringCallback;

import java.util.List;

public class SplashPresenter extends BasePresenter<SplashActivity>{

    public SplashPresenter(SplashActivity mView) {
        super(mView);
    }

    public void createProject(Project project){
        addEnqueue(RetrofitManager.instance().request().createProject(RequestHelper.jsonBody(project)), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView()!=null){
                    getIView().onCreateProjectSuccess(project);
                    getIView().hideLoading();
                }
            }
            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView()!=null){
                    getIView().hideLoading();
                    getIView().showToast(throwable.getMessage());
                }
            }
        });
    }


    public void getProjectList(){
        getIView().showLoading();
        addEnqueue(RetrofitManager.instance().request().getProjectList(), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView()!=null){
                    getIView().onGetProjectListSuccess(GsonHelper.gson().fromJson(data, new TypeToken<List<Project>>() {
                    }.getType()));
                    getIView().hideLoading();
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView()!=null){
                    getIView().hideLoading();
                    getIView().showToast(throwable.getMessage());
                    getIView().onGetProjectListFail();
                }
            }
        });
    }

    public void deleteProject(int index,long projectId){
        addEnqueue(RetrofitManager.instance().request().deleteProject(projectId), new StringCallback() {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (getIView()!=null){
                    getIView().onDeleteProjectSuccess(index);
                }
            }

            @Override
            public void onFailure(int code, Throwable throwable) {
                if (getIView()!=null){
                    getIView().showTips(QMUITipDialog.Builder.ICON_TYPE_FAIL,"删除失败："+throwable.getMessage());
                }
            }
        });
    }
}
