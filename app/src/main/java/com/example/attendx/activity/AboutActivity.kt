package com.example.attendx.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.attendx.MainActivity
import com.example.attendx.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val nav = findViewById<BottomNavigationView>(R.id.nav5)
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.backHome -> {
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    true // Return true to indicate that the item has been selected
                }
                else -> false // Return false for other items
            }
        }
    }
}