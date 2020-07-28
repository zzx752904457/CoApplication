package com.example.mvp.coapplication.base

import java.lang.Exception

class UnexpectCodeException : Exception() {
    var errorCode: Int = -1

    var errorMessage : String? = null

    var jsonStr: String? = null
}