package ru.mobilengineer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.ui.fragment.home.HomeFragment
import javax.inject.Inject

class HomeActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var id: Int? = null
//    private var adapterNews: GroupNewsAdapter? = null
//    private var myGroup: GroupResponse? = null

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        App.getComponent().inject(this)

        openHomeFragment()
    }

    fun openHomeFragment() {
        val fragment =
            HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    companion object {

        fun open(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}