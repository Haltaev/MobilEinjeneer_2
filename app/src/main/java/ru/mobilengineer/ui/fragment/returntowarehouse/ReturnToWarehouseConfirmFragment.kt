package ru.mobilengineer.ui.fragment.returntowarehouse

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_return_to_warehouse_confirm.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.request.MovingCreateRequest
import ru.mobilengineer.data.api.model.response.MyStockResponse
import ru.mobilengineer.data.api.model.response.WarehousesResponse
import ru.mobilengineer.ui.activity.ActionWithItemActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.adapter.OutGoingItemsAdapter
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.ReturnToWarehouseConfirmViewModel
import javax.inject.Inject

class ReturnToWarehouseConfirmFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: OutGoingItemsAdapter? = null
    lateinit var returnToWarehouseConfirmViewModel: ReturnToWarehouseConfirmViewModel

    private var counter = 0
    var quantity = 0
    var key = ""
    var warehouse = WarehousesResponse()
    private var isScanned = true
    var isSingleItem = true
    var items: ArrayList<MyStockResponse> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.fragment_return_to_warehouse_confirm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        returnToWarehouseConfirmViewModel = injectViewModel(viewModelFactory)

        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
            warehouse = Gson().fromJson(
                bundle.getString(TITLE, "{}"),
                object : TypeToken<WarehousesResponse>() {}.type
            )
            isScanned = bundle.getBoolean(IS_SCANNED, true)
            items = Gson().fromJson(
                bundle.getString(ITEMS, "[{}]"),
                object : TypeToken<ArrayList<MyStockResponse>>() {}.type
            )

            counter = bundle.getInt(QUANTITY)
        }
        isSingleItem = (items.size == 1)
        items[0].quantity?.let {
            if (!items.isNullOrEmpty() && items.size == 1) {
                quantity = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel(returnToWarehouseConfirmViewModel)

        if (key == "WAREHOUSE") bar_name.text = "Склад"

        if (items.size > 1) {
            context?.let { ctx ->
                adapter = OutGoingItemsAdapter(
                    items,
                    ctx
                )
                return_to_warehouse_recycler_view.layoutManager = LinearLayoutManager(ctx)
                return_to_warehouse_recycler_view.adapter = adapter
                group_many_items.visibility = View.VISIBLE
                group_single_item.visibility = View.GONE
                return_to_warehouse_recycler_view.visibility = View.VISIBLE
                warehouse_confirm_ic.visibility = View.GONE
                warehouse_confirm_titles.visibility = View.GONE
            }
        } else {
            group_many_items.visibility = View.GONE
            group_single_item.visibility = View.VISIBLE
            return_to_warehouse_recycler_view.visibility = View.GONE
            warehouse_confirm_ic.visibility = View.VISIBLE
            warehouse_confirm_titles.visibility = View.VISIBLE
        }

        warehouse.name?.let {
            if (it.isNotEmpty()) warehouse_confirm_title.text = it
            if (it.isNotEmpty()) warehouse_title.text = it
        }
        total_count.text = countItemsQuantity()


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
                    items = Gson().toJson(items)
                )
                (activity as ActionWithItemActivity).finish()
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }


        button_confirm.setOnClickListener {
            (activity as ActionWithItemActivity).showProgressBar()
            val movingItems = arrayListOf<MovingCreateRequest>()
            for (i in items) {
                val item = MovingCreateRequest()
                item.apply {
                    mcId = System.currentTimeMillis()
                    destinationId = warehouse.id
                    destinationSide = 0
                    quantity = counter
                    skuId = i.skuId
                }
                movingItems.add(item)
            }
            returnToWarehouseConfirmViewModel.transferToEngineer(movingItems)
//            (activity as ActionWithItemActivity).finish()
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

    private fun observeViewModel(viewModel: ReturnToWarehouseConfirmViewModel) {
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
        for (i in items) {
            i.quantity?.let {
                quantity += it
            }
        }
        return quantity.toString()
    }

    companion object {
        const val RETURN_TO_WAREHOUSE = "RETURN_TO_WAREHOUSE"
        const val WAREHOUSE = "WAREHOUSE"
        const val KEY = "KEY"
        const val IS_SCANNED = "IS_SCANNED"
        const val TITLE = "TITLE"
        const val ITEMS = "ITEMS"
        const val QUANTITY = "QUANTITY"
    }
}