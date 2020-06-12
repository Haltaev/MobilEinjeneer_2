package ru.mobilengineer.ui.fragment.installindevice

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_install_in_device_confirm.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.request.InstallCartridgeRequest
import ru.mobilengineer.data.api.model.response.DeviceResponse
import ru.mobilengineer.data.api.model.response.MyStockResponse
import ru.mobilengineer.ui.activity.ActionWithItemActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.fragment.BaseFragment
import javax.inject.Inject

class InstallInDeviceConfirmFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    lateinit var installInDeviceConfirmViewModel: InstallInDeviceConfirmViewModel

    var key = ""
    var device = ArrayList<DeviceResponse>()
    var counter = 1
    var quantity = 0
    var isScanned = true
    var items: ArrayList<MyStockResponse> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.fragment_install_in_device_confirm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        installInDeviceConfirmViewModel = injectViewModel(viewModelFactory)

        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
            device = Gson().fromJson(
                bundle.getString(TITLE, "[]"),
                object : TypeToken<ArrayList<DeviceResponse>>() {}.type
            )
            isScanned = bundle.getBoolean(IS_SCANNED, true)
            items = Gson().fromJson(
                bundle.getString(ITEMS, "{}"),
                object : TypeToken<ArrayList<MyStockResponse>>() {}.type
            )
        }
        items[0].quantity?.let {
            if (!items.isNullOrEmpty() && items.size == 1) {
                counter = it
                quantity = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        device[0].name?.let {
            device_confirm_title.text = it
        }


        observeViewModel(installInDeviceConfirmViewModel)

        items[0].name?.let {
            text_title.text = it
        }

        items[0].vendorCode?.let {
            art.text = "Арт: $it"
        }

        text_view_count.text = counter.toString()

        if (counter == quantity) {
            button_plus.isEnabled = false
        }
        if (quantity == 1) {
            button_minus.isEnabled = false
            button_plus.isEnabled = false
        }

        button_plus.setOnClickListener {
            text_view_count.text = (++counter).toString()
            button_minus.isEnabled = true
            if (counter >= quantity) {
                it.isEnabled = false
            }
        }
        button_minus.setOnClickListener {
            text_view_count.text = (--counter).toString()
            button_plus.isEnabled = true
            if (counter <= 1) {
                it.isEnabled = false
            }
        }

        back_button.setOnClickListener {
            if (isScanned) {
                ScannerActivity.open(
                    context = requireContext(),
                    key = key,
                    items = Gson().toJson(items)
                )
                (activity as ActionWithItemActivity).finish()
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }

        button_confirm.setOnClickListener {
            (activity as ActionWithItemActivity).showProgressBar()
            for (i in 1..counter) {
                val request = arrayListOf<InstallCartridgeRequest>(InstallCartridgeRequest())
                request[0].apply {
                    mcId = System.currentTimeMillis()
                    destinationId = device[0].id
                    destinationSide = 1
                    quantity = 1
                    skuId = items[0].skuId
                }

                installInDeviceConfirmViewModel.installInDevice(request)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isScanned) {
                ScannerActivity.open(
                    context = requireContext(),
                    key = key,
                    items = Gson().toJson(items)
                )
                (activity as ActionWithItemActivity).finish()
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }


    private fun observeViewModel(viewModel: InstallInDeviceConfirmViewModel) {
        viewModel.apply {
            installLiveData.observe(viewLifecycleOwner, Observer {
                (activity as ActionWithItemActivity).hideProgressBar()
                (activity as ActionWithItemActivity).finish()
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
        const val IS_SCANNED = "IS_SCANNED"
        const val ITEMS = "ITEMS"
        const val TITLE = "TITLE"
        const val DEVICE_TITLE = "Принтер RX300"
    }
}