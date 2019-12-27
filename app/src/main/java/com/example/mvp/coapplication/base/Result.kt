package com.example.mvp.coapplication.base

data class Result<T>(val errorCode: Int, val errorMsg: String?, val data: T?)