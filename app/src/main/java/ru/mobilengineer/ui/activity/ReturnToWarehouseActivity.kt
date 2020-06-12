package ru.mobilengineer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.fragment.returntowarehouse.ReturnToWarehouseFragment
import javax.inject.Inject

class ReturnToWarehouseActivity : BaseActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager


    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)

        openReturnToWarehouseFragment()
    }

    fun openReturnToWarehouseFragment() {
        val fragment =
            ReturnToWarehouseFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    companion object {
        fun open(context: Context) {
            val intent = Intent(context, ReturnToWarehouseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
