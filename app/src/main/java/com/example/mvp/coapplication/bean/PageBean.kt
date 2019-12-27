package com.example.mvp.coapplication.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PageBean<T : Parcelable>(val curPage: Int, val datas: List<T>?) : Parcelable