package com.androiddesenv.opiniaodetudo.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.androiddesenv.opiniaodetudo.MainActivity
import com.androiddesenv.opiniaodetudo.R

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        const val NIGHT_MODE_PREF = "night_mode_pref"
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        configNightMode()
    }
    private fun configNightMode() {
        val preference = preferenceManager.findPreference<Preference>(NIGHT_MODE_PREF)
        preference?.setOnPreferenceChangeListener { preference, newValue ->
            (activity as MainActivity).setNightMode()
            true
        }
    }
}