package ru.mobilengineer.ui.fragment.home

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_my_stock.*
import ru.mobilengineer.App
import ru.mobilengineer.OnHomeItemClickListener
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.data.api.model.response.MyStockResponse
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.MyProfileSettingsActivity
import ru.mobilengineer.ui.activity.MyStockActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.adapter.MyStockAdapter
import ru.mobilengineer.viewmodel.MyStockViewModel
import javax.inject.Inject

class MyStockFragment : Fragment(), OnHomeItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: MyStockAdapter? = null

    private var isAnyItemPicked = false

    lateinit var myStockViewModel: MyStockViewModel
    var items: ArrayList<MyStockResponse> = arrayListOf()
    var itemsForSearch: ArrayList<MyStockResponse> = arrayListOf()
    var itemsMap: MutableMap<Int, MyStockResponse> = mutableMapOf()

    var quantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        myStockViewModel = injectViewModel(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_stock, container, false)
    }

    override fun onResume() {
        super.onResume()
        group_selected.visibility = View.GONE
        observeViewModel(viewModel = myStockViewModel)

        if(preferencesManager.filterMode != null){
            preferencesManager.filterMode?.let {
                myStockViewModel.getMyStock(it)
            }
        } else {
            myStockViewModel.getMyStock("n")
        }

        (activity as MyStockActivity).showProgressBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_cancel_select.setOnClickListener {
            items.forEach {
                it.isPicked = false
            }
//            adapter?.notifyDataSetChanged()
            setAdapter(items)
            group_selected.visibility = View.GONE
        }

        back_button.setOnClickListener {
            (activity as MyStockActivity).finish()
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

        button_return_to_warehouse.setOnClickListener {
            ScannerActivity.open(
                context = requireContext(),
                items = Gson().toJson(mapToArrayList(itemsMap)),
                key = ScannerActivity.RETURN_TO_WAREHOUSE
            )
        }

        button_take_away.setOnClickListener {
            ScannerActivity.open(
                context = requireContext(),
                items = Gson().toJson(mapToArrayList(itemsMap)),
                key = ScannerActivity.TRANSFER_TO_ENGINEER
            )
        }

        filter_button.setOnClickListener {
            (activity as MyStockActivity).openFiltersFragment()
        }

        profile_button.setOnClickListener {
            MyProfileSettingsActivity.open(requireContext())
        }
    }

    fun mapToArrayList(map: MutableMap<Int, MyStockResponse>): ArrayList<MyStockResponse> {
        val arrayList: ArrayList<MyStockResponse> = arrayListOf()
        for (i in map.keys) {
            if (map[i] != null) {
                arrayList.add(map[i]!!)
            }
        }
        return arrayList
    }

    private fun observeViewModel(viewModel: MyStockViewModel) {
        viewModel.apply {
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                var quantity = 0
                items = resp
                setAdapter(resp)
                resp.forEach { item ->
                    item.quantity?.let {
                        quantity += it
                    }
                }
                count_warehouse.text = resources.getString(R.string._count, quantity.toString())
                (activity as MyStockActivity).hideProgressBar()
            })
            deleteLiveData.observe(viewLifecycleOwner, Observer { resp ->
                if(preferencesManager.filterMode != null){
                    preferencesManager.filterMode?.let {
                        myStockViewModel.getMyStock(it)
                    }
                } else {
                    myStockViewModel.getMyStock("n")
                }
            })
            errorLiveData.observe(viewLifecycleOwner, Observer { errorResp ->
                (activity as MyStockActivity).hideProgressBar()
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
                        ).show()
                    }
                }
            })
        }
    }

    private fun setAdapter(warehouses: ArrayList<MyStockResponse>) {
        context?.let { ctx ->
            adapter = MyStockAdapter(
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

    override fun onWarehouseItemClicked(item: IncommingSkuFromAnyResponse, position: Int) {
        (activity as MyStockActivity).showProgressBar()
        myStockViewModel.cancelTransfer(item.movingId.toString())
    }
}