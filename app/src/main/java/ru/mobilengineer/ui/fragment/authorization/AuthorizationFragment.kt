package ru.mobilengineer.ui.fragment.authorization

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_auth.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.AuthorizationViewModel
import javax.inject.Inject


class AuthorizationFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    lateinit var authorizationViewModel: AuthorizationViewModel


    override fun getLayoutId(): Int {
        return R.layout.fragment_auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        authorizationViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel(viewModel = authorizationViewModel)

        auth_ok.setOnClickListener {
            (activity as AuthorizationActivity).showProgressBar()
            authorizationViewModel.authorization(
                et_login.text.toString(),
                et_password.text.toString()
            )
        }

        et_password.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                (activity as AuthorizationActivity).showProgressBar()
                authorizationViewModel.authorization(
                    et_login.text.toString(),
                    et_password.text.toString()
                )

                return@OnEditorActionListener true
            }
            false
        })
    }


    private fun observeViewModel(viewModel: AuthorizationViewModel) {
        viewModel.apply {
            dataLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as AuthorizationActivity).hideProgressBar()
                preferencesManager.token = resp.token
                (activity as AuthorizationActivity).openCodeFragment()
            })
            errorLiveData.observe(viewLifecycleOwner, Observer { errorResp ->
                if (errorResp == 400) {
                    (activity as AuthorizationActivity).hideProgressBar()
                    auth_error_massage.visibility = View.VISIBLE
                } else {
                    (activity as AuthorizationActivity).hideProgressBar()
                    Toast.makeText(context, "Error: Check Internet connection", Toast.LENGTH_LONG)
                        .show()
                }
            })
        }
    }

    companion object {
        const val REQUEST_TAKE_PHOTO = 1
        const val DATA = "data"
    }
}