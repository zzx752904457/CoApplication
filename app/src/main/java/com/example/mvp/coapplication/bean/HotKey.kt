package com.example.mvp.coapplication.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HotKey(val id: Int, val list: String?, val name: String?) : Parcelable