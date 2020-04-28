package com.androiddesenv.opiniaodetudo

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.androiddesenv.opiniaodetudo.fragment.ListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    private val fragments = mapOf(FORM_FRAGMENT to ::FormFragment, LIST_FRAGMENT to ::ListFragment)
    companion object {
        val FORM_FRAGMENT = "formFragment"
        val LIST_FRAGMENT = "listFragment"
        val GPS_PERMISSION_REQUEST = 112
    }

    public fun navigateTo(item: String) {
        val fragmentInstance: Fragment = fragments[item]?.invoke()!!
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragmentInstance)
            .addToBackStack(item)
            .commit()
    }

    private fun configureBottomMenu() {
        val bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_main_menu)
        bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuitem_newitem -> navigateTo(FORM_FRAGMENT)
                R.id.menuitem_listitem -> navigateTo(LIST_FRAGMENT)
            }
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigateTo(FORM_FRAGMENT)
        configureBottomMenu()
        askForGPSPermission()
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
}
