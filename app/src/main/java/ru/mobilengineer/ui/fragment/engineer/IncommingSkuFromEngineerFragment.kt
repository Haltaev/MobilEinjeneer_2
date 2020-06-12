package ru.mobilengineer.ui.fragment.engineer

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_engineer_items_list.*
import ru.mobilengineer.App
import ru.mobilengineer.OnIncommingSkuClickListener
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.response.AnotherEngineersResponse
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.data.api.model.response.WarehousesResponse
import ru.mobilengineer.ui.activity.ActionWithItemActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.adapter.ItemsAdapter
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.IncommingSkuFromEngimeerViewModel
import javax.inject.Inject

class IncommingSkuFromEngineerFragment : BaseFragment(), OnIncommingSkuClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: ItemsAdapter? = null

    private var isAnyItemPicked = false

    var engineer = AnotherEngineersResponse()
    var key = ""
    private var isScanned = true

    lateinit var warehousesViewModel: IncommingSkuFromEngimeerViewModel
    var warehouses: ArrayList<IncommingSkuFromAnyResponse> = arrayListOf()
    var warehousesForSearch: ArrayList<IncommingSkuFromAnyResponse> = arrayListOf()
    var items: MutableMap<Int, IncommingSkuFromAnyResponse> = mutableMapOf()
    var quantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        warehousesViewModel = injectViewModel(viewModelFactory)

        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
            engineer = Gson().fromJson(
                bundle.getString(TITLE, ""),
                object : TypeToken<AnotherEngineersResponse>() {}.type
            )
            isScanned = bundle.getBoolean(IS_SCANNED, true)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_engineer_items_list
    }

    override fun onResume() {
        super.onResume()
        group_selected.visibility = View.GONE
        (activity as ActionWithItemActivity).showProgressBar()

        warehousesViewModel.getIncommingSkuFromWarehouse(engineer)

        observeViewModel(viewModel = warehousesViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_cancel_select.setOnClickListener {
            warehouses.forEach {
                it.isPicked = false
            }
//            adapter?.notifyDataSetChanged()
            setAdapter(warehouses)
            group_selected.visibility = View.GONE
        }

        back_button.setOnClickListener {
            if (isScanned) {
                ScannerActivity.open(
                    context = requireContext(),
                    key = key,
                    items = Gson().toJson(mapToArrayList(items))
                )
                (activity as ActionWithItemActivity).finish()
            } else {
                activity?.supportFragmentManager?.popBackStack()
            }
        }

        button_take_away.setOnClickListener {
            (activity as ActionWithItemActivity).openTransferFromEngineerFragment(
                isScanned = false,
                anotherEngineers = Gson().toJson(engineer),
                key = key,
                items = Gson().toJson(mapToArrayList(items))
            )
            Log.e("array", Gson().toJson(mapToArrayList(items)))
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
    }

    fun mapToArrayList(map: MutableMap<Int, IncommingSkuFromAnyResponse>): ArrayList<IncommingSkuFromAnyResponse> {
        val arrayList: ArrayList<IncommingSkuFromAnyResponse> = arrayListOf()
        for (i in map.keys) {
            if (map[i] != null) {
                arrayList.add(map[i]!!)
            }
        }
        return arrayList
    }

    private fun observeViewModel(viewModel: IncommingSkuFromEngimeerViewModel) {
        viewModel.apply {
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as ActionWithItemActivity).hideProgressBar()
                warehouses = resp
                setAdapter(resp)
                var quantity = 0
                resp.forEach { item ->
                    item.quantity?.let {
                        quantity += it
                    }
                }
                count_warehouse.text = resources.getString(R.string._count, quantity.toString())
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
                    else -> { }
                }
            })
        }
    }

    private fun setAdapter(warehouses: ArrayList<IncommingSkuFromAnyResponse>) {
        context?.let { ctx ->
            adapter = ItemsAdapter(
                warehouses,
                ctx,
                this
            )

            recycler_view_warehouse_items.layoutManager = LinearLayoutManager(ctx)
            recycler_view_warehouse_items.adapter = adapter
        }
    }

    override fun onHomeItemClicked(count: Int?, position: Int, isIncreased: Boolean) {
        if (isIncreased) {
            items[position] = warehouses[position]
            count?.let {
                quantity += it
            }
        } else {
            items.remove(position)
            count?.let {
                quantity -= it
            }
        }
        count_selected.text = getString(R.string._count, quantity.toString())
    }

    override fun onHomeItemLongClicked(isAnyItemPicked: Boolean, position: Int) {
        quantity = 0
        items = mutableMapOf()
        if (position == 0) recycler_view_warehouse_items.scrollToPosition(position)
        this.isAnyItemPicked = isAnyItemPicked
        if (isAnyItemPicked) {
            group_selected.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                et_search.focusable = View.NOT_FOCUSABLE
            }
        }
        else {
            group_selected.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                et_search.focusable = View.FOCUSABLE
            }
        }
    }

    override fun isConsiderSerialNumber(): Boolean {
        return preferencesManager.isConsiderSerialNumber
    }

    override fun onTakeAwayButtonClicked(item: IncommingSkuFromAnyResponse, position: Int) {
        items[position] = item
        (activity as ActionWithItemActivity).openTransferFromEngineerFragment(
            isScanned = false,
            anotherEngineers = Gson().toJson(engineer),
            key = key,
            items = Gson().toJson(mapToArrayList(items))
        )
        Log.e("array", Gson().toJson(mapToArrayList(items)))
    }

    companion object {
        const val KEY = "KEY"
        const val IS_SCANNED = "IS_SCANNED"
        const val TITLE = "TITLE"
        const val ITEMS = "ITEMS"
    }

}