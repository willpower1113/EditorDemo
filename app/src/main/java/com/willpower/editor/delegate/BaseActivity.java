package com.willpower.editor.delegate;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.willpower.editor.widget.DialogHelper;

import static android.os.Build.VERSION_CODES.M;

public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements IActivity<P>, IView {
    protected QMUITipDialog progress;
    protected Dialog dialog;
    protected Context context;
    protected Activity activity;
    protected P mPresenter;

    protected static final int REQUEST_CODE = 1001;

    protected void requestPermissions(int requestCode, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M) {
            boolean granted = false;
            for (String permission :
                    permissions) {
                if (ContextCompat.checkSelfPermission(this,
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }
            if (granted) {
                //进行权限请求
                ActivityCompat.requestPermissions(this, permissions, requestCode);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        context = this;
        activity = this;
        mPresenter = initPresenter();
        init();
    }

    @Override
    public void setStateBarColor() {
        //设置状态栏颜色
    }


    @Override
    public View getRootView() {
        return findViewById(android.R.id.content);
    }


    @Override
    public void showLoading() {
        showLoading("加载中...");
    }

    @Override
    public void showLoading(String message) {
        progress = DialogHelper.showLoading(context, message);
        progress.show();
    }

    @Override
    public void hideLoading() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTips(int iconType, String msg) {
        DialogHelper.showTipDialog(context, iconType, msg);
    }

    @Override
    public void jumpActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void jumpActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public LifecycleOwner getOwner() {
        return this;
    }

    /**
     * 设置全屏
     */
    protected void setFullScreen() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        hideLoading();
        progress = null;
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }
}
