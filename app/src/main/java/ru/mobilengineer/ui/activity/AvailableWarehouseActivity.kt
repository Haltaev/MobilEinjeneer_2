package ru.mobilengineer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.progress_bar.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.ui.fragment.available.AvailableWarehouseFragment
import ru.mobilengineer.ui.fragment.filters.FiltersFragment
import javax.inject.Inject

class AvailableWarehouseActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun getLayoutId(): Int = R.layout.activity_my_stock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        App.getComponent().inject(this)

        when (intent.getStringExtra(KEY)) {
            FROM_ENGINEER -> openAvailableWarehouseFragment(FROM_ENGINEER)
            FROM_WAREHOUSE -> openAvailableWarehouseFragment(FROM_WAREHOUSE)
        }
    }

    fun openAvailableWarehouseFragment(key: String) {

        val arguments = Bundle()
        arguments.putString(KEY, key)
        val fragment =
            AvailableWarehouseFragment()
        fragment.arguments = arguments
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
        const val KEY = "KEY"
        const val TITLE = "TITLE"
        const val ITEMS = "ITEMS"
        const val FROM_ENGINEER = "users"
        const val FROM_WAREHOUSE = "warehouses"

        fun open(
            context: Context,
            key: String
        ) {
            val intent = Intent(context, AvailableWarehouseActivity::class.java)
            intent.putExtra(KEY, key)
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