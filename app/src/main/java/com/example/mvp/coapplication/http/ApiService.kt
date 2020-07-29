package com.example.mvp.coapplication.http

import com.example.mvp.coapplication.bean.Banner
import com.example.mvp.coapplication.base.Result
import com.example.mvp.coapplication.bean.Article
import com.example.mvp.coapplication.bean.HotKey
import com.example.mvp.coapplication.bean.PageBean
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    //获取banner数据
    @GET("banner/json")
    suspend fun getBanners(): Result<List<Banner>>

    //获取热门词汇数据
    @GET("hotkey/json")
    suspend fun getHotKeys(): Result<List<HotKey>>

    @GET("hotkey/json")
    suspend fun getHotKeys2(): ResponseBody

    @GET("article/list/{page}/json")
    suspend fun getArticles(@Path("page") page: Int): Result<PageBean<Article>>

    @GET("article/list/{page}/json")
    fun getArticlesAsync(@Path("page") page: Int): Deferred<Result<PageBean<Article>>>
}