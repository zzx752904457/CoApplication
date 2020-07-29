package com.example.mvp.coapplication

import android.util.Log
import com.example.mvp.coapplication.base.BaseLifecyclePresenter
import com.example.mvp.coapplication.base.IBaseView
import com.example.mvp.coapplication.bean.Article
import com.example.mvp.coapplication.bean.Banner
import com.example.mvp.coapplication.bean.HotKey
import com.example.mvp.coapplication.http.HttpFactory
import kotlinx.coroutines.*
import java.lang.Exception

interface IMainView : IBaseView {
    fun getDataByMainScopeSucceed(dataList: List<HotKey>)

    fun getDataByLifecycleScopeSucceed(count: Int, sum: Int)

    fun getDataByLiveDataScopeSucceed(dataList: List<Banner>)

    fun getArticlesSucceed(dataList: List<Article>)
}

class MainPresenter(view: IMainView) : BaseLifecyclePresenter<IMainView>(view) {
    private var mainJob: Job? = null

    companion object {
        const val KEY_LIFECYCLE = "KEY_LIFECYCLE"

        const val KEY_MAIN = "KEY_MAIN"

        const val KEY_SERIAL = "KEY_SERIAL"

        const val KEY_CONCURRENT = "KEY_CONCURRENT"
    }

    override fun onLiveDataObserver(key: String, values: List<Any?>) {
        super.onLiveDataObserver(key, values)
        when (key) {
            KEY_LIFECYCLE -> view.getDataByLifecycleScopeSucceed(values[0] as Int, values[1] as Int)

            KEY_MAIN -> view.getDataByMainScopeSucceed(
                values[0] as List<HotKey>
            )

            KEY_SERIAL -> view.getArticlesSucceed(values[0] as List<Article>)

            KEY_CONCURRENT -> view.getArticlesSucceed(values[0] as List<Article>)
        }
    }

    fun getDataByLifecycleScope() {
        getLifecycleScope().launchWhenResumed {
            Log.e("协程测试", "开始执行getDataByLifecycleScope()")
            var count = 0
            var sum = 0
            repeat(50) {
                count += 1
                sum += 100
            }
            setLiveDataKeyAndValue(KEY_LIFECYCLE, count, sum)
        }
    }

    /**
     * @method  getData
     * @description  基础使用，不做任何封装
     * @date: 2020/7/29 2:21 PM
     * @author: ZhaoXuan.Zeng
     * @param []
     * @return
     */
    fun getData() {
        launch {
            try {
                //Retrofit内部处理的线程的切换调度，所以我们只需要关心结果就行了
                val result = HttpFactory.instance.getService().getHotKeys()
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getDataByMainScope() {
        mainJob?.cancel()
        mainJob = requestData {
            val hotKeys = operateResult(HttpFactory.instance.getService().getHotKeys()) ?: return@requestData
            setLiveDataKeyAndValue(KEY_MAIN, hotKeys)
        }
    }

    /**
     * @method  getOriginalResult
     * @description  获取原始接口结果
     * @date: 2020/7/29 2:21 PM
     * @author: ZhaoXuan.Zeng
     * @param []
     * @return
     */
    fun getOriginalResult() {
        requestData {
            val body = HttpFactory.instance.getService().getHotKeys2()
            Log.e("协程测试", body.string())
        }
    }

    /**
     * 模拟在子线程执行操作，完毕后再回调给主线程
     */
    fun runThreadDispatch() {
        launch {
            val t1 = System.currentTimeMillis()
            val map = hashMapOf<Int, Int>()
            withContext(Dispatchers.Default) {
                repeat(50000000) {
                    map[0] = it
                }
            }
            val t2 = System.currentTimeMillis() - t1
            Log.e("协程测试", "耗时：$t2")
        }
    }

    /**
     * 发起并行请求
     */
    fun getDataByConcurrent2() {
        requestData {
            try {
                val deferred1 = HttpFactory.instance.getService().getArticlesAsync(0)
                val deferred2 = HttpFactory.instance.getService().getArticlesAsync(1)
                Log.e("协程测试", "${deferred1.await()}, ${deferred2.await()}")
            } catch (e: Exception) {
                Log.e("协程测试", e.message ?: "")
            }
        }
    }
}