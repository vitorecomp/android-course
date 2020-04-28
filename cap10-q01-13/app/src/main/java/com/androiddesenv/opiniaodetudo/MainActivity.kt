package com.androiddesenv.opiniaodetudo

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androiddesenv.opiniaodetudo.fragment.ListFragment
import com.androiddesenv.opiniaodetudo.fragment.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    private val fragments = mapOf(FORM_FRAGMENT to ::FormFragment, LIST_FRAGMENT to ::ListFragment, SETTINGS_FRAGMENT to ::SettingsFragment)

    companion object {
        const val FORM_FRAGMENT = R.id.menuitem_newitem
        const val LIST_FRAGMENT = R.id.menuitem_listitem
        const val SETTINGS_FRAGMENT = R.id.menuitem_settingsitem
        const val GPS_PERMISSION_REQUEST = 112
    }

    public fun navigateTo(item: Int) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        val fragmentInstance: Fragment = fragments[item]?.invoke()!!
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, fragmentInstance)
            .addToBackStack(fragments[item]?.name)
            .commit()
    }

    private fun configureBottomMenu() {
        val bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_main_menu)
        bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuitem_newitem -> navigateTo(FORM_FRAGMENT)
                R.id.menuitem_listitem -> navigateTo(LIST_FRAGMENT)
                R.id.menuitem_settingsitem -> navigateTo(SETTINGS_FRAGMENT)
            }
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chooseTheme()
        setContentView(R.layout.activity_main)

        configureBottomMenu()
        askForGPSPermission()

        if(savedInstanceState == null){
            navigateTo(FORM_FRAGMENT)
        }
    }

    private fun chooseTheme() {
        val nightMode = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(SettingsFragment.NIGHT_MODE_PREF, false)
        if(nightMode) {
            setTheme(R.style.AppThemeNight_NoActionBar)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }
    }
    fun setNightMode(){
        recreate()
    }

    private fun askForGPSPermission() {
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                GPS_PERMISSION_REQUEST )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            GPS_PERMISSION_REQUEST -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,
                        "Permissão para usar o GPS concedida",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }
    fun navigateWithBackStack(destiny: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, destiny)
            .addToBackStack(null)
            .commit()
    }
}
