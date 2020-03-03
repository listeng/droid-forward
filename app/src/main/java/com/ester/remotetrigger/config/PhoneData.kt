package com.ester.remotetrigger.config

import android.os.Parcelable

/**
 * Created by tls on 2018/1/21.
 */
data class PhoneData(
        val tel: String,
        val name: String,
        val date: String,
        val content: String,
        val deviceId: String
)