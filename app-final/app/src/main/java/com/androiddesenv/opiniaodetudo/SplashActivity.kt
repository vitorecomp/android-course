package com.androiddesenv.opiniaodetudo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.androiddesenv.opiniaodetudo.fragment.SettingsFragment

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        chooseTheme()
        val handler = Handler()
        handler.postDelayed({ mostrarMainActivity() }, 2000)

    }

    private fun chooseTheme(){
        val nightMode = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(SettingsFragment.NIGHT_MODE_PREF, false)
        if(nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.AppThemeNight_NoActionBar)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            setTheme(R.style.AppTheme_NoActionBar)
        }
    }

    private fun mostrarMainActivity() {
        val intent = Intent(
            this@SplashActivity, MainActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}