package ru.mobileinjeneer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.mobileinjeneer.R
import ru.mobileinjeneer.ui.fragment.authorization.AddTouchIdFragment
import ru.mobileinjeneer.ui.fragment.authorization.AuthorizationCodeFragment
import ru.mobileinjeneer.ui.fragment.authorization.AuthorizationFragment

class AuthorizationActivity : BaseActivity() {


    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            openAuthFragment()
        }
    }

    fun openAuthFragment() {
        val fragment =
            AuthorizationFragment()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(R.id.fragment_container, fragment)
            .commit()
    }

    fun openCodeFragment() {
        val fragment =
            AuthorizationCodeFragment()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openTouchFragment() {
        val fragment =
            AddTouchIdFragment()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    companion object {
        fun open(context: Context) {
            val intent = Intent(context, AuthorizationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
