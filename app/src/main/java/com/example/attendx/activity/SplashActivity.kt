package com.example.attendx.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.attendx.R
import com.example.attendx.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        val logoAnimationDuration = 600L
        val splashDuration = 4000L

        Handler(Looper.getMainLooper()).postDelayed({
            val set = AnimatorInflater.loadAnimator(this, R.animator.logo_animator) as AnimatorSet
            set .setTarget(binding.splashLogo)
            set.start()

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, SplashSlider::class.java))
                finish()
            },logoAnimationDuration)
        },splashDuration)

    }
}