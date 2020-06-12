package ru.mobilengineer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.progress_bar.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.ui.fragment.filters.FiltersFragment
import ru.mobilengineer.ui.fragment.home.MyStockFragment
import javax.inject.Inject

class MyStockActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun getLayoutId(): Int = R.layout.activity_my_stock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        App.getComponent().inject(this)

        openHomeFragment()
    }

    fun openHomeFragment() {
        val fragment =
            MyStockFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openFiltersFragment() {
        val fragment =
            FiltersFragment()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    companion object {

        fun open(context: Context) {
            val intent = Intent(context, MyStockActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }
    fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }
}