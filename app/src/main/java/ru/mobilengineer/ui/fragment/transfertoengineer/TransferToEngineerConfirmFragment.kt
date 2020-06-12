package ru.mobilengineer.ui.fragment.transfertoengineer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer_confirm.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.request.MovingCreateRequest
import ru.mobilengineer.data.api.model.response.AnotherEngineersResponse
import ru.mobilengineer.data.api.model.response.MyStockResponse
import ru.mobilengineer.ui.activity.ActionWithItemActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.adapter.OutGoingItemsAdapter
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.TransferToEngineerConfirmViewModel
import javax.inject.Inject


class TransferToEngineerConfirmFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: OutGoingItemsAdapter? = null

    lateinit var transferToEngineerConfirmViewModel: TransferToEngineerConfirmViewModel

    var counter = 1
    var quantity = 0
    var key = ""
    var anotherEngineer: AnotherEngineersResponse = AnotherEngineersResponse()
    var isSingleItem = true
    var isScanned = true
    var transferItems: ArrayList<MyStockResponse> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.fragment_transfer_to_engineer_confirm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        transferToEngineerConfirmViewModel = injectViewModel(viewModelFactory)

        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
            transferItems = Gson().fromJson(
                bundle.getString(ITEMS, "{}"),
                object : TypeToken<ArrayList<MyStockResponse>>() {}.type
            )

            anotherEngineer = Gson().fromJson(
                bundle.getString(TITLE, ""),
                object : TypeToken<AnotherEngineersResponse>() {}.type
            )

            isScanned = bundle.getBoolean(IS_SCANNED, true)

            counter = bundle.getInt(QUANTITY)

        }
        isSingleItem = (transferItems.size == 1)
        transferItems[0].quantity?.let {
            if (!transferItems.isNullOrEmpty() && transferItems.size == 1) {
                quantity = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel(transferToEngineerConfirmViewModel)

        if (transferItems.size > 1) {
            context?.let { ctx ->
                adapter = OutGoingItemsAdapter(
                    transferItems,
                    ctx
                )
                transfer_to_engineer_recycler_view.layoutManager = LinearLayoutManager(ctx)
                transfer_to_engineer_recycler_view.adapter = adapter
                group_many_items.visibility = View.VISIBLE
                group_single_item.visibility = View.GONE
                transfer_to_engineer_recycler_view.visibility = View.VISIBLE
                name_confirm_layout.visibility = View.GONE
            }
        } else {
            group_many_items.visibility = View.GONE
            group_single_item.visibility = View.VISIBLE
            transfer_to_engineer_recycler_view.visibility = View.GONE
            name_confirm_layout.visibility = View.VISIBLE
        }
        anotherEngineer.name?.let {
            if (it.isNotEmpty()) device_confirm_title.text = it
            if (it.isNotEmpty()) name_title.text = it
        }


        total_count.text = countItemsQuantity()

        transferItems[0].name?.let {
            text_title.text = it
        }

        transferItems[0].vendorCode?.let {
            art.text = "Арт: $it"
        }

        text_view_count.text = counter.toString()


        if (counter == quantity) {
            button_plus.isEnabled = false
        }
        if(counter == 1) {
            button_minus.isEnabled = false
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
                    items = Gson().toJson(transferItems)
                )
                (activity as ActionWithItemActivity).finish()
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }

        button_confirm.setOnClickListener {
            (activity as ActionWithItemActivity).showProgressBar()
            val movingItems = arrayListOf<MovingCreateRequest>()
            if(isSingleItem){
                val item = MovingCreateRequest()
                item.apply {
                    mcId = System.currentTimeMillis()
                    destinationId = anotherEngineer.warehouseId
                    destinationSide = 0
                    quantity = counter
                    skuId = transferItems[0].skuId
                }
                movingItems.add(item)
            } else {
                for (i in transferItems) {
                    val item = MovingCreateRequest()
                    item.apply {
                        mcId = System.currentTimeMillis()
                        destinationId = anotherEngineer.warehouseId
                        destinationSide = 0
                        quantity = i.quantity
                        skuId = i.skuId
                    }
                    movingItems.add(item)
                }
            }
            transferToEngineerConfirmViewModel.transferToEngineer(movingItems)
            Log.e("movingItems", Gson().toJson(movingItems))
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isScanned) {
                ScannerActivity.open(
                    context = requireContext(),
                    key = key,
                    items = Gson().toJson(transferItems)
                )
                (activity as ActionWithItemActivity).finish()
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }


    private fun observeViewModel(viewModel: TransferToEngineerConfirmViewModel) {
        viewModel.apply {
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as ActionWithItemActivity).hideProgressBar()
                (activity as ActionWithItemActivity).finish()
            })
            errorLiveData.observe(viewLifecycleOwner, Observer { errorResp ->
                (activity as ActionWithItemActivity).hideProgressBar()
                if (errorResp == 401) {
                    preferencesManager.apply {
                        isAuthCompleted = false
                        isTouchIdAdded = false
                        isPasscodeChanging = false
                        passcode = null
                    }
                    AuthorizationActivity.open(requireContext())
                    activity?.finishAffinity()
                } else if (errorResp == 400) {
                    Toast.makeText(context, "Error: Bad Request", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun countItemsQuantity(): String {
        var quantity = 0
        for (i in transferItems) {
            i.quantity?.let {
                quantity += it
            }
        }
        return quantity.toString()
    }

    companion object {
        const val KEY = "KEY"
        const val IS_SCANNED = "IS_SCANNED"
        const val TITLE = "TITLE"
        const val ITEMS = "ITEMS"
        const val QUANTITY = "QUANTITY"
        const val ENGINEER = "ENGINEER"
        const val TRANSFER_TO_ENGINEER = "TRANSFER_TO_ENGINEER"
    }
}
