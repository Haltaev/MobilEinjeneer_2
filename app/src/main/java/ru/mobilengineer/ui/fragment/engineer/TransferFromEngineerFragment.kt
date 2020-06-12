package ru.mobilengineer.ui.fragment.transfertoengineer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_transfer_from_engineer.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.request.MovingAcceptRequest
import ru.mobilengineer.data.api.model.response.AnotherEngineersResponse
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.ui.activity.ActionWithItemActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.adapter.IncommingItemsAdapter
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.TransferFromEngineerViewModel
import javax.inject.Inject


class TransferFromEngineerFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: IncommingItemsAdapter? = null

    lateinit var transferToEngineerConfirmViewModel: TransferFromEngineerViewModel

    var counter = 1
    var quantity = 0
    var key = ""
    var anotherEngineer: AnotherEngineersResponse = AnotherEngineersResponse()
    var isSingleItem = true
    var isScanned = true
    var incommingItems: ArrayList<IncommingSkuFromAnyResponse> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.fragment_transfer_from_engineer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        transferToEngineerConfirmViewModel = injectViewModel(viewModelFactory)

        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
            incommingItems = Gson().fromJson(
                bundle.getString(ITEMS, "{}"),
                object : TypeToken<ArrayList<IncommingSkuFromAnyResponse>>() {}.type
            )
            anotherEngineer = Gson().fromJson(
                bundle.getString(TITLE, ""),
                object : TypeToken<AnotherEngineersResponse>() {}.type
            )

            isScanned = bundle.getBoolean(IS_SCANNED, true)

        }
        isSingleItem = (incommingItems.size == 1)
        incommingItems[0].quantity?.let {
            if (!incommingItems.isNullOrEmpty() && incommingItems.size == 1) {
                counter = it
                quantity = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel(transferToEngineerConfirmViewModel)

        if (incommingItems.size > 1) {
            context?.let { ctx ->
                adapter = IncommingItemsAdapter(
                    incommingItems,
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

        incommingItems[0].name?.let {
            text_title.text = it
        }

        incommingItems[0].vendorCode?.let {
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
                    items = Gson().toJson(incommingItems)
                )
                (activity as ActionWithItemActivity).finish()
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }

        button_confirm.setOnClickListener {
            (activity as ActionWithItemActivity).showProgressBar()
            val movingItems = arrayListOf<Pair<Int, MovingAcceptRequest>>()
            if (incommingItems.size > 1) {
                for (i in incommingItems) {
                    val item = MovingAcceptRequest()
                    item.apply {
                        mcId = System.currentTimeMillis()
                        destinationId = i.targetWarehouseId
                        destinationSide = 0
                        quantity = i.quantity
                        skuId = i.skuId
                    }
                    i.movingId?.let {
                        movingItems.add(Pair(it, item))
                    }
                }
                transferToEngineerConfirmViewModel.putItemsToMyWarehouse(movingItems)
            } else {
                val item = MovingAcceptRequest()
                item.apply {
                    mcId = System.currentTimeMillis()
                    destinationId = incommingItems[0].targetWarehouseId
                    destinationSide = 0
                    quantity = counter
                    skuId = incommingItems[0].skuId
                }
                incommingItems[0].movingId?.let {
                    movingItems.add(Pair(it, item))
                }
                transferToEngineerConfirmViewModel.putItemsToMyWarehouse(movingItems)

            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isScanned) {
                ScannerActivity.open(
                    context = requireContext(),
                    key = key,
                    items = Gson().toJson(incommingItems)
                )
                (activity as ActionWithItemActivity).finish()
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }


    private fun observeViewModel(viewModel: TransferFromEngineerViewModel) {
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
        for (i in incommingItems) {
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
        const val ENGINEER = "ENGINEER"
        const val TRANSFER_TO_ENGINEER = "TRANSFER_TO_ENGINEER"
    }
}
