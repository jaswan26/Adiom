package com.homeautomation.adiom

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
    }

    override fun onStart() {
        super.onStart()
        netValidator()

    }

    fun netValidator() {
        Handler().postDelayed({
            forNext()
        }, 2500)
    }

    private fun forNext() {
        startActivity(Intent(this, HomeActivity::class.java))
        this.finish()
    }
}
