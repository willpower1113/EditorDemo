package com.willpower.editor.delegate;

import android.view.View;

public interface IActivity<P extends IPresenter> extends IView {
    /**
     * 设置状态栏颜色
     */
    void setStateBarColor();
    /**
     * 初始化Presenter
     *
     * @return
     */
    P initPresenter();

    int getLayoutId();

    View getRootView();

    void init();
}
