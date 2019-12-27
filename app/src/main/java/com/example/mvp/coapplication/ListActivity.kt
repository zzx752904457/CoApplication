package com.example.mvp.coapplication

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp.coapplication.base.BaseLifecycleActivity
import com.example.mvp.coapplication.bean.Article
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : BaseLifecycleActivity<ListPresenter>(), IListView {

    private val adapter = ListAdapter()

    override fun createPresenter(): ListPresenter {
        return ListPresenter(this)
    }

    override fun createLayoutResource(): Int {
        return R.layout.activity_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter.loadArticles()

        sr_layout.setOnRefreshListener {
            mPresenter.loadArticles()
        }

        sr_layout.setOnLoadMoreListener {
            mPresenter.loadArticles(true)
        }

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
        recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = 20
                } else {
                    outRect.top = 0
                }
                outRect.bottom = 20
            }
        })
    }

    private fun resetRefreshLayout() {
        sr_layout.finishRefresh()
        sr_layout.finishLoadMore()
    }

    override fun onRefreshSucceed(dataList: List<Article>) {
        resetRefreshLayout()
        adapter.refreshDataList(dataList)
    }

    override fun onLoadMoreSucceed(dataList: List<Article>) {
        resetRefreshLayout()
        adapter.addDataList(dataList)
    }

    override fun onLoadMoreFailed() {
        resetRefreshLayout()
        Log.e("协程测试", "加载更多出错")
    }
}