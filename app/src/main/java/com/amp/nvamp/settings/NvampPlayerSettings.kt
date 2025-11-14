package com.amp.nvamp.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.amp.nvamp.MainActivity.Companion.playerViewModel
import com.amp.nvamp.R
import com.amp.nvamp.viewmodel.PlayerViewModel
import com.google.android.material.color.DynamicColors
import com.google.android.material.materialswitch.MaterialSwitch

class NvampPlayerSettings : AppCompatActivity() {


    lateinit var switch: MaterialSwitch


    companion object{
        lateinit var playerViewModel: PlayerViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nvamp_player_settings)

        playerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        switch  =  findViewById(R.id.dark_mode)


        switch.isChecked = isDarkModeEnabled()

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ThemeManager.applyTheme(ThemeManager.MODE_DARK)
                playerViewModel.saveDarkMode(ThemeManager.MODE_DARK)
            } else {
                ThemeManager.applyTheme(ThemeManager.MODE_LIGHT)
                playerViewModel.saveDarkMode(ThemeManager.MODE_LIGHT)
            }
        }


    }

    private fun isDarkModeEnabled(): Boolean {
        val prefs = getSharedPreferences("PlayerPreferences", MODE_PRIVATE)
        val mode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        return when (mode) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> {
                val currentNightMode = resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK
                currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }
}