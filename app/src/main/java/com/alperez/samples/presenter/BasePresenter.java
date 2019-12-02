package com.alperez.samples.presenter;

import java.lang.ref.WeakReference;

/**
 * Created by stanislav.perchenko on 12/2/2019, 7:45 PM.
 */
public abstract class BasePresenter<TView> {

    private WeakReference<TView> viewRef;

    private volatile boolean released;

    protected BasePresenter(TView view) {
        if (view != null) {
            viewRef = new WeakReference<>(view);
        } else {
            throw new IllegalArgumentException("A valid instance of the View interface must be provided");
        }
    }

    protected TView getView() {
        return viewRef.get();
    }

    /**
     * This method starts initialization process of prepared UI.
     * It must be called from the Activity's onPostCreate()
     */
    public abstract void initializeView();

    /**
     * This method must be called from Activity's onDestroy()
     */
    public synchronized void release() {
        released = true;
        viewRef.clear();
    }

    public synchronized boolean isReleased() {
        return released;
    }

    /**
     * This method checks if the presentor has been released already
     * and throws IllegalSztateException if so.
     * subclasses must call this method before start execution of any operation.
     */
    protected synchronized void checkReleased() {
        if (released) {
            throw new IllegalStateException("Already released!");
        }
    }
}
