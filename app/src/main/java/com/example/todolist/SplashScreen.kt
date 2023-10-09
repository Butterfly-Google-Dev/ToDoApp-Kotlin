package com.example.todolist

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView

class SplashScreen : AppCompatActivity() {

    private lateinit var splashText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashText = findViewById(R.id.splTxt)

        applyShakeAnimation()

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun applyShakeAnimation() {
        val animator = ObjectAnimator.ofFloat(splashText, "translationX", -20f, 20f)
        animator.duration = 400
        animator.repeatCount = 5
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }
}