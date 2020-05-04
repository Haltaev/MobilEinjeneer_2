package ru.mobileinjeneer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import ru.mobileinjeneer.common.PreferencesManager
import ru.mobileinjeneer.common.injectViewModel
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

//        success.text = preferencesManager.status
        observeViewModel(viewModel)
        viewModel.getContacts()
    }

    private fun observeViewModel(viewModel: MainViewModel) {
        viewModel.apply {
            dataLiveData.observe(this@MainActivity, Observer { resp ->
                success.text = preferencesManager.status
                Toast.makeText(context, "Эдгар, пош...", Toast.LENGTH_LONG).show()
                Toast.makeText(context, "Эдгар, я все сделал", Toast.LENGTH_LONG).show()
            })
        }
    }
}
