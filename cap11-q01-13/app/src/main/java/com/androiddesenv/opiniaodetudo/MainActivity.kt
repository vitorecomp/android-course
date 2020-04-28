package com.androiddesenv.opiniaodetudo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androiddesenv.opiniaodetudo.fragment.ListFragment
import com.androiddesenv.opiniaodetudo.fragment.SettingsFragment
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity(){

    private val fragments = mapOf(FORM_FRAGMENT to ::FormFragment, LIST_FRAGMENT to ::ListFragment, SETTINGS_FRAGMENT to ::SettingsFragment)

    companion object {
        const val FORM_FRAGMENT = R.id.menuitem_newitem
        const val LIST_FRAGMENT = R.id.menuitem_listitem
        const val SETTINGS_FRAGMENT = R.id.menuitem_settingsitem

        const val GPS_PERMISSION_REQUEST = 112

        const val PUSH_NOTIFICATION_MESSAGE_REQUEST = 1232
        const val PUSH_NOTIFICATION_CHANNEL = "PushNotificationChannel"

        const val NEW_REVIEW_NOTIFICATION_MESSAGE_REQUEST = 1233
        const val DELETE_NOTIFICATION_ACTION_NAME = "DELETE"
        const val DELETE_NOTIFICATION_EXTRA_NAME = "REVIEW_TO_DELETE"
    }

    private fun deleteReview(intent: Intent?) {
        if(intent?.action == DELETE_NOTIFICATION_ACTION_NAME){
            val id = intent.getStringExtra(DELETE_NOTIFICATION_EXTRA_NAME)
            object: AsyncTask<Void, Void, Unit>() {
                override fun doInBackground(vararg params: Void?) {
                    ReviewRepository(this@MainActivity.applicationContext).delete(id)
                }
            }.execute()
        }
    }

    override fun onNewIntent(intentParam: Intent?) {
        super.onNewIntent(intentParam)
        deleteReview(intentParam)
        intent = intentParam
    }

    private fun isImageShare(intent: Intent?): Boolean {
        return intent?.action == Intent.ACTION_SEND
    }

    private fun handleImageShare(intentParam: Intent?) {
        if(
            intentParam?.action == Intent.ACTION_SEND &&
            intentParam?.extras!!.get(Intent.EXTRA_STREAM) != null){
            selectMenu(FORM_FRAGMENT)
            navigateTo(FORM_FRAGMENT)
        }
    }

    private fun selectMenu(item: Int){
        val bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_main_menu)
        bottomNavigationMenu.selectedItemId = item
    }

    fun navigateTo(item: Int) {
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
                R.id.menuitem_listitem -> navigateTo(LIST_FRAGMENT)
                R.id.menuitem_newitem -> navigateTo(FORM_FRAGMENT)
                R.id.menuitem_settingsitem -> navigateTo(SETTINGS_FRAGMENT)
            }
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chooseTheme()
        setContentView(R.layout.activity_main)

        if(isImageShare(intent)){
            handleImageShare(intent)
        }else if(savedInstanceState == null){
            navigateTo(LIST_FRAGMENT)
        }

        configureBottomMenu()
        askForGPSPermission()
        logToken()
    }

    private fun logToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TOKEN_FCM", task.exception)
            } else {
                val token = task.result?.token
                Log.d("TOKEN_FCM", "logToken:${token}")
            }
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
