package com.androiddesenv.opiniaodetudo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.androiddesenv.opiniaodetudo.fragment.ListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    private val fragments = mapOf(FORM_FRAGMENT to ::FormFragment, LIST_FRAGMENT to ::ListFragment)
    companion object {
        val FORM_FRAGMENT = "formFragment"
        val LIST_FRAGMENT = "listFragment"
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
    }
}
