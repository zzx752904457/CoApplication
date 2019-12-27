package com.example.mvp.coapplication.base

import android.util.Log


abstract class BaseLifecycleActivity<P : BaseLifecyclePresenter<*>> : BaseActivity(), IBaseView {
    protected lateinit var mPresenter: P

    protected abstract fun createPresenter(): P

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        mPresenter = createPresenter()
        mPresenter.initLiveData(this)
        lifecycle.addObserver(mPresenter)
    }

    override fun showLoading() {
    }

    override fun showEmpty() {
    }

    override fun showError(errorCode: Int, errorMsg: String) {
        Log.e("协程测试", errorMsg)
    }

    override fun showContent() {
    }

}