package com.example.mvp.coapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.mvp.coapplication.base.BaseLifecycleActivity
import com.example.mvp.coapplication.bean.Article
import com.example.mvp.coapplication.bean.Banner
import com.example.mvp.coapplication.bean.HotKey
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseLifecycleActivity<MainPresenter>(), IMainView {

    override fun createPresenter(): MainPresenter {
        return MainPresenter(this)
    }

    override fun createLayoutResource(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mPresenter.getDataByLifecycleScope()

        btn_main_scope.setOnClickListener {
            mPresenter.getDataByMainScope()
        }

        btn_concurrent_request_2.setOnClickListener {
            mPresenter.getDataByConcurrent2()
        }

        btn_dispatch.setOnClickListener {
            mPresenter.runThreadDispatch()
        }

        btn_open.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListActivity::class.java).apply {
                Toast.makeText(this@MainActivity, "启动列表页", Toast.LENGTH_SHORT).show()
            })
        }

    }

    override fun getDataByMainScopeSucceed(dataList: List<HotKey>) {
        val list = arrayListOf<String?>()
        for (hotKey in dataList) {
            list.add(hotKey.name)
        }
        Log.e("协程测试", "热门词汇请求成功：$list")
    }

    override fun getDataByLifecycleScopeSucceed(count: Int, sum: Int) {
        Log.e("协程测试", "执行了：${count}次，最终结果是：$sum")
    }

    override fun getDataByLiveDataScopeSucceed(dataList: List<Banner>) {
        val list = arrayListOf<String?>()
        for (banner in dataList) {
            list.add(banner.imagePath)
        }
        Log.e("协程测试", "banner图请求成功：$list")
    }

    override fun getArticlesSucceed(dataList: List<Article>) {
        val list = arrayListOf<String?>()
        for (article in dataList) {
            list.add(article.title)
        }
        Log.e("协程测试", "Article列表请求成功：$list")
    }

}
