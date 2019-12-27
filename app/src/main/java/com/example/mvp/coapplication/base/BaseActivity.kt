package com.example.mvp.coapplication.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeSetContentView()
        setContentView(createLayoutResource())
    }

    protected open fun beforeSetContentView() {

    }

    protected abstract fun createLayoutResource(): Int
}