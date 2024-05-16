package com.example.attendx

import android.content.Intent
import android.Manifest
import android.annotation.SuppressLint

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.attendx.activity.AboutActivity
import com.example.attendx.activity.SettingsActivity
import com.example.attendx.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private var MY_PERMISSIONS_REQUEST_READ_LOCATION = 1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide() // hide the title bar

        setContentView(binding.root)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_READ_LOCATION
            )
        } else {
            if (savedInstanceState == null) {
                drawerLayout = binding.drawerLayout
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
                navController = navHostFragment.navController

                NavigationUI.setupWithNavController(binding.navView, navController)
            }
        }

        val navigationView = binding.navView
        val drawer = binding.drawerLayout
        val hView = navigationView.getHeaderView(0)
        val text = hView.findViewById<TextView>(R.id.textView20)

        text.text = "Welcome User"

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.about -> {
                    val i = Intent(this, AboutActivity::class.java)
                    drawer.closeDrawers()
                    startActivity(i)
                }
                R.id.setting -> {
                    val i = Intent(this, SettingsActivity::class.java)
                    drawer.closeDrawers()
                    startActivity(i)
                }
            }
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Hey", "I am not empty")
                } else {
                    this.finishAffinity()
                }
            }
        }
    }
}
