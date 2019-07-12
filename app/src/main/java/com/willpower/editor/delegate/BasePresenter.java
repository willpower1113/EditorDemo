package com.willpower.editor.delegate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public abstract class BasePresenter<V extends IView> implements IPresenter<V> {

    protected WeakReference<V> mView;

    protected List<Call> requests;

    public BasePresenter(V mView) {
        this.mView = new WeakReference<>(mView);
        this.requests = new ArrayList<>();
    }

    @Override
    public V getIView() {
        if (mView == null) return null;
        return this.mView.get();
    }

    protected void addEnqueue(Call call, Callback callback) {
        requests.add(call);
        call.enqueue(callback);
    }

    @Override
    public void onDestroy() {
        if (mView != null) {
            mView.clear();
        }
        for (Call call :
                requests) {
            if (!call.isExecuted() && !call.isCanceled()) {
                call.cancel();
            }
        }
    }
}
