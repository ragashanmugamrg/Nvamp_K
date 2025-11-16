package com.amp.nvamp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.amp.nvamp.settings.ThemeManager

class NvampApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        val prefs = getSharedPreferences("PlayerPreferences", MODE_PRIVATE)
        val mode = prefs.getInt("theme_mode", ThemeManager.MODE_SYSTEM)
        ThemeManager.applyTheme(mode)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
