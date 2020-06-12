package ru.mobilengineer.ui.fragment.available

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_available_warehouse.*
import ru.mobilengineer.App
import ru.mobilengineer.OnAvailableWarehouseClickListener
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.request.MovingAcceptRequest
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.AvailableWarehouseActivity
import ru.mobilengineer.ui.activity.MyProfileSettingsActivity
import ru.mobilengineer.ui.adapter.AvailableWarehouseAdapter
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.AvailableWarehouseViewModel
import javax.inject.Inject

class AvailableWarehouseFragment : BaseFragment(), OnAvailableWarehouseClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: AvailableWarehouseAdapter? = null
    lateinit var availableWarehouseViewModel: AvailableWarehouseViewModel
    var items: ArrayList<IncommingSkuFromAnyResponse> = arrayListOf()
    var itemsForSearch: ArrayList<IncommingSkuFromAnyResponse> = arrayListOf()
    var itemsMap: MutableMap<Int, IncommingSkuFromAnyResponse> = mutableMapOf()
    var quantity = 0
    private var isAnyItemPicked = false
    var key = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        availableWarehouseViewModel = injectViewModel(viewModelFactory)
        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_available_warehouse
    }

    override fun onResume() {
        super.onResume()
        group_selected.visibility = View.GONE
        (activity as AvailableWarehouseActivity).showProgressBar()

        observeViewModel(viewModel = availableWarehouseViewModel)

        getItems()

        (activity as AvailableWarehouseActivity).showProgressBar()
    }

    fun getItems(){
        if (preferencesManager.filterMode != null) {
            preferencesManager.filterMode?.let {
                availableWarehouseViewModel.getIncommingSkuFromAll(it, key)
            }
        } else {
            availableWarehouseViewModel.getIncommingSkuFromAll("n", key)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_cancel_select.setOnClickListener {
            items.forEach {
                it.isPicked = false
            }
            setAdapter(items)
            group_selected.visibility = View.GONE
        }

        profile_button.setOnClickListener {
            MyProfileSettingsActivity.open(requireContext())
        }

        button_refuse.setOnClickListener {
            val itemForRefuse: ArrayList<IncommingSkuFromAnyResponse> = arrayListOf()
            for (i in items) {
                if (i.isPicked) itemForRefuse.add(i)
            }
            val movingItems = arrayListOf<Pair<Int, MovingAcceptRequest>>()
            for (i in itemForRefuse) {
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
            availableWarehouseViewModel.putItemsToMyWarehouse(movingItems)

        }

        button_receive.setOnClickListener {
            val itemForRecive: ArrayList<IncommingSkuFromAnyResponse> = arrayListOf()
            for (i in items) {
                if (i.isPicked) itemForRecive.add(i)
            }
            val listOfMovingId = arrayListOf<String>()
            for (item in itemForRecive) {
                listOfMovingId.add(item.movingId.toString())
            }
            availableWarehouseViewModel.cancelTransfer(listOfMovingId)
        }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                items.forEach { item ->
                    item.name?.let {
                        if (it.contains(s.toString(), true)) {
                            itemsForSearch.add(item)
                        }
                    }
                }
                if (s.toString().isNotEmpty()) {
                    setAdapter(itemsForSearch)
                } else {
                    setAdapter(items)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                itemsForSearch.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        filter_button.setOnClickListener {
            (activity as AvailableWarehouseActivity).openFiltersFragment()
        }

        back_button.setOnClickListener {
            (activity as AvailableWarehouseActivity).finish()
        }
    }

    fun setAdapter(arrayItems: ArrayList<IncommingSkuFromAnyResponse>) {
        context?.let { ctx ->
            adapter = AvailableWarehouseAdapter(
                arrayItems,
                ctx,
                this
            )
            available_warehouse_recycler_view.layoutManager = LinearLayoutManager(ctx)
            available_warehouse_recycler_view.adapter = adapter
        }
    }

    private fun observeViewModel(viewModel: AvailableWarehouseViewModel) {
        viewModel.apply {
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                var quantity = 0
                items = resp
                setAdapter(items)
                items.forEach { item ->
                    item.quantity?.let {
                        quantity += it
                    }
                }
                count_warehouse.text = resources.getString(R.string._count, quantity.toString())
                (activity as AvailableWarehouseActivity).hideProgressBar()
            })
            movingAcceptLiveData.observe(viewLifecycleOwner, Observer { resp ->
                group_selected.visibility = View.GONE
                getItems()
            })
            cancelTransferLiveData.observe(viewLifecycleOwner, Observer { resp ->
                group_selected.visibility = View.GONE
                getItems()
            })
            errorLiveData.observe(viewLifecycleOwner, Observer { errorResp ->
                (activity as AvailableWarehouseActivity).hideProgressBar()
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
                    }
                }
            })
        }
    }

    override fun onHomeItemClicked(count: Int?, position: Int, isIncreased: Boolean) {
        if (isIncreased) {
            itemsMap[position] = items[position]
            count?.let {
                quantity += it
            }
        } else {
            itemsMap.remove(position)
            count?.let {
                quantity -= it
            }
        }
        count_selected.text = getString(R.string._count, quantity.toString())
    }

    override fun onHomeItemLongClicked(isAnyItemPicked: Boolean, position: Int) {
        quantity = 0
        itemsMap = mutableMapOf()
        if (position == 0) available_warehouse_recycler_view.scrollToPosition(position)
        this.isAnyItemPicked = isAnyItemPicked
        if (isAnyItemPicked) {
            group_selected.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                et_search.focusable = View.NOT_FOCUSABLE
            }
        } else {
            group_selected.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                et_search.focusable = View.FOCUSABLE
            }
        }
    }

    override fun isConsiderSerialNumber(): Boolean {
        return preferencesManager.isConsiderSerialNumber
    }

    override fun onItemRefuseClicked(item: IncommingSkuFromAnyResponse, position: Int) {
        (activity as AvailableWarehouseActivity).showProgressBar()
        availableWarehouseViewModel.cancelTransfer(arrayListOf(item.movingId.toString()))
    }

    override fun onItemReceiveClicked(incommingItem: IncommingSkuFromAnyResponse, position: Int) {
        (activity as AvailableWarehouseActivity).showProgressBar()
        val item = MovingAcceptRequest()
        item.apply {
            mcId = System.currentTimeMillis()
            destinationId = incommingItem.targetWarehouseId
            destinationSide = 0
            quantity = incommingItem.quantity
            skuId = incommingItem.skuId
        }
        incommingItem.movingId?.let {
            availableWarehouseViewModel.putItemsToMyWarehouse(arrayListOf(Pair(it, item)))
        }

    }

    companion object{
        const val KEY = "KEY"
        const val FROM_ENGINEER = "FROM_ENGINEER"
        const val FROM_WAREHOUSE = "FROM_WAREHOUSE"
    }

}