package ru.mobilengineer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.ui.activity.AuthorizationActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.getComponent().inject(this)
        viewModel = injectViewModel(viewModelFactory)

        AuthorizationActivity.open(this)
        finish()
    }

    private fun observeViewModel(viewModel: MainViewModel) {
        viewModel.apply {
            dataLiveData.observe(this@MainActivity, Observer { resp ->

            })
        }
    }

    companion object {
        fun open(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
