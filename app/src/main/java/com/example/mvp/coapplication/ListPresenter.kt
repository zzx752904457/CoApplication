package com.example.mvp.coapplication

import com.example.mvp.coapplication.base.BaseLifecyclePresenter
import com.example.mvp.coapplication.base.IBaseView
import com.example.mvp.coapplication.bean.Article
import com.example.mvp.coapplication.http.HttpFactory
import kotlinx.coroutines.Job

interface IListView : IBaseView {
    fun onRefreshSucceed(dataList: List<Article>)

    fun onLoadMoreSucceed(dataList: List<Article>)

    fun onLoadMoreFailed()
}

class ListPresenter(view: IListView) : BaseLifecyclePresenter<IListView>(view) {
    private var page = 0

    private var listJob: Job? = null

    companion object {
        const val KEY_REFRESH_SUCCEED = "KEY_REFRESH_SUCCEED"

        const val KEY_LOAD_MORE_SUCCEED = "KEY_LOAD_MORE_SUCCEED"

        const val KEY_LOAD_MORE_ERROR = "KEY_LOAD_MORE_ERROR"
    }

    override fun onLiveDataObserver(key: String, values: List<Any?>) {
        super.onLiveDataObserver(key, values)
        when (key) {
            KEY_REFRESH_SUCCEED -> view.onRefreshSucceed(values[0] as List<Article>)
            KEY_LOAD_MORE_SUCCEED -> view.onLoadMoreSucceed(values[0] as List<Article>)
            KEY_LOAD_MORE_ERROR -> view.onLoadMoreFailed()
        }
    }

    fun loadArticles(loadMore: Boolean = false) {
        listJob?.cancel()
        listJob = requestData(errorCallBack = { errorCode, errorMsg ->
            if (loadMore) {
                setLiveDataKeyAndValue(KEY_LOAD_MORE_ERROR)
            } else {
                showError(errorCode, errorMsg)
            }
        }){
            //请求数据bean
            val pageBean =
                operateResult(HttpFactory.instance.getService().getArticles(if (loadMore) page + 1 else 0))
            if (pageBean?.datas == null) {
                //数据列表对象为空
                if (loadMore) {
                    setLiveDataKeyAndValue(KEY_LOAD_MORE_ERROR)
                } else {
                    showError(-1, "网络错误")
                }
            } else {
                //请求成功
                page = if (loadMore) {
                    page + 1
                } else {
                    0
                }
                setLiveDataKeyAndValue(
                    if (loadMore) KEY_LOAD_MORE_SUCCEED else KEY_REFRESH_SUCCEED,
                    pageBean.datas
                )
            }
        }
    }
}

