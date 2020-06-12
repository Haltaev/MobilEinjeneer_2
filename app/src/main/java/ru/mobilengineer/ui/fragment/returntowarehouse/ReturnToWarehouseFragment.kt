package ru.mobilengineer.ui.fragment.returntowarehouse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_return_to_warehouse.*
import ru.mobilengineer.App
import ru.mobilengineer.OnTargetItemClickListener
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.response.MyStockResponse
import ru.mobilengineer.data.api.model.response.WarehousesResponse
import ru.mobilengineer.ui.activity.ActionWithItemActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.adapter.ReturnToWarehouseAdapter
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.WarehousesViewModel
import javax.inject.Inject

class ReturnToWarehouseFragment : BaseFragment(), OnTargetItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: ReturnToWarehouseAdapter? = null
    lateinit var warehousesViewModel: WarehousesViewModel

    var counter = 1
    var quantity = 0
    var key = ""
    var isSingleItem = true
    var items: ArrayList<MyStockResponse> = arrayListOf()
    var warehouses: ArrayList<WarehousesResponse> = arrayListOf()
    var warehousesForSearch: ArrayList<WarehousesResponse> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.fragment_return_to_warehouse
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        warehousesViewModel = injectViewModel(viewModelFactory)
        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
            items = Gson().fromJson(
                bundle.getString(ITEMS, "{}"),
                object : TypeToken<ArrayList<MyStockResponse>>() {}.type
            )
        }
        isSingleItem = (items.size == 1)
        if (isSingleItem) {
            items[0].quantity?.let {
                if (!items.isNullOrEmpty() && items.size == 1) {
                    counter = it
                    quantity = it
                }
            }
        } else{
            items.forEach{ item ->
                item.quantity?.let {
                    quantity += it
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (key == "WAREHOUSE") bar_name.text = "Склад"

        warehousesViewModel.getWarehouses()

        observeViewModel(warehousesViewModel)

        cartridges_selected_count.text = quantity.toString()

        if (isSingleItem) {
            group_single_item.visibility = View.VISIBLE
            group_many_items.visibility = View.GONE
        } else {
            group_many_items.visibility = View.VISIBLE
            group_single_item.visibility = View.GONE
        }

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
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                warehouses.forEach { item ->
                    item.name?.let {
                        if (it.contains(s.toString(), true)) {
                            warehousesForSearch.add(item)
                        }
                    }
                }
                if (s.toString().isNotEmpty()) {
                    setAdapter(warehousesForSearch)
                } else {
                    setAdapter(warehouses)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                warehousesForSearch.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        back_button.setOnClickListener {
            ScannerActivity.open(
                context = requireContext(),
                key = key,
                items = Gson().toJson(items)
            )
            (activity as ActionWithItemActivity).finish()
        }

        button_next.setOnClickListener {

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

    fun setAdapter(itemsWarehouses: ArrayList<WarehousesResponse>) {
        context?.let { ctx ->
            adapter = ReturnToWarehouseAdapter(
                itemsWarehouses,
                ctx,
                this
            )
            recycler_view_return_to_warehouse.layoutManager = LinearLayoutManager(ctx)
            recycler_view_return_to_warehouse.adapter = adapter
        }
    }


    private fun observeViewModel(viewModel: WarehousesViewModel) {
        viewModel.apply {
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as ActionWithItemActivity).hideProgressBar()
                warehouses = resp
                setAdapter(resp)
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
                    400 -> {
                        Toast.makeText(context, "Error: Bad Request", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            "Error: Check Internet connection",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            })
        }
    }

    override fun onTargetItemClicked(warehouse: WarehousesResponse) {
        (activity as ActionWithItemActivity).openReturnToWarehouseConfirmFragment(
            isScanned = false,
            title = Gson().toJson(warehouse),
            key = key,
            items = Gson().toJson(items),
            quantity = counter
        )
    }

    companion object {
        const val KEY = "KEY"
        const val ITEMS = "ITEMS"
    }
}
