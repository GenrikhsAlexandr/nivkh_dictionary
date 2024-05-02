package com.aleksandrgenrikhs.nivkhdictionary.domain

import android.content.Context

interface NetworkConnectionChecker {

    fun isNetworkConnected(context: Context): Boolean
}