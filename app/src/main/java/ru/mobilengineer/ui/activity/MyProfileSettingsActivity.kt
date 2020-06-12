package ru.mobilengineer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.progress_bar.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.fragment.authorization.AuthorizationCodeFragment
import ru.mobilengineer.ui.fragment.profile.EditProfileNameFragment
import ru.mobilengineer.ui.fragment.profile.ProfileSettingsFragment
import javax.inject.Inject

class MyProfileSettingsActivity : BaseActivity() {
    @Inject
    lateinit var preferencesManager: PreferencesManager


    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)

        openProfileSettingsFragment()

    }

    fun openEditProfileNameFragment() {
        val fragment =
            EditProfileNameFragment()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openAuthorizationCodeFragment() {
        val fragment =
            AuthorizationCodeFragment()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openProfileSettingsFragment() {
        val fragment =
            ProfileSettingsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }
    fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

    companion object {
        fun open(context: Context) {
            val intent = Intent(context, MyProfileSettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}