package com.example.mvp.coapplication.base

import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.Exception


open class BaseLifecyclePresenter<V : IBaseView>(protected val view: V) : BaseLifecycleCallBack,
    CoroutineScope by MainScope() {
    private val mutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val map = hashMapOf<String, List<Any?>>()
    protected lateinit var owner: LifecycleOwner

    companion object {
        private const val CODE_UN_LOGIN = 1001
        private const val CODE_LOGIN_TIMEOUT = 1002

        private const val KEY_SHOW_LOADING = "KEY_SHOW_LOADING"
        private const val KEY_SHOW_ERROR = "KEY_SHOW_ERROR"
        private const val KEY_SHOW_EMPTY = "KEY_SHOW_EMPTY"
        private const val KEY_SHOW_CONTENT = "KEY_SHOW_CONTENT"
    }

    fun initLiveData(owner: LifecycleOwner) {
        this.owner = owner
        mutableLiveData.observe(owner, object : Observer<String> {
            override fun onChanged(t: String?) {
                t ?: return
                val values = map[t] ?: return
                onLiveDataObserver(t, values)
            }
        })
    }

    override fun onCreate() {
    }

    override fun onStart(owner: LifecycleOwner) {
    }

    override fun onStop(owner: LifecycleOwner) {
    }

    override fun onResume(owner: LifecycleOwner) {
    }

    override fun onPause(owner: LifecycleOwner) {
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (owner is AppCompatActivity) {
            cancel()
        }
    }

    open fun onDestroyView(owner: LifecycleOwner) {
        cancel()
    }

    open fun onLiveDataObserver(key: String, values: List<Any?>) {
        when (key) {
            KEY_SHOW_LOADING -> view.showLoading()
            KEY_SHOW_ERROR -> view.showError(values[0] as Int, values[1] as String)
            KEY_SHOW_EMPTY -> view.showEmpty()
            KEY_SHOW_CONTENT -> view.showContent()
        }
    }

    fun setLiveDataKeyAndValue(key: String, vararg values: Any?) {
        val list = arrayListOf<Any?>()
        values.forEach {
            list.add(it)
        }
        map[key] = list
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mutableLiveData.value = key
        } else {
            mutableLiveData.postValue(key)
        }
    }

    fun showLiveDataKeyAndValue(key: String, vararg values: Any?) {
        val list = arrayListOf<Any?>()
        values.forEach {
            list.add(it)
        }
        map[key] = list
        onLiveDataObserver(key, list)
    }

    protected fun requestData(
        showToast : Boolean = true,
        errorCallBack: ((errorCode: Int, errorMsg: String) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch {
            try {
                block.invoke(this)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Log.e("协程测试", "请求取消")
                } else if (e is UnexpectCodeException) {
                    val errorMsg = e.errorMessage ?: "网络错误"
                    val errorCode = e.errorCode
                    if (errorCode == CODE_UN_LOGIN || errorCode == CODE_LOGIN_TIMEOUT) {
                        login()
                    } else {
                        if (errorCallBack == null) {
                            showError(errorCode, errorMsg)
                        } else {
                            errorCallBack.invoke(errorCode, errorMsg)
                        }
                    }
                    if (showToast) {
                        Toast.makeText()
                    }
                } else {
                    if (errorCallBack == null) {
                        showError(-1, "网络错误")
                    } else {
                        errorCallBack.invoke(-1, "网络错误")
                    }
                    if (showToast) {
                        Toast.makeText()
                    }
                }
            }
        }
    }

    protected fun <T> operateResult(result: Result<T>, needUnexpectedResult: Boolean = false): T? {
        when (result.errorCode) {
            0 -> {
                return result.data
            }
            else -> {
                if (needUnexpectedResult) {
                    return result.data
                } else {
                    throw UnexpectCodeException().apply {
                        errorCode = result.errorCode
                        errorMessage = result.errorMsg
                        jsonStr = if (result.data == null) null else result.data.toString()
                    }
                }
            }
        }
    }

    private fun login() {

    }

    protected fun getLifecycleScope(): LifecycleCoroutineScope {
        return owner.lifecycleScope
    }

    protected fun showLoading() {
        setLiveDataKeyAndValue(KEY_SHOW_LOADING)
    }

    protected fun showError(errorCode: Int, errorMsg: String?) {
        setLiveDataKeyAndValue(KEY_SHOW_ERROR, errorCode, errorMsg)
    }

    protected fun showEmpty() {
        setLiveDataKeyAndValue(KEY_SHOW_EMPTY)
    }

    protected fun showContent() {
        setLiveDataKeyAndValue(KEY_SHOW_CONTENT)
    }
}