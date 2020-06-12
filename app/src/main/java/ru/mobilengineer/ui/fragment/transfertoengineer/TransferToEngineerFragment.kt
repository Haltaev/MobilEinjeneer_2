package ru.mobilengineer.ui.fragment.transfertoengineer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_return_to_warehouse.*
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.*
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.art
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.back_button
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.button_minus
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.button_next
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.button_plus
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.cartridges_selected_count
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.et_search
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.group_many_items
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.group_single_item
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.text_title
import kotlinx.android.synthetic.main.fragment_transfer_to_engineer.text_view_count
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


class TransferToEngineerFragment : BaseFragment(), OnNameEngineerItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: TransferToEngineerAdapter? = null


    lateinit var anotherEngineersViewModel: TransferToEngineerViewModel

    var counter = 1
    var quantity = 0
    var key = ""
    var isSingleItem = true
    var items: ArrayList<MyStockResponse> = arrayListOf()
    var engineers: ArrayList<AnotherEngineersResponse> = arrayListOf()
    var engineersForSearch: ArrayList<AnotherEngineersResponse> = arrayListOf()

    override fun getLayoutId(): Int {
        return R.layout.fragment_transfer_to_engineer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        App.getComponent().inject(this)
        anotherEngineersViewModel = injectViewModel(viewModelFactory)
        val bundle = this.arguments
        if (bundle != null) {
            items = Gson().fromJson(
                bundle.getString(ITEMS, "{}"),
                object : TypeToken<ArrayList<MyStockResponse>>() {}.type
            )
            key = bundle.getString(KEY, "")
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

        (activity as ActionWithItemActivity).showProgressBar()
        anotherEngineersViewModel.getAnotherEngineers()

        observeViewModel(viewModel = anotherEngineersViewModel)


        items[0].name?.let {
            text_title.text = it
        }

        items[0].vendorCode?.let {
            art.text = "Арт: $it"
        }

        text_view_count.text = counter.toString()

        cartridges_selected_count.text = quantity.toString()

        if (isSingleItem) {
            group_single_item.visibility = View.VISIBLE
            group_many_items.visibility = View.GONE
        } else {
            group_many_items.visibility = View.VISIBLE
            group_single_item.visibility = View.GONE
        }

        if (counter == quantity) {
            button_plus.isEnabled = false
        }
        if (counter == 1) {
            button_minus.isEnabled = false
        }

        button_plus.setOnClickListener {
            text_view_count.text = (++counter).toString()
            button_minus.isEnabled = true
            if (counter >= quantity.toInt()) {
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
            ScannerActivity.open(
                context = requireContext(),
                key = key,
                items = Gson().toJson(items)
            )
            (activity as ActionWithItemActivity).finish()
        }

        button_next.setOnClickListener {

        }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                engineers.forEach { item ->
                    item.name?.let {
                        if (it.contains(s.toString(), true)) {
                            engineersForSearch.add(item)
                        }
                    }
                }
                if (s.toString().isNotEmpty()) {
                    setAdapter(engineersForSearch)
                } else {
                    setAdapter(engineers)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                engineersForSearch.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            ScannerActivity.open(
                context = requireContext(),
                key = key,
                items = Gson().toJson(items)
            )
            (activity as ActionWithItemActivity).finish()
        }
    }

    fun setAdapter(resp: ArrayList<AnotherEngineersResponse>) {
        context?.let { ctx ->
            adapter = TransferToEngineerAdapter(
                resp,
                ctx,
                this
            )
            recycler_view_transfer_to_engineer.layoutManager = LinearLayoutManager(ctx)
            recycler_view_transfer_to_engineer.adapter = adapter
        }
    }

    private fun observeViewModel(viewModel: TransferToEngineerViewModel) {
        viewModel.apply {
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as ActionWithItemActivity).hideProgressBar()
                engineers = resp
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
                        ).show()
                    }
                }
            })
        }
    }

    override fun onNameEngineerItemClicked(anotherEngineers: AnotherEngineersResponse) {
        (activity as ActionWithItemActivity).openTransferToEngineerConfirmFragment(
            isScanned = false,
            anotherEngineers = Gson().toJson(anotherEngineers),
            key = key,
            items = Gson().toJson(items),
            quantity = counter
        )
        Log.e("anotherEngineers", Gson().toJson(items))
    }

    companion object {
        const val KEY = "KEY"
        const val ITEMS = "ITEMS"
    }
}