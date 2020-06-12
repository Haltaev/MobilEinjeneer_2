package ru.mobilengineer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity

class SplashActivity : AppCompatActivity() {

    private var handler = Handler()
    private var runnable = object : Runnable{
        override fun run() {
            startMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    private fun startMainActivity() {
        AuthorizationActivity.open(this)
        finish()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 1000)
    }
}