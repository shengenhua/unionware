package unionware.base.app.view.base.mvp;


import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

public class BasePresenter<V extends IView> implements IPresenter<V>, LifecycleObserver {


    protected V mView;

    @Override
    public void attach(@Nullable V view) {
        mView = view;
        if (mView != null) {
            mView.getLifecycle().addObserver(this);
        }
    }


    @Override
    public void detach() {
        if (mView != null) {
            mView.getLifecycle().removeObserver(this);
            mView = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        detach();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(LifecycleOwner owner) {

    }
}
