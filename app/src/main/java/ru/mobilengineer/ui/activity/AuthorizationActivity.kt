package ru.mobilengineer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.fragment.authorization.AddTouchIdFragment
import ru.mobilengineer.ui.fragment.authorization.AuthorizationCodeFragment
import ru.mobilengineer.ui.fragment.authorization.AuthorizationFragment
import javax.inject.Inject

class AuthorizationActivity : BaseActivity() {
    @Inject
    lateinit var preferencesManager: PreferencesManager


    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)

        if (preferencesManager.isAuthCompleted) {
            openCodeFragmentWithoutBackStack()
        }
        else if (savedInstanceState == null) {
            openAuthFragment()
        }

    }

    fun openAuthFragment() {
        val fragment =
            AuthorizationFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
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

    fun openCodeFragmentWithoutBackStack() {
        val fragment =
            AuthorizationCodeFragment()
        supportFragmentManager
            .beginTransaction()
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
