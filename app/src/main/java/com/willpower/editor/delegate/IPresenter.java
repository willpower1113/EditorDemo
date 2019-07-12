package com.willpower.editor.delegate;


public interface IPresenter<V extends IView> {
    /**
     * @return 获取View
     */
    V getIView();

    void onDestroy();
}
