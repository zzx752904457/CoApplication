package com.example.mvp.coapplication

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.example.mvp.coapplication.base.BaseLifecyclePresenter
import com.example.mvp.coapplication.base.IBaseView
import com.example.mvp.coapplication.base.Result
import com.example.mvp.coapplication.bean.Article
import com.example.mvp.coapplication.bean.Banner
import com.example.mvp.coapplication.bean.HotKey
import com.example.mvp.coapplication.bean.PageBean
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

    override fun onCreate() {
        super.onCreate()
//        launch {
//            while (true) {
//                delay(2000)
//                Log.e("协程测试", "隔了两秒执行该方法")
//            }
//        }
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

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        Log.e("协程测试", "已经走了onResume")
    }

    fun getDataByMainScope() {
        mainJob?.cancel()
        mainJob = requestData {
            Log.e("协程测试", "开始执行getConcurrentDataByMainScope()")
            val hotKeys =
                operateResult(HttpFactory.instance.getService().getHotKeys()) ?: return@requestData
            setLiveDataKeyAndValue(KEY_MAIN, hotKeys)
        }
        requestData {
            repeat(20) {
                Log.e("协程测试", "2开始执行$it")
            }
        }
        repeat(20) {
            Log.e("协程测试", "1开始执行$it")
        }
    }

    fun getDataByLiveDataScope() {
        liveData {
            Log.e("协程测试", "开始执行getDataByLiveDataScope()")
            try {
                val banners =
                    operateResult(HttpFactory.instance.getService().getBanners())
                        ?: return@liveData
                emit(banners)
            } catch (e: Exception) {
                Log.e("协程测试", e.message ?: "")
            }
        }.observe(owner, Observer {
            view.getDataByLiveDataScopeSucceed(it)
        })
    }

    /**
     * 发起串行请求
     */
    fun getDataBySerial() {
        requestData {
            Log.e("协程测试", "开始执行getDataBySerial()")
            val t1 = System.currentTimeMillis()
            val dataList = mutableListOf<Article>()
            for (i in 0 until 30) {
                dataList.addAll(
                    operateResult(HttpFactory.instance.getService().getArticles(i))?.datas
                        ?: continue
                )
            }
            val t2 = System.currentTimeMillis() - t1
            Log.e("协程测试", t2.toString())
            setLiveDataKeyAndValue(KEY_SERIAL, dataList)
        }
    }

    /**
     * 发起第一种并行请求
     */
    fun getDataByConcurrent1() {
        requestData {
            Log.e("协程测试", "开始执行getDataByConcurrent1()")
            val t1 = System.currentTimeMillis()
            val deferredList = mutableListOf<Deferred<Result<PageBean<Article>>>>()
            val dataList = mutableListOf<Article>()
            repeat(30) {
                val deferred = async {
                    HttpFactory.instance.getService().getArticles(it)
                }
                deferredList.add(deferred)
            }
            for (deferred in deferredList) {
                dataList.addAll(operateResult(deferred.await())?.datas ?: continue)
            }
            val t2 = System.currentTimeMillis() - t1
            Log.e("协程测试", t2.toString())
            setLiveDataKeyAndValue(KEY_CONCURRENT, dataList)
        }
    }

    /**
     * 发起第二种并行请求
     */
    fun getDataByConcurrent2() {
        requestData {
            Log.e("协程测试", "开始执行getDataByConcurrent2()")
            val t1 = System.currentTimeMillis()
            try {
                val deferredList = mutableListOf<Deferred<Result<PageBean<Article>>>>()
                val dataList = mutableListOf<Article>()
                repeat(30) {
                    val deferred = HttpFactory.instance.getService().getArticlesAsync(it)
                    deferredList.add(deferred)
                }
                for (deferred in deferredList) {
                    dataList.addAll(operateResult(deferred.await())?.datas ?: continue)
                }
                val t2 = System.currentTimeMillis() - t1
                Log.e("协程测试", t2.toString())
                setLiveDataKeyAndValue(KEY_CONCURRENT, dataList)
            } catch (e: Exception) {
                Log.e("协程测试", e.message ?: "")
            }
        }
    }

    /**
     * 模拟在子线程执行操作，完毕后再回调给主线程
     */
    fun runThreadDispatch() {
        launch {
            Log.e("协程测试", "开始执行runThreadDispatch()")
            val t1 = System.currentTimeMillis()
            val map = hashMapOf<Int, Int>()
            withContext(Dispatchers.Default) {
                Log.e("协程测试", "开始循环: ${Thread.currentThread().name}")
                repeat(50000000) {
                    map[0] = it
                }
                Log.e("协程测试", "结束循环: ${Thread.currentThread().name}")
            }
            val t2 = System.currentTimeMillis() - t1
            Log.e("协程测试", Thread.currentThread().name)
            Log.e("协程测试", "耗时：$t2")
        }
    }


}