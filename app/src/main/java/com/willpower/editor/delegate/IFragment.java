package com.willpower.editor.delegate;

import android.view.View;

public interface IFragment<P extends IPresenter> extends IView {
    /**
     * 是否使用 EventBus  Activity创建时会检测，并决定是否注册EventBus
     *
     * @return
     */
    boolean useEventBus();
    /**
     * 初始化Presenter
     *
     * @return
     */
    P initPresenter();

    int getLayoutId();

    View getRootView();

    void lazyLoad();

    void init();
}
