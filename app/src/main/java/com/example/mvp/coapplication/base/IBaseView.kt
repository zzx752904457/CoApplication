package com.example.mvp.coapplication.base

interface IBaseView {
    fun showLoading()
    fun showEmpty()
    fun showError(errorCode: Int, errorMsg: String)
    fun showContent()
}