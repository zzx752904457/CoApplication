package com.example.mvp.coapplication.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Banner(
    val desc: String?,
    val id: Int,
    val imagePath: String?,
    val title: String?,
    val url: String?
) : Parcelable