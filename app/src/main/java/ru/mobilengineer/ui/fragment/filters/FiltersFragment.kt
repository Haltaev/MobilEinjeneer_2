package ru.mobilengineer.ui.fragment.filters

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_filters.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.fragment.BaseFragment
import javax.inject.Inject

class FiltersFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var filterMode = "n"
    var isConsiderSerialNumber = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_filters
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
//        authorizationViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        check_box_consider_serial_number.isActivated = preferencesManager.isConsiderSerialNumber
        check_box_consider_serial_number.setOnClickListener {
            it.isActivated = !it.isActivated
            isConsiderSerialNumber = it.isActivated
        }

        button_ascending_title.setOnClickListener {
            allButtonsDeactivate()
            it.isActivated = true
            filterMode = "n"
        }
        button_descending_title.setOnClickListener {
            allButtonsDeactivate()
            it.isActivated = true
            filterMode = "N"
        }
        button_ascending_date_of_receiving.setOnClickListener {
            allButtonsDeactivate()
            it.isActivated = true
            filterMode = "r"
        }
        button_descending_date_of_receiving.setOnClickListener {
            allButtonsDeactivate()
            it.isActivated = true
            filterMode = "R"
        }
        button_ascending_quantity.setOnClickListener {
            allButtonsDeactivate()
            it.isActivated = true
            filterMode = "q"
        }
        button_descending_quantity.setOnClickListener {
            allButtonsDeactivate()
            it.isActivated = true
            filterMode = "Q"
        }
        button_ascending_vendor_code.setOnClickListener {
            allButtonsDeactivate()
            it.isActivated = true
            filterMode = "v"
        }
        button_descending_vendor_code.setOnClickListener {
            allButtonsDeactivate()
            it.isActivated = true
            filterMode = "V"
        }

        button_confirm.setOnClickListener {
            preferencesManager.filterMode = filterMode
            preferencesManager.isConsiderSerialNumber = isConsiderSerialNumber
            activity?.supportFragmentManager?.popBackStack()
        }

        filter_button.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        preferencesManager.apply {
            when(filterMode) {
                "n" -> button_ascending_title.isActivated = true
                "N" -> button_descending_title.isActivated = true
                "r" -> button_ascending_date_of_receiving.isActivated = true
                "R" -> button_descending_date_of_receiving.isActivated = true
                "q" -> button_ascending_quantity.isActivated = true
                "Q" -> button_descending_quantity.isActivated = true
                "v" -> button_ascending_vendor_code.isActivated = true
                "V" -> button_descending_vendor_code.isActivated = true
            }
        }
    }

    private fun allButtonsDeactivate(){
        button_ascending_title.isActivated = false
        button_descending_title.isActivated = false
        button_ascending_date_of_receiving.isActivated = false
        button_descending_date_of_receiving.isActivated = false
        button_ascending_quantity.isActivated = false
        button_descending_quantity.isActivated = false
        button_ascending_vendor_code.isActivated = false
        button_descending_vendor_code.isActivated = false
    }
}
