package com.willpower.editor.delegate;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public interface IView {

    void showLoading();

    void showLoading(String message);

    void hideLoading();

    void showToast(String msg);

    void showTips(@QMUITipDialog.Builder.IconType int iconType,String msg);

    LifecycleOwner getOwner();

    void jumpActivity(Intent intent);

    void jumpActivityForResult(Intent intent, int requestCode);
}
