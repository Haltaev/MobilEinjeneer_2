package ru.mobilengineer.ui.fragment.engineer

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
import kotlinx.android.synthetic.main.fragment_pick_engineer.*
import ru.mobilengineer.App
import ru.mobilengineer.OnNameEngineerItemClickListener
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.data.api.model.response.AnotherEngineersResponse
import ru.mobilengineer.data.api.model.response.MyStockResponse
import ru.mobilengineer.ui.activity.ActionWithItemActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.adapter.TransferToEngineerAdapter
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.TransferToEngineerViewModel
import javax.inject.Inject

class PickEngineerFragment : BaseFragment(), OnNameEngineerItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: TransferToEngineerAdapter? = null
    lateinit var warehousesViewModel: TransferToEngineerViewModel

    var key = ""
    var items: ArrayList<AnotherEngineersResponse> = arrayListOf()
    var itemsForSearch: ArrayList<AnotherEngineersResponse> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.fragment_pick_engineer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        warehousesViewModel = injectViewModel(viewModelFactory)
        val bundle = this.arguments
        if (bundle != null) {
            key = bundle.getString(KEY, "")
        }
    }

    fun setAdapter(itemsWarehouses: ArrayList<AnotherEngineersResponse>){
        context?.let { ctx ->
            adapter = TransferToEngineerAdapter(
                itemsWarehouses,
                ctx,
                this
            )
            recycler_view_pick_warehouse.layoutManager = LinearLayoutManager(ctx)
            recycler_view_pick_warehouse.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as ActionWithItemActivity).showProgressBar()

        warehousesViewModel.getAnotherEngineers()

        observeViewModel(warehousesViewModel)


        back_button.setOnClickListener {
            ScannerActivity.open(
                context = requireContext(),
                key = key,
                items = Gson().toJson(arrayListOf<MyStockResponse>())
            )
            (activity as ActionWithItemActivity).finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            ScannerActivity.open(
                context = requireContext(),
                key = key,
                items = ""
            )
            (activity as ActionWithItemActivity).finish()
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
    }
    private fun observeViewModel(viewModel: TransferToEngineerViewModel) {
        viewModel.apply {
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                setAdapter(resp)
                (activity as ActionWithItemActivity).hideProgressBar()
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

    override fun onNameEngineerItemClicked(anotherEngineers: AnotherEngineersResponse) {
        (activity as ActionWithItemActivity).openIncommingSkuFromEngineerFragment(
            isScanned = false,
            title  = Gson().toJson(anotherEngineers),
            key = key
        )
    }

    companion object {
        const val KEY = "KEY"
        const val ITEMS = "ITEMS"
    }
}