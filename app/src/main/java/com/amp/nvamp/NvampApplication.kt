package com.amp.nvamp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors

class NvampApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}