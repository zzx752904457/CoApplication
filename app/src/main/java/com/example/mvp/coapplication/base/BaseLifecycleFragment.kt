package com.example.mvp.coapplication.base

import android.content.Context
import android.os.Bundle

abstract class BaseLifecycleFragment<P : BaseLifecyclePresenter<*>> : BaseFragment() {
    protected lateinit var mPresenter: P

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPresenter = createPresenter()
        mPresenter.initLiveData(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mPresenter)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.onDestroyView(this)
    }

    protected abstract fun createPresenter(): P
}