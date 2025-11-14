package com.amp.nvamp.settings

import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    fun applyTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    const val MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    const val MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES
    const val MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
}