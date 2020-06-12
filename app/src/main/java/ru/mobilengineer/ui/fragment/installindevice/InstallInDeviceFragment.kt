package ru.mobilengineer.ui.fragment.installindevice

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_install_in_device.*
import kotlinx.android.synthetic.main.fragment_install_in_device.art
import kotlinx.android.synthetic.main.fragment_install_in_device.back_button
import kotlinx.android.synthetic.main.fragment_install_in_device.text_title
import kotlinx.android.synthetic.main.fragment_install_in_device_confirm.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.response.MyStockResponse
import ru.mobilengineer.ui.activity.ActionWithItemActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.fragment.BaseFragment
import javax.inject.Inject

class InstallInDeviceFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    lateinit var installInDeviceViewModel: InstallInDeviceViewModel

    var quantity = ""
    var key = ""
    var deviceName = ""
    var items: ArrayList<MyStockResponse> = arrayListOf()
    var test:Boolean = false
    var itemsForSearch: ArrayList<MyStockResponse> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.fragment_install_in_device
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        installInDeviceViewModel = injectViewModel(viewModelFactory)
        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
            deviceName = bundle.getString(TITLE, "")
            items = Gson().fromJson(
                bundle.getString(ITEMS, "{}"),
                object : TypeToken<ArrayList<MyStockResponse>>() {}.type
            )
        }
//        if (quantity.isEmpty()) {
//            counter = quantity.toInt()
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel(installInDeviceViewModel)

        items[0].name?.let {
            text_title.text = it
        }

        items[0].vendorCode?.let {
            art.text = "Арт: $it"
        }

//        text_view_count.text = counter.toString()

        back_button.setOnClickListener {
            ScannerActivity.open(
                context = requireContext(),
                key = key,
                items = Gson().toJson(items)
            )
            (activity as ActionWithItemActivity).finish()
        }

        button_next.setOnClickListener {
            test = true
            (activity as ActionWithItemActivity).showProgressBar()

            installInDeviceViewModel.getDeviceInfo(device_serial_number.text.toString())
        }

        sheet_counter.setOnClickListener {

        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            ScannerActivity.open(
                context = requireContext(),
                key = key,
                items = Gson().toJson(items)
            )
            (activity as ActionWithItemActivity).finish()
        }
    }

    override fun onResume() {
        super.onResume()
        test = false
    }

    override fun onPause() {
        super.onPause()
        test = false
    }

    private fun observeViewModel(viewModel: InstallInDeviceViewModel) {
        viewModel.apply {
            if (dataLiveData.hasObservers())
                dataLiveData.removeObservers(viewLifecycleOwner)
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as ActionWithItemActivity).hideProgressBar()
                if(test) {
                    (activity as ActionWithItemActivity).openInstallInDeviceConfirmFragment(
                        key = key,
                        isScanned = false,
                        items = Gson().toJson(items),
                        title = Gson().toJson(resp)
                    )
//                    test = false
                }
            })
            errorLiveData.observe(viewLifecycleOwner, Observer { errorResp ->
                (activity as ActionWithItemActivity).hideProgressBar()
                when (errorResp) {
                    401 -> {
                        preferencesManager.apply {
                            isAuthCompleted = false
                            isTouchIdAdded = false
                            isPasscodeChanging = false
                            passcode = null
                        }
                        AuthorizationActivity.open(requireContext())
                        activity?.finishAffinity()
                    }
                    404 -> {
                        Toast.makeText(context, "Устройство не найдено", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            "Error: Check Internet connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        }
    }

    companion object {
        const val KEY = "KEY"
        const val ITEMS = "ITEMS"
        const val TITLE = "TITLE"
    }
}